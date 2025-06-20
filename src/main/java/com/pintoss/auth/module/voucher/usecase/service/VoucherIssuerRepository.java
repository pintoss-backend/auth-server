package com.pintoss.auth.module.voucher.usecase.service;

import com.pintoss.auth.module.voucher.model.VoucherIssuer;
import com.pintoss.auth.module.voucher.store.VoucherIssuerEntity;
import com.pintoss.auth.module.voucher.usecase.dto.VoucherIssuerDetailResult;
import com.pintoss.auth.module.voucher.usecase.dto.VoucherIssuerResult;
import java.util.List;
import java.util.Optional;

public interface VoucherIssuerRepository {
    void save(VoucherIssuer voucherIssuer);

    boolean existsByName(String name);

    Optional<VoucherIssuerEntity> findById(Long voucherIssuerId);

    List<VoucherIssuerResult> fetchSummaryList();

    Optional<VoucherIssuerDetailResult> fetchDetail(Long voucherIssuerId);
}
