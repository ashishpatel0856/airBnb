package com.ashish.projects.airBnb.service;


import com.ashish.projects.airBnb.entity.Booking;

public interface CheckoutService {
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
