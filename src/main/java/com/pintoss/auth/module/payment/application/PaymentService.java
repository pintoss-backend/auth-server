package com.pintoss.auth.module.payment.application;

import com.pintoss.auth.module.order.application.flow.OrderReader;
import com.pintoss.auth.module.order.domain.Order;
import com.pintoss.auth.module.payment.domain.PaymentDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentApprovalService paymentApprovalService;
    private final OrderReader orderReader;
    private final PaymentAdder paymentAdder;

    @Transactional
    public void purchase(PurchaseCommand command) {
        Order order = orderReader.getByOrderNo(command.getOrderNo());

        PaymentApprovalResponse approvalResponse = paymentApprovalService.approval(
            new PaymentApprovalRequest(command.getServiceCode(), command.getOrderNo(), command.getMessage()));


        PaymentDomain payment = new PaymentDomain(
            approvalResponse.getIsSuccess(),
            command.getServiceId(),
            command.getServiceCode(),
            command.getOrderNo(),
            approvalResponse.getOrderDate(),
            approvalResponse.getTransactionId(),
            approvalResponse.getAuthAmount(),
            approvalResponse.getAuthDate(),
            approvalResponse.getPaymentMethodType(),
            approvalResponse.getDetailResponseMessage(),
            approvalResponse.getJson()
            );
        paymentAdder.add(payment);
    }
}
