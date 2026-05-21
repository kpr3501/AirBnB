package com.project.airbnb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.airbnb.dto.BookingDto;
import com.project.airbnb.dto.ProfileUpdateRequestDto;
import com.project.airbnb.entity.User;
import com.project.airbnb.service.BookingService;
import com.project.airbnb.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/users")   
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    @PutMapping("/profile")
    public ResponseEntity<Void> updateUserProfile(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto) {
        userService.updateUserProfile(profileUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/myBookings")
    public ResponseEntity<List<BookingDto>> getMyBookings() {
        // Implement logic to fetch bookings for the authenticated user
            List<BookingDto> bookings = bookingService.getBookingsForCurrentUser();
            return ResponseEntity.ok(bookings);
        


    }
    
    @GetMapping("/myProfile")
    public ResponseEntity<?> getMyProfile() {
        // Implement logic to fetch profile details for the authenticated user
        User user = (User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.getUserById(user.getId()));
    }
    
    
    
}
