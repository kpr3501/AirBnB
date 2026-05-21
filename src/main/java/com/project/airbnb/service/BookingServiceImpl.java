package com.project.airbnb.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.airbnb.dto.BookingDto;
import com.project.airbnb.dto.BookingRequest;
import com.project.airbnb.dto.GuestDto;
import com.project.airbnb.dto.HotelReportDto;
import com.project.airbnb.entity.Booking;
import com.project.airbnb.entity.Guest;
import com.project.airbnb.entity.Hotel;
import com.project.airbnb.entity.Inventory;
import com.project.airbnb.entity.Room;
import com.project.airbnb.entity.User;
import com.project.airbnb.entity.enums.BookingStatus;
import com.project.airbnb.exception.ResourceNotFoundException;
import com.project.airbnb.exception.UnauthorizedException;
import com.project.airbnb.repository.BookingRepository;
import com.project.airbnb.repository.GuestRepository;
import com.project.airbnb.repository.HotelRepository;
import com.project.airbnb.repository.InventoryRepository;
import com.project.airbnb.repository.RoomRepository;
import com.project.airbnb.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository   inventoryRepository;
    private final ModelMapper modelMapper;
    private final GuestRepository guestRepository;  
    private final CheckoutService checkoutService;
    private final PricingService pricingService;

    @org.springframework.beans.factory.annotation.Value("${frontend.url}")
    private String frontendUrl;
    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {
        log.info("Initialising booking for hotel ID: {}, room ID: {}, check-in date: {}, check-out date: {}, rooms count: {}",
                bookingRequest.getHotelId(), bookingRequest.getRoomId(), bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());
        
        // Validate the booking request
        // Check room availability for the specified dates
        // Create a new booking with status "RESERVED"
        // Return the booking details in the response
        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(() -> {
            log.error("Hotel not found with ID: {}", bookingRequest.getHotelId());
            return new RuntimeException("Hotel not found with ID: " + bookingRequest.getHotelId());
        });

        Room room = roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(() -> {
            log.error("Room not found with ID: {}", bookingRequest.getRoomId());
            return new RuntimeException("Room not found with ID: " + bookingRequest.getRoomId());
        });

        Long dateCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());

        List<Inventory> availableInventory = inventoryRepository.findAndLockAvailableInventory(
            bookingRequest.getRoomId(),
            bookingRequest.getCheckInDate(),
            bookingRequest.getCheckOutDate(),
            bookingRequest.getRoomsCount()
        );

        if(availableInventory.size() < dateCount) {
            log.warn("Not enough inventory available for hotel ID: {}, room ID: {}, check-in date: {}, check-out date: {}, rooms count: {}",
                    bookingRequest.getHotelId(), bookingRequest.getRoomId(), bookingRequest.getCheckInDate(),
                    bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());
            throw new RuntimeException("Not enough inventory available for the selected dates");
        }
        for(Inventory inventory : availableInventory) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
            inventoryRepository.save(inventory);
        }
        inventoryRepository.initBooking(room.getId(), bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(availableInventory);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));
        

        Booking booking = Booking.builder()
            .hotel(hotel)
            .room(room)
            .checkInDate(bookingRequest.getCheckInDate())
            .checkOutDate(bookingRequest.getCheckOutDate())
            .roomsCount(bookingRequest.getRoomsCount())
            .bookingStatus(BookingStatus.RESERVED)
            .user(getCurrentUser())
            .amount(totalPrice) // Placeholder for actual user ID, should be fetched from authenticated user context
            .build();

        BookingDto bookingDto = modelMapper.map(bookingRepository.save(booking), BookingDto.class);
        return bookingDto; // Placeholder for actual booking details
    }
    @Override
    @Transactional
    public BookingDto addGuestsToBooking(List<GuestDto> listOfGuests, Long bookingId) {
        // TODO Auto-generated method stub
        log.info("Adding guests to booking ID: {}, guests count: {}", bookingId, listOfGuests.size());
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.error("Booking not found with ID: {}", bookingId);
            return new RuntimeException("Booking not found with ID: " + bookingId);
        });

        User user = getCurrentUser();
        if(! user.equals(booking.getUser())){
            throw new UnauthorizedException("Booking doesnot belogs to current user");
        }

        if (hasBookingExpired(booking)){
            log.warn("Cannot add guests to expired booking ID: {}", bookingId);
            throw new IllegalStateException("Cannot add guests to expired booking");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED) {
            log.warn("Cannot add guests to booking ID: {} with status: {}", bookingId, booking.getBookingStatus());
            throw new IllegalStateException("Can only add guests to bookings with RESERVED status");
        }

        listOfGuests.forEach(guestDto -> {Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(user);
            guest=guestRepository.save(guest);
            booking.getGuests().add(guest);
            });
        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        bookingRepository.save(booking);
        log.info("Successfully added guests to booking ID: {}", bookingId);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {
        // TODO Auto-generated method stub
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.error("Booking not found with ID: {}", bookingId);
            return new ResourceNotFoundException("Booking not found with ID: " + bookingId);
        });
                User user = getCurrentUser();
        if(! user.equals(booking.getUser())){
            throw new UnauthorizedException("Booking doesnot belogs to current user");
        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired please initiate new booking");
        }
        String sessionUrl = checkoutService.getCheoutSession(booking, frontendUrl+ "/payment-success" , frontendUrl + "/payment-failure");
        booking.setBookingStatus(BookingStatus.PAYMENTS_PENDING);
        bookingRepository.save(booking);
        return sessionUrl;
    }
    @Override
    @Transactional
    public void capturePayment(Event event) {
       if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) return;

            String sessionId = session.getId();
            Booking booking =
                    bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() ->
                            new ResourceNotFoundException("Booking not found for session ID: "+sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomsCount());

            inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomsCount());

            log.info("Successfully confirmed the booking for Booking ID: {}", booking.getId());
        } else {
            log.warn("Unhandled event type: {}", event.getType());
        }
    }

       @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnauthorizedException("Booking does not belong to this user with id: "+user.getId());
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        inventoryRepository.cancelBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        // handle the refund

        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new AccessDeniedException("Booking does not belong to this user with id: "+user.getId());
        }

        return booking.getBookingStatus().name();
    }

    @Override
    public List<BookingDto> getAllBookingsForHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not " +
                "found with ID: "+hotelId));
        User user = getCurrentUser();

        log.info("Getting all booking for the hotel with ID: {}", hotelId);

        if(!user.equals(hotel.getOwner())) throw new AccessDeniedException("You are not the owner of hotel with id: "+hotelId);

        List<Booking> bookings = bookingRepository.findByHotel(hotel);

        return bookings.stream()
                .map((element) -> modelMapper.map(element, BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public HotelReportDto generateHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not " +
                "found with ID: "+hotelId));
        User user = getCurrentUser();

        log.info("Generating report for hotel with ID: {}", hotelId);

        if(!user.equals(hotel.getOwner())) throw new AccessDeniedException("You are not the owner of hotel with id: "+hotelId);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByHotelAndCreatedAtBetween(hotel, startDateTime, endDateTime);

        Long totalConfirmedBookings = bookings
                .stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        BigDecimal totalRevenueOfConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgRevenue = totalConfirmedBookings == 0 ? BigDecimal.ZERO :
                totalRevenueOfConfirmedBookings.divide(BigDecimal.valueOf(totalConfirmedBookings), RoundingMode.HALF_UP);

        return new HotelReportDto(totalConfirmedBookings, totalRevenueOfConfirmedBookings, avgRevenue);
    }

    // @Override
    // public List<BookingDto> getMyBookings() {
    //     User user = getCurrentUser();

    //     return bookingRepository.findByUser(user)
    //             .stream().
    //             map((element) -> modelMapper.map(element, BookingDto.class))
    //             .collect(Collectors.toList());
    // }

    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    @Override
    public List<BookingDto> getBookingsForCurrentUser() {
        User user = getCurrentUser();

        return bookingRepository.findByUser(user)
                .stream()
                .map((element) -> modelMapper.map(element, BookingDto.class))
                .collect(Collectors.toList());
    }


}
