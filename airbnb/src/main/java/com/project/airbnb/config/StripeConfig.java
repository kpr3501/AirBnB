package com.project.airbnb.config;


import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    public StripeConfig(@org.springframework.beans.factory.annotation.Value("${stripe.api.key}") String apiKey) {
        com.stripe.Stripe.apiKey = apiKey;
    }

}
