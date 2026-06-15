package interfaces;

import enums.OrderStatus;
import models.Order;

public interface IOrderObserver {
    void onOrderStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus);
}
