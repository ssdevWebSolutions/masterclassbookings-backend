package com.ssdevcheckincheckout.ssdev.Backend.config;

import jakarta.servlet.http.Cookie;


public class ApplicationSessionCookieConfig {

    public void createSessionCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(3600);  // Set cookie expiration time (1 hour)
        cookie.setSecure(true);  // Use secure cookies (HTTPS only)
        cookie.setHttpOnly(true);  // Prevent client-side access to cookie
        cookie.setPath("/");  // Set cookie path to root

        // Use setValue() instead of setAttribute()
        cookie.setValue(value);  // Correct method to set value
    }
}
