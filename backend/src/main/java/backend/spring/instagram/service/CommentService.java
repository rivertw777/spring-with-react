package backend.spring.instagram.service;

import backend.spring.instagram.model.dto.request.CommentWriteRequest;
import backend.spring.instagram.model.entity.Comment;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    void writeComment(String username, Long PostId, CommentWriteRequest wirteParam);
    List<Comment> getComments(Long postId);

}