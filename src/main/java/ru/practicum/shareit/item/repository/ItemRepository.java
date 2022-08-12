package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerId(Long userId);

    Page<Item> findByOwnerId(Long userId, Pageable pageable);

    Collection<Item> findByRequestId(Long requestId);

    @Query("select i from Item as i " +
            "where (upper(i.name) like upper(concat('%', ?2, '%')) " +
            " or upper(i.description) like upper(concat('%', ?2, '%'))) " +
            " and (i.ownerId = ?1 or i.available = TRUE) ")
    Page<Item> search(Long userId, String text, Pageable pageable);
}
