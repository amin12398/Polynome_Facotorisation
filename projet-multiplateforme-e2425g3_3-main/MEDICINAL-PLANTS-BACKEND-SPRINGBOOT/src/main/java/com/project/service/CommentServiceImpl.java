package com.project.service;

import com.project.dto.CommentResponse;
import com.project.entity.Comment;
import com.project.entity.PlantesMedicinales;
import com.project.entity.User;
import com.project.repository.CommentRepository;
import com.project.repository.PlantesMedicinalesRepository;
import com.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PlantesMedicinalesRepository plantesMedicinalesRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PlantesMedicinalesRepository plantesMedicinalesRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.plantesMedicinalesRepository = plantesMedicinalesRepository;
    }

    @Transactional
    @Override
    public Map<String, Object> addCommentToPlant(Long plantId, String content, String username) {
        // Find the plant
        PlantesMedicinales plant = plantesMedicinalesRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        // Find the user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create and save the comment
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPlant(plant);
        comment.setUser(user);

        commentRepository.save(comment);

        // Build the response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Comment added successfully");
        response.put("username", username);

        return response;
    }

    @Override
    public List<CommentResponse> getCommentsByPlant(Long plantId) {
        // Retrieve all comments for the specified plant
        return commentRepository.findByPlantId(plantId).stream()
                .map(comment -> new CommentResponse(comment.getUser().getUsername(), comment.getContent()))
                .collect(Collectors.toList());
    }
}
