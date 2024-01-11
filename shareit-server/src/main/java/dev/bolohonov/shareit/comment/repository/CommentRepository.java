package dev.bolohonov.shareit.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.bolohonov.shareit.comment.Comment;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Collection<Comment> findCommentsByItemIdOrderByCreatedDesc(Long itemId);
}
