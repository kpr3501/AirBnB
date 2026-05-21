package com.project.airbnb.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.airbnb.dto.BookingDto;
import com.project.airbnb.dto.BookingRequest;
import com.project.airbnb.dto.GuestDto;
import com.project.airbnb.entity.BookingStatusResponseDto;
import com.project.airbnb.service.BookingService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequest bookingRequest) {
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }

    @PostMapping("{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuestsToBooking(@RequestBody List<GuestDto> listOfGuests, @PathVariable Long bookingId) {
        // Implement logic to add guests to an existing booking
        return ResponseEntity.ok(bookingService.addGuestsToBooking(listOfGuests, bookingId));
    }

    @PostMapping("{bookingId}/payments")
    public ResponseEntity<Map<String,String>> initiatePayment(@PathVariable Long bookingId) {
        // Implement logic to add guests to an existing booking
        String sessionUrl = bookingService.initiatePayments(bookingId);
        return ResponseEntity.ok(Map.of("ssessionUrl" , sessionUrl));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookingId}/status")
    public ResponseEntity<BookingStatusResponseDto> getBookingStatus(@PathVariable Long bookingId) {
        String status = bookingService.getBookingStatus(bookingId);
        return ResponseEntity.ok(new BookingStatusResponseDto(status));
    }
    
}
