package com.project.airbnb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.airbnb.dto.BookingDto;
import com.project.airbnb.dto.HotelDto;
import com.project.airbnb.dto.HotelReportDto;
import com.project.airbnb.service.BookingService;
import com.project.airbnb.service.HotelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel (@RequestBody HotelDto hotelDto) {
        log.info("Received request to create new hotel: {}", hotelDto.getName());
        HotelDto createdHotel = hotelService.createNewHotel(hotelDto);
        log.info("Successfully created hotel with ID: {}", createdHotel.getId());
        return new ResponseEntity<>(createdHotel, HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId) {
        log.info("Received request to fetch hotel with ID: {}", hotelId);
        HotelDto hotelDto = hotelService.getHotelById(hotelId);
        log.info("Successfully fetched hotel with ID: {}", hotelId);
        return new ResponseEntity<>(hotelDto, HttpStatus.OK);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long hotelId, @RequestBody HotelDto hotelDto) {
        log.info("Received request to update hotel with ID: {}", hotelId);
        HotelDto updatedHotel = hotelService.updateHotelById(hotelId, hotelDto);
        log.info("Successfully updated hotel with ID: {}", hotelId);
        return new ResponseEntity<>(updatedHotel, HttpStatus.OK);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long hotelId) {
        log.info("Received request to delete hotel with ID: {}", hotelId);
        hotelService.deleteHotelById(hotelId);
        log.info("Successfully deleted hotel with ID: {}", hotelId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{hotelId}/activate")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId) {
        log.info("Received request to activate hotel with ID: {}", hotelId);
        hotelService.actiavateHotelById(hotelId);
        log.info("Successfully activated hotel with ID: {}", hotelId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
    }

    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotel(){
        log.info("Received request to fetch all hotels");
        List<HotelDto> hotelDtos = hotelService.getAllHotels();
        log.info("Successfully fetched all hotels");
        return new ResponseEntity<>(hotelDtos, HttpStatus.OK);
    }

    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity<List<BookingDto>> getAllBookingsForHotel(@PathVariable Long hotelId){
        log.info("Received request to fetch all bookings for hotel with ID: {}", hotelId);
        List<BookingDto> bookingDtos = bookingService.getAllBookingsForHotel(hotelId);
        log.info("Successfully fetched all bookings for hotel with ID: {}", hotelId);
        return new ResponseEntity<>(bookingDtos, HttpStatus.OK); 
    }   
    
    @GetMapping("/{hotelId}/reports")
    public ResponseEntity<HotelReportDto> getHotelReport(@PathVariable Long hotelId, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        log.info("Received request to generate report for hotel with ID: {}", hotelId);
        // Implement logic to generate report based on startDate and endDate
        HotelReportDto reportData = bookingService.generateHotelReport(hotelId, startDate, endDate);
        log.info("Successfully generated report for hotel with ID: {}", hotelId);
        return new ResponseEntity<>(reportData, HttpStatus.OK);
    }
    
    
}