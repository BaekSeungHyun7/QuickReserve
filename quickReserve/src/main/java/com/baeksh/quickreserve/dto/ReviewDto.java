package com.baeksh.quickreserve.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDto {

    private Long id;  			// 리뷰 PK
    private String username;  	// 리뷰 작성자
    private String restaurantName;  // 매장 이름
    private String title;  		// 리뷰 제목
    private String content;  	// 리뷰 내용
}
