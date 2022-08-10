package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerId(Long userId);

    Collection<Item> findByRequestId(Long requestId);

    @Query("select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?2, '%')) " +
            " or upper(i.description) like upper(concat('%', ?2, '%'))) " +
            " and (i.ownerId = ?1 or i.available = TRUE) ")
    Collection<Item> search(Long userId, String text);
}
