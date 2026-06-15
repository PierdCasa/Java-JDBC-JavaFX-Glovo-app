package interfaces;

import enums.VehicleType;

public interface IDeliveryFeeStrategy {
    double calculateFee(double distance, VehicleType vehicleType);
    String getStrategyName();
}
