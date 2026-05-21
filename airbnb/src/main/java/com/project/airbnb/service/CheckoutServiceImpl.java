package com.project.airbnb.service;

import com.project.airbnb.repository.BookingRepository;

import java.math.BigDecimal;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.project.airbnb.entity.Booking;
import com.project.airbnb.entity.User;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    private final BookingRepository bookingRepository;

    @org.springframework.beans.factory.annotation.Value("${stripe.api.key}")
    private String stripeApiKey;

    @Override
    public String getCheoutSession(Booking booking, String successUrl, String failureUrl) {
        log.info("payment session initiated");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        try {
            // Validate Stripe API key
            if (stripeApiKey == null || stripeApiKey.contains("your_stripe_secret_key_here")) {
                throw new RuntimeException("Stripe API key is not configured. Please set a valid Stripe API key in application.properties with property 'stripe.api.key'");
            }
            
            // Set Stripe API key
            com.stripe.Stripe.apiKey = stripeApiKey;
            
            Customer customer = Customer.create(CustomerCreateParams.builder()
                                                    .setEmail(user.getEmail())
                                                    .setName(user.getName())
                                                    .build()
                                                     );   
                                                    
        
        SessionCreateParams sessionCreateParams = new SessionCreateParams.Builder()
                                        .setMode(SessionCreateParams.Mode.PAYMENT)
                                        .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                                        .setCustomer(customer.getId())
                                        .setSuccessUrl(successUrl)
                                        .setCancelUrl(failureUrl)
                                        .addLineItem(
                                            SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency("inr")
                                                            .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(booking.getHotel().getName() + " : " + booking.getRoom().getType())
                                                            .setDescription("Booking Id: " + booking.getId())
                                                                            .build()        
                                                        )   
                                                            .build()       
                                        )   
                                            .build()
                                        )
                                        .build();  

        Session  session = Session.create(sessionCreateParams);  
        booking.setPaymentSessionId(session.getId());
        bookingRepository.save(booking);
        return session.getUrl();
        }
        catch(StripeException e){
            log.error("Stripe error occurred while creating checkout session: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create checkout session: " + e.getMessage(), e);
        }

    }

}
