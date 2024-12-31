package com.project.service;

import com.project.dto.CommentResponse;
import java.util.List;
import java.util.Map;

public interface CommentService {
    Map<String, Object> addCommentToPlant(Long plantId, String content, String username);

    List<CommentResponse> getCommentsByPlant(Long plantId); // New method
}
