package com.github.messageconsumer.service.order.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderException extends RuntimeException {

    private OrderErrorCode errorCode;
    private String errorMessage;

    public OrderException(OrderErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
