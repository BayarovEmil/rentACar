package com.cognito.rent_a_car_api.api;

import com.cognito.rent_a_car_api.business.FeedbackService;
import com.cognito.rent_a_car_api.core.common.PageResponse;
import com.cognito.rent_a_car_api.dto.FeedbackRequest;
import com.cognito.rent_a_car_api.dto.FeedbackResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedback")
@Tag(name = "Feedback")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Integer> saveFeedback(
            @Valid @RequestBody FeedbackRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedbackService.save(request,connectedUser));
    }

    @GetMapping("/feedback/{car-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacks(
            @PathVariable("car-id") Integer carId,
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedbackService.findAllFeedbacks(carId,page,size,connectedUser));
    }
}
