package com.project.airbnb.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.airbnb.service.BookingService;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebHookController {
    private final BookingService bookingService;
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/payment")
    public ResponseEntity<String> handlePaymentWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try{
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            bookingService.capturePayment(event);
            return ResponseEntity.ok("Webhook received and processed successfully");
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
        }
        

}
}
