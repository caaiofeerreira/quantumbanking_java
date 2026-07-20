package com.quantumbanking.external;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ExternalLiquidationSimulator {


    private static final double SUCCESS_RATE = 0.9;

    private static final long MIN_DELAY_MS = 200;
    private static final long MAX_DELAY_MS = 1500;

    private final Random random = new Random();

    public boolean simulate() {
        simulateNetworkLatency();
        return random.nextDouble() < SUCCESS_RATE;
    }

    private void simulateNetworkLatency() {
        try {
            long delay = MIN_DELAY_MS + (long) (random.nextDouble() * (MAX_DELAY_MS - MIN_DELAY_MS));
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}