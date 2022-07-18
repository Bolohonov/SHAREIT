package ru.practicum.shareit.comment.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.Comment;

@Component
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getText(),
                comment.getItemId(),
                comment.getAuthorId(),
                comment.getCreated()
        );
    }
}
