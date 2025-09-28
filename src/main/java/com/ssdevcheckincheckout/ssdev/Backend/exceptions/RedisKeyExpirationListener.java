package com.ssdevcheckincheckout.ssdev.Backend.exceptions;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import com.ssdevcheckincheckout.ssdev.Backend.service.ReservationService;

@Service
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    private final ReservationService reservationService;

    @Autowired
    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer,
                                      ReservationService reservationService) {
        super(listenerContainer);
        this.reservationService = reservationService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if (expiredKey.startsWith("reservation:")) {
            String reservationId = expiredKey.replace("reservation:", "");
            boolean released = reservationService.releaseReservation(reservationId);
            System.out.println("Reservation " + reservationId + " expired and released automatically: " + released);
        }
    }
}

