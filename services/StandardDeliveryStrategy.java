package services;

import enums.VehicleType;
import interfaces.IDeliveryFeeStrategy;

// Strategie standard: taxa baza + cost/km
public class StandardDeliveryStrategy implements IDeliveryFeeStrategy {
    
    private static final double BASE_FEE = 5.0;     // 5 RON taxa de baza
    private static final double PER_KM_RATE = 2.0;  // 2 RON/km

    @Override
    public double calculateFee(double distance, VehicleType vehicleType) {
        return BASE_FEE + (distance * PER_KM_RATE);
    }

    @Override
    public String getStrategyName() {
        return "Standard";
    }

    @Override
    public String toString() {
        return "StandardDeliveryStrategy{base=" + BASE_FEE + " RON, perKm=" + PER_KM_RATE + " RON/km}";
    }
}
