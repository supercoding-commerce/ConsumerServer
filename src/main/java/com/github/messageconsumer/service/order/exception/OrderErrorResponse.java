package com.github.messageconsumer.service.order.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderErrorResponse {

    private OrderErrorCode errorCode;
    private String errorMessage;
}

