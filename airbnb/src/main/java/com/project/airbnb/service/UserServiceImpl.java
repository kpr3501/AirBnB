package com.project.airbnb.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.airbnb.dto.ProfileUpdateRequestDto;
import com.project.airbnb.entity.User;
import com.project.airbnb.exception.ResourceNotFoundException;
import com.project.airbnb.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService ,UserDetailsService{

    private final UserRepository userRepository;
    @Override
    public User getUserById(Long id) {  
        return userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User Id not found" + id));
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
    @Override
    public void updateUserProfile(ProfileUpdateRequestDto profileUpdateRequestDto) {
        userRepository.findById(profileUpdateRequestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User Id not found" + profileUpdateRequestDto.getUserId()));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(!user.getId().equals(profileUpdateRequestDto.getUserId())){
                throw new RuntimeException("Unauthorized to update profile of other user");
            }
        if(profileUpdateRequestDto.getName() != null){
            user.setName(profileUpdateRequestDto.getName());
        }
        if(profileUpdateRequestDto.getGender() != null){
            user.setGender(profileUpdateRequestDto.getGender());
        }
        if(profileUpdateRequestDto.getDateOfBirth() != null){
            user.setDob(profileUpdateRequestDto.getDateOfBirth());
        }
        userRepository.save(user);
        throw new UnsupportedOperationException("Unimplemented method 'updateUserProfile'");
    }

}
