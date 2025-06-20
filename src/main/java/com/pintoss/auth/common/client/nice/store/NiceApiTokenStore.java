package com.pintoss.auth.common.client.nice.store;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class NiceApiTokenStore {
    private NiceApiAccessToken accessToken;
    private NiceApiCryptoToken cryptoToken;
    private NiceApiSymmetricKey symmetricKey;
}
