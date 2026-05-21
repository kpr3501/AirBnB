package com.project.airbnb.service;

import java.time.LocalDate;
import java.util.List;


import com.project.airbnb.dto.BookingDto;
import com.project.airbnb.dto.BookingRequest;
import com.project.airbnb.dto.GuestDto;
import com.project.airbnb.dto.HotelReportDto;
import com.stripe.model.Event;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuestsToBooking(List<GuestDto> listOfGuests, Long bookingId);

    String initiatePayments(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingsForHotel(Long hotelId);

    HotelReportDto generateHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDto> getBookingsForCurrentUser();

    
}