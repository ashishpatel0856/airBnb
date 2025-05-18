package com.ashish.projects.airBnb.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    public StripeConfig(@Value("${STRIPE_SECRET_KEY}") String stripeSecretKey) {
        Stripe.apiKey = stripeSecretKey;
    }
}
