package com.baeksh.quickreserve.controller;

import com.baeksh.quickreserve.dto.ReviewDto;
import com.baeksh.quickreserve.service.ReviewService;
import com.baeksh.quickreserve.exception.CustomException;
import com.baeksh.quickreserve.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 생성
    @PostMapping("/review")
    public ResponseEntity<?> createReview(@RequestBody ReviewDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        String username = auth.getName();
        ReviewDto createdReview = reviewService.createReview(username, request);
        return ResponseEntity.ok(createdReview);
    }

    // 리뷰 수정
    @PutMapping("/review/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody ReviewDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String username = auth.getName();
        ReviewDto updatedReview = reviewService.updateReview(id, username, request);
        return ResponseEntity.ok(updatedReview);
    }

    // 리뷰 삭제
    @DeleteMapping("/review/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String username = auth.getName();
        Long deletedReviewId = reviewService.deleteReview(id, username);
        return ResponseEntity.ok(deletedReviewId);
    }

    // 리뷰 상세 조회
    @GetMapping("/search/{id}")
    public ResponseEntity<?> getReviewDetail(@PathVariable Long id) {
        ReviewDto review = reviewService.getReviewDetail(id);
        return ResponseEntity.ok(review);
    }

    // 리뷰 전체 리스트 조회
    @GetMapping("/search")
    public ResponseEntity<?> getAllReviews() {
        List<ReviewDto> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }
}
