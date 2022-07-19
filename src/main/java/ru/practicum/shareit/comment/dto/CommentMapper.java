package ru.practicum.shareit.comment.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.Comment;

@Component
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment, String authorName) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItemId(),
                comment.getAuthorId(),
                comment.getCreated(),
                authorName
        );
    }

    public static CommentDtoForItem toCommentDtoForItem(Comment comment, String authorName) {
        return new CommentDtoForItem(
                comment.getId(),
                comment.getText(),
                authorName,
                comment.getCreated()
        );
    }
}
