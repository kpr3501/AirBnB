package com.project.airbnb.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class HotelContactInfo {

    private String phoneNumber;
    private String address;
    private String email;
    private String location;

}
