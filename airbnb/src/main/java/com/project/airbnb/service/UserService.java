package com.project.airbnb.service;

import com.project.airbnb.dto.ProfileUpdateRequestDto;
import com.project.airbnb.entity.User;

public interface UserService {

    User getUserById(Long id);

    void updateUserProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

}
