package com.ashish.projects.airBnb.controller;

import com.ashish.projects.airBnb.service.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final BookingService bookingService;

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private  String endPointSecret;

    public WebhookController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload , @RequestHeader("Stripe-Signature") String signHeader) {
        try {
            Event event = Webhook.constructEvent(payload,signHeader, endPointSecret);
            bookingService.capturePayment(event);
            return ResponseEntity.ok().build();
        }  catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }
}
