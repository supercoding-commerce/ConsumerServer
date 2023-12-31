package com.github.messageconsumer.web.advice.exception;


import com.github.messageconsumer.web.advice.exception.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    private ErrorCode errorCode;
    private String errorMessage;


}
