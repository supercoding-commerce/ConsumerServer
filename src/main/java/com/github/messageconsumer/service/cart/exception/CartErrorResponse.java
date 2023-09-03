package com.github.messageconsumer.service.cart.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartErrorResponse {

    private CartErrorCode errorCode;
    private String errorMessage;
}

