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
	
	//리뷰기능
	
	

    private final ReviewService reviewService;

    /**
     * 리뷰 생성 API
     * @param request 리뷰 생성 요청 데이터를 담고 있는 DTO (매장 이름, 제목, 내용)
     * @return 생성된 리뷰의 상세 정보 반환
     */
    @PostMapping("/review")
    public ResponseEntity<?> createReview(@RequestBody ReviewDto request) {
        // 현재 인증된 사용자의 정보를 가져옴 (로그인 상태 확인)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);  // 인증되지 않은 사용자는 예외 처리
        }

        // 인증된 사용자의 username 가져오기
        String username = auth.getName();

        // 리뷰 생성 로직 호출 (username과 요청 데이터를 전달)
        ReviewDto createdReview = reviewService.createReview(username, request);

        // 생성된 리뷰 정보를 반환 (HTTP 200 OK와 함께)
        return ResponseEntity.ok(createdReview);
    }

    /**
     * 리뷰 수정 API
     * @param id 수정할 리뷰의 고유 ID (PathVariable)
     * @param request 수정할 제목과 내용이 담긴 DTO
     * @return 수정된 리뷰의 상세 정보 반환
     */
    @PutMapping("/review/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody ReviewDto request) {
        // 현재 인증된 사용자의 정보를 가져옴 (로그인 상태 확인)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);  // 인증되지 않은 사용자는 예외 처리
        }

        // 인증된 사용자의 username 가져오기
        String username = auth.getName();

        // 리뷰 수정 로직 호출 (리뷰 ID, username, 수정할 내용 전달)
        ReviewDto updatedReview = reviewService.updateReview(id, username, request);

        // 수정된 리뷰 정보를 반환
        //241004 0720 200 OK
        return ResponseEntity.ok(updatedReview);
    }

    /**
     * 리뷰 삭제 API
     * @param id 삭제할 리뷰의 고유 ID (PathVariable)
     * @return 삭제된 리뷰의 ID 반환
     */
    @DeleteMapping("/review/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        // 현재 인증된 사용자의 정보를 가져옴 (로그인 상태 확인)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);  // 인증되지 않은 사용자는 예외 처리
        }

        // 인증된 사용자의 username 가져오기
        String username = auth.getName();

        // 리뷰 삭제 로직 호출 (리뷰 ID와 username 전달)
        Long deletedReviewId = reviewService.deleteReview(id, username);

        // 삭제된 리뷰의 ID 반환
        return ResponseEntity.ok(deletedReviewId);
    }

    /**
     * 리뷰 상세 조회 API
     * @param id 조회할 리뷰의 고유 ID (PathVariable)
     * @return 조회된 리뷰의 상세 정보 반환
     */
    @GetMapping("/search/{id}")
    public ResponseEntity<?> getReviewDetail(@PathVariable Long id) {
        // 리뷰 조회 로직 호출 (리뷰 ID로 리뷰 정보 가져옴)
        ReviewDto review = reviewService.getReviewDetail(id);

        // 조회된 리뷰 정보를 반환
        return ResponseEntity.ok(review);
    }

    /**
     * 전체 리뷰 리스트 조회 API
     * @return 모든 리뷰의 리스트 반환
     */
    @GetMapping("/search")
    public ResponseEntity<?> getAllReviews() {
        // 전체 리뷰 리스트 조회 로직 호출
        List<ReviewDto> reviews = reviewService.getAllReviews();

        // 조회된 모든 리뷰 정보를 반환
        return ResponseEntity.ok(reviews);
    }
}
