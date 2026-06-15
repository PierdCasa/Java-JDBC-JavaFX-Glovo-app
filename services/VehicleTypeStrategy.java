package services;

import enums.VehicleType;
import interfaces.IDeliveryFeeStrategy;

// Strategie bazata pe tipul vehiculului: bicicleta ieftina, masina scumpa
public class VehicleTypeStrategy implements IDeliveryFeeStrategy {

    private static final double BASE_FEE = 3.0;

    @Override
    public double calculateFee(double distance, VehicleType vehicleType) {
        double perKmRate;
        switch (vehicleType) {
            case BICYCLE:
                perKmRate = 1.0;    // cel mai ieftin
                break;
            case MOTORCYCLE:
                perKmRate = 2.0;    // mediu
                break;
            case CAR:
                perKmRate = 3.0;    // cel mai scump
                break;
            default:
                perKmRate = 2.0;
        }
        return BASE_FEE + (distance * perKmRate);
    }

    @Override
    public String getStrategyName() {
        return "Bazat pe Vehicul";
    }

    @Override
    public String toString() {
        return "VehicleTypeStrategy{BICYCLE=1 RON/km, MOTORCYCLE=2 RON/km, CAR=3 RON/km}";
    }
}
