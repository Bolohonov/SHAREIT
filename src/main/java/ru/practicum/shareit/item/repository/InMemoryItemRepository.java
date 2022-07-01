package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;

import java.util.*;

@Component
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, List<Item>> items = new HashMap<>();

    @Override
    public Item addItem(Long userId, Item item) {
        item.setId(getId());
        items.compute(userId, (id, userItems) -> {
            if(userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return item;
    }

    @Override
    public Item updateItem(Long userId, Item item) {
        items.compute(userId, (id, userItems) -> {
            for (Item i : userItems) {
                if (compareItemsByIdOwnerRequest(item, i)) {
                    userItems.remove(i);
                    userItems.add(item);
                }
            }
            return userItems;
        });
        return item;
    }

    @Override
    public Optional<Item> findItemById(Long id) {
        Optional<Item> item = Optional.empty();
        for (List<Item> i : items.values()) {
            item = i.stream().filter(p -> p.getId() == id).findFirst();
        }
        return item;
    }

    @Override
    public Collection<Item> findUserItems(Long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public void deleteItem(Long userId, Long id) {
        items.get(userId).remove(this.findItemById(id));
    }

    private long getId() {
        long lastId = items.values()
                .stream()
                .flatMap(Collection::stream)
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }

    private boolean compareItemsByIdOwnerRequest(Item firstItem, Item secondItem) {
        if (firstItem.getId() == secondItem.getId()
                && firstItem.getOwner() == secondItem.getOwner()
                && firstItem.getRequest() == secondItem.getRequest()) {
            return true;
        } else {
            return false;
        }
    }
}
