package com.baeksh.quickreserve.service;

import com.baeksh.quickreserve.dto.ReviewDto;
import com.baeksh.quickreserve.entity.ReviewEntity;
import com.baeksh.quickreserve.entity.RestaurantEntity;
import com.baeksh.quickreserve.entity.UserEntity;
import com.baeksh.quickreserve.exception.CustomException;
import com.baeksh.quickreserve.exception.ErrorCode;
import com.baeksh.quickreserve.repository.ReviewRepository;
import com.baeksh.quickreserve.repository.RestaurantRepository;
import com.baeksh.quickreserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    // 리뷰 생성 로직
    @Transactional
    public ReviewDto createReview(String username, ReviewDto request) {
        // 매장 이름, 제목, 내용 유효성 검사
        if (request.getRestaurantName() == null || request.getRestaurantName().isEmpty() || request.getTitle() == null || request.getTitle().isEmpty() || request.getContent() == null || request.getContent().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        RestaurantEntity restaurant = restaurantRepository.findByName(request.getRestaurantName()).orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        // 리뷰 엔티티 생성 및 저장
        ReviewEntity review = ReviewEntity.builder()
                .user(user)
                .restaurant(restaurant)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        reviewRepository.save(review);

        // 리뷰 정보 반환
        return ReviewDto.builder()
                .id(review.getId())
                .username(user.getUsername())
                .restaurantName(restaurant.getName())
                .title(review.getTitle())
                .content(review.getContent())
                .build();
    }

    // 리뷰 수정 로직
    @Transactional
    public ReviewDto updateReview(Long id, String username, ReviewDto request) {
        if (id == null || id <= 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
        if (request.getTitle() == null || request.getTitle().isEmpty() || request.getContent() == null || request.getContent().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        ReviewEntity review = reviewRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        // 작성자가 맞는지 확인
        if (!review.getUser().getUsername().equals(username)) {
            throw new CustomException(ErrorCode.INVALID_USER);
        }

        // 리뷰 수정
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        reviewRepository.save(review);

        return ReviewDto.builder()
                .id(review.getId())
                .username(review.getUser().getUsername())
                .restaurantName(review.getRestaurant().getName())
                .title(review.getTitle())
                .content(review.getContent())
                .build();
    }

    // 리뷰 삭제 로직
    @Transactional
    public Long deleteReview(Long id, String username) {
        if (id == null || id <= 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        ReviewEntity review = reviewRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        // 리뷰 작성자이거나 매장 관리자여야 삭제 가능
        if (!review.getUser().getUsername().equals(username) && !review.getRestaurant().getOwner().getUsername().equals(username)) {
            throw new CustomException(ErrorCode.INVALID_USER);
        }

        reviewRepository.delete(review);
        return review.getId();
    }

    // 리뷰 상세 조회 로직
    @Transactional(readOnly = true)
    public ReviewDto getReviewDetail(Long id) {
        if (id == null || id <= 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        ReviewEntity review = reviewRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        return ReviewDto.builder()
                .id(review.getId())
                .username(review.getUser().getUsername())
                .restaurantName(review.getRestaurant().getName())
                .title(review.getTitle())
                .content(review.getContent())
                .build();
    }

    // 리뷰 전체 리스트 조회 로직
    @Transactional(readOnly = true)
    public List<ReviewDto> getAllReviews() {
        List<ReviewEntity> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(review -> ReviewDto.builder()
                        .id(review.getId())
                        .username(review.getUser().getUsername())
                        .restaurantName(review.getRestaurant().getName())
                        .title(review.getTitle())
                        .content(review.getContent())
                        .build())
                .collect(Collectors.toList());
    }
}
