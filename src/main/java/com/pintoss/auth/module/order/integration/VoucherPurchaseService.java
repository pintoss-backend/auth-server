package com.pintoss.auth.module.order.integration;

import com.pintoss.auth.module.payment.application.PaymentMethodType;

public interface VoucherPurchaseService {
    PurchaseResponse purchase(String orderId, String transactionId, String mid, Long taxAmount, PaymentMethodType paymentMethodType);
}
