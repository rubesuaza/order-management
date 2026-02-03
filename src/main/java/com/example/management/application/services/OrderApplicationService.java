package com.example.management.application.services;

import com.example.management.application.ports.in.CreateOrderUseCase;
import com.example.management.application.ports.in.GetOrderUseCase;
import com.example.management.application.ports.in.OrderItemCommand;
import com.example.management.application.ports.in.OrderStatusUseCase;
import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.domain.model.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderApplicationService implements CreateOrderUseCase, GetOrderUseCase, OrderStatusUseCase {

    private final OrderRepository orderRepository;

    public OrderApplicationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Order create(UUID customerId, List<OrderItemCommand> items) {
        List<OrderItem> domainItems = items.stream()
                .map(cmd -> {
                    String currency = cmd.unitPriceCurrency() != null ? cmd.unitPriceCurrency() : Money.DEFAULT_CURRENCY;
                    Money unitPrice = new Money(cmd.unitPriceAmount(), currency);
                    return new OrderItem(cmd.productId(), cmd.quantity(), unitPrice);
                })
                .toList();
        Order order = new Order(UUID.randomUUID(), customerId, domainItems);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    @Transactional
    public Optional<Order> markAsPaid(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.markAsPaid();
                    return orderRepository.save(order);
                });
    }

    @Override
    @Transactional
    public Optional<Order> ship(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.ship();
                    return orderRepository.save(order);
                });
    }

    @Override
    @Transactional
    public Optional<Order> cancel(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.cancel();
                    return orderRepository.save(order);
                });
    }
}
