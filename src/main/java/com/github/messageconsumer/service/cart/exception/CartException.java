package com.github.messageconsumer.service.cart.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartException extends RuntimeException {

    private CartErrorCode errorCode;
    private String errorMessage;

    public CartException(CartErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
