package com.ashish.projects.VrboApp.service;

import com.ashish.projects.VrboApp.entity.Booking;

public interface CheckoutService {
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
