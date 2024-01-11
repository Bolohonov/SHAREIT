package dev.bolohonov.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.bolohonov.shareit.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
