package com.shopmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatistics {

    // Overview stats
    private Long totalUsers;
    private Long totalProducts;
    private Long totalOrders;
    private BigDecimal totalRevenue;

    // Recent activity
    private List<OrderResponse> recentOrders;
    private List<ProductResponse> lowStockProducts;
    private List<ProductResponse> topSellingProducts;

    // Order stats by status
    private OrderStatusStatistics orderStatusStatistics;

    // Sales data (for charts)
    private List<SalesDataPoint> salesData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStatusStatistics {
        private long pending;
        private long confirmed;
        private long processing;
        private long shipped;
        private long delivered;
        private long cancelled;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesDataPoint {
        private String period;
        private BigDecimal revenue;
        private Long orderCount;
    }
}
