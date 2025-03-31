package com.locket.user.controller.feedback;

import com.locket.user.service.feedback.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<?> getFeedback(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        return feedbackService.handleFeedbackRequest(userId, year, month);
    }
}
