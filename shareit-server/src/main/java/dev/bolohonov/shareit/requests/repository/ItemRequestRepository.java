package dev.bolohonov.shareit.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.bolohonov.shareit.requests.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Collection<ItemRequest> findByRequesterId(Long userId);
}
