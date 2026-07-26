package com.quantumbanking.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ExternalLiquidationSimulator {

    private final Random random = new Random();

    @Value("${simulation.external-liquidation.success-rate}")
    private double successRate;

    @Value("${simulation.external-liquidation.min-delay-ms}")
    private long minDelayMs;

    @Value("${simulation.external-liquidation.max-delay-ms}")
    private long maxDelayMs;

    public boolean simulate() {
        simulateNetworkLatency();
        return random.nextDouble() < successRate;
    }

    private void simulateNetworkLatency() {
        try {
            long delay = minDelayMs + (long) (random.nextDouble() * (maxDelayMs - minDelayMs));
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}