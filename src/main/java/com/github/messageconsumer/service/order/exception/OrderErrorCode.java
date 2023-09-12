package com.github.messageconsumer.service.order.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderErrorCode {
    //status(HttpStatus.badRequest) 400
    INVALID_QUANTITY("수량을 확인해주세요."),

    //status(HttpStatus.NOT_FOUND) 404
    USER_NOT_FOUND("존재하지 않는 유저 입니다."),
    THIS_PRODUCT_DOES_NOT_EXIST("존재하지 않는 상품 입니다."),
    THIS_ORDER_DOES_NOT_EXIST("존재하지 않는 주문 입니다."),

    //status(HttpStatus.CONFLICT) 409
    OUT_OF_STOCK("상품이 품절되었습니다."),
    LACK_OF_STOCK("재고가 부족합니다"),
    PRODUCT_ALREADY_EXISTS("장바구니에 이미 존재하는 상품입니다.");

    private final String description;
}
