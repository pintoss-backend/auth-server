package com.pintoss.auth.api.order;

import com.pintoss.auth.module.payment.application.PaymentMethodType;
import com.pintoss.auth.module.order.domain.OrderItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "결제 방법은 필수 입력 항목입니다.")
    private PaymentMethodType paymentMethod;

    @NotEmpty(message = "주문 항목을 한 개 이상 포함해야 합니다.")
    @Valid
    private List<OrderItemRequest> orderItems;

}
