package com.pintoss.auth.module.order.domain;

import com.pintoss.auth.common.exception.ErrorCode;
import com.pintoss.auth.common.exception.client.BadRequestException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNo;

    @Column(nullable = false, name = "orderer_id")
    private Long ordererId;

    @Column(nullable = false, name = "orderer_name")
    private String ordererName;

    @Column(nullable = false, name = "orderer_email")
    private String ordererEmail;

    @Column(nullable = false, name = "orderer_phone")
    private String ordererPhone;

    @Column(nullable = false, name = "order_name")
    private String orderName;

    @Column(nullable = true)
    private Long paymentId;

    @OneToMany(
        mappedBy = "order",
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
        orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private long totalPrice;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Order(Long ordererId, String ordererName, String ordererEmail, String ordererPhone, String orderName, List<OrderItem> orderItems) {
        this.ordererId = ordererId;
        this.ordererName = ordererName;
        this.ordererEmail = ordererEmail;
        this.ordererPhone = ordererPhone;
        this.orderNo = generateOrderNo();
        this.orderName = orderName;
        orderItems.forEach(this::addOrderItem); // 연관관계 메서드 사용
        this.orderItems = orderItems;
        this.status = OrderStatus.PENDING;
        this.totalPrice = orderItems.stream().mapToLong(item -> item.getPrice()).sum();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private String generateOrderNo() {
        String datePart = LocalDate.now().format(DATE_FORMAT); // "YYYYMMdd"
        int randomPart = 10000000 + RANDOM.nextInt(90000000); // 8자리 랜덤 숫자
        return datePart + randomPart;
    }

    // 개별 OrderItem 추가 메서드 (연관관계 설정)
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.assignOrder(this);
    }

    public static Order create(Long ordererId, String ordererName, String ordererEmail, String ordererPhone, String orderName, List<OrderItem> orderItems) {
        return new Order(ordererId, ordererName, ordererEmail, ordererPhone, orderName, orderItems);
    }

    public void verifyTotalPrice(long taxAmount) {
        if(totalPrice != taxAmount) {
            throw new BadRequestException(ErrorCode.PAYMENT_APPROVED_AMOUNT_MISMATCH);
        }
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELED) {
            throw new BadRequestException(ErrorCode.ORDER_ALREADY_CANCELED);
        }
        this.status = OrderStatus.CANCELED;
        this.updatedAt = LocalDateTime.now();
    }

    public void issued() {
        this.status = OrderStatus.ISSUED;
    }

    public void markAsPaid(Long paymentId) {
        if (this.status == OrderStatus.PAID) {
            throw new BadRequestException(ErrorCode.ORDER_ALREADY_PAID);
        } else if (this.status == OrderStatus.CANCELED) {
            throw new BadRequestException(ErrorCode.ORDER_ALREADY_CANCELED);
        } else if (this.status == OrderStatus.ISSUED) {
            throw new BadRequestException(ErrorCode.ORDER_ALREADY_ISSUED);
        }
        this.paymentId = paymentId;
        this.status = OrderStatus.PAID;
        this.updatedAt = LocalDateTime.now();
        for (OrderItem orderItem : orderItems) {
            orderItem.issueProcessing();
        }
    }

    public void assignPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public void markAsRefunded() {
        if (this.status != OrderStatus.PAID && this.status != OrderStatus.ISSUED) {
            throw new BadRequestException(ErrorCode.ORDER_NOT_REFUNDABLE);
        }

        boolean allRefunded = this.orderItems.stream().allMatch(OrderItem::isRefunded);
        boolean allFailed = this.orderItems.stream().allMatch(OrderItem::isRefundFailed);

        if (allRefunded) {
            this.status = OrderStatus.REFUNDED;
        } else if (allFailed) {
            this.status = OrderStatus.REFUND_FAILED;
        } else {
            this.status = OrderStatus.PARTIAL_REFUNDED;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void validateRefundable() {
        if (this.status == OrderStatus.REFUNDED || this.status == OrderStatus.REFUND_FAILED || this.status == OrderStatus.PARTIAL_REFUNDED) {
            throw new BadRequestException(ErrorCode.ORDER_ALREADY_REFUNDED);
        } else if (this.status == OrderStatus.CANCELED) {
            throw new BadRequestException(ErrorCode.ORDER_ALREADY_CANCELED);
        } else if (this.status != OrderStatus.PAID && this.status != OrderStatus.ISSUED) {
            throw new BadRequestException(ErrorCode.ORDER_NOT_REFUNDABLE);
        }
    }
}
