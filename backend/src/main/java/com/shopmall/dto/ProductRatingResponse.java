package com.shopmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRatingResponse {

    private Long productId;
    private Double averageRating;
    private Long totalReviews;
    private Integer ratingCount5;
    private Integer ratingCount4;
    private Integer ratingCount3;
    private Integer ratingCount2;
    private Integer ratingCount1;

    public static ProductRatingResponse fromReviews(List<com.shopmall.entity.Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return ProductRatingResponse.builder()
                    .averageRating(0.0)
                    .totalReviews(0L)
                    .ratingCount5(0)
                    .ratingCount4(0)
                    .ratingCount3(0)
                    .ratingCount2(0)
                    .ratingCount1(0)
                    .build();
        }

        double average = reviews.stream()
                .mapToInt(com.shopmall.entity.Review::getRating)
                .average()
                .orElse(0.0);

        long count5 = reviews.stream().filter(r -> r.getRating() == 5).count();
        long count4 = reviews.stream().filter(r -> r.getRating() == 4).count();
        long count3 = reviews.stream().filter(r -> r.getRating() == 3).count();
        long count2 = reviews.stream().filter(r -> r.getRating() == 2).count();
        long count1 = reviews.stream().filter(r -> r.getRating() == 1).count();

        return ProductRatingResponse.builder()
                .productId(reviews.get(0).getProduct().getId())
                .averageRating(Math.round(average * 10.0) / 10.0)
                .totalReviews((long) reviews.size())
                .ratingCount5((int) count5)
                .ratingCount4((int) count4)
                .ratingCount3((int) count3)
                .ratingCount2((int) count2)
                .ratingCount1((int) count1)
                .build();
    }
}
