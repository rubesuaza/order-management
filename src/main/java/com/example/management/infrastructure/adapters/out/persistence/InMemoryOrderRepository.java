package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of OrderRepository.
 * Used for tests and when no persistent store is configured.
 */
@Repository
@Profile({"test", "default"})
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<UUID, Order> store = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        Order toStore = reconstitute(order);
        store.put(toStore.getId(), toStore);
        return reconstitute(toStore);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(store.get(id)).map(this::reconstitute);
    }

    @Override
    public boolean existsById(UUID id) {
        return store.containsKey(id);
    }

    private Order reconstitute(Order order) {
        return new Order(
                order.getId(),
                order.getCustomerId(),
                order.getCreatedAt(),
                new ArrayList<>(order.getItems()),
                order.getStatus()
        );
    }
}
