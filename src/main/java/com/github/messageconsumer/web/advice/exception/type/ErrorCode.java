package com.github.messageconsumer.web.advice.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    BAD_REQUEST("입력 값을 확인해 주세요.");
    private final String description;
}
