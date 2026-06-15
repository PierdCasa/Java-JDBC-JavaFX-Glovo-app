package services;

import enums.VehicleType;
import interfaces.IDeliveryFeeStrategy;

// Strategie vreme ploioasa: multiplicator suplimentar 1.5x
public class RainyWeatherStrategy implements IDeliveryFeeStrategy {

    private static final double BASE_FEE = 5.0;
    private static final double PER_KM_RATE = 2.0;
    private static final double SURGE_MULTIPLIER = 1.5;

    @Override
    public double calculateFee(double distance, VehicleType vehicleType) {
        double standardFee = BASE_FEE + (distance * PER_KM_RATE);
        return standardFee * SURGE_MULTIPLIER;
    }

    @Override
    public String getStrategyName() {
        return "Vreme Ploioasa (x" + SURGE_MULTIPLIER + ")";
    }

    @Override
    public String toString() {
        return "RainyWeatherStrategy{multiplier=" + SURGE_MULTIPLIER + "}";
    }
}
