package com.project.airbnb.dto;

import java.time.LocalDate;

import com.project.airbnb.entity.enums.Gender;

import lombok.Data;

@Data
public class ProfileUpdateRequestDto {

    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
    public Long getUserId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserId'");
    }
}
