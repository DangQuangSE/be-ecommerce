package com.sport_pro_be.modules.review.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String userName;
    private String userAvatar;
    private Integer rating;
    private String comment;
    private List<String> images;
    private String replyComment;
    private LocalDateTime createdAt;
}
