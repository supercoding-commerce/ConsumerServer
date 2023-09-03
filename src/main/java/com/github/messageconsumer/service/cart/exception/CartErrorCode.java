package com.github.messageconsumer.service.cart.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CartErrorCode {
    //status(HttpStatus.badRequest) 400
    INVALID_QUANTITY("수량을 확인해주세요.", HttpStatus.BAD_REQUEST),

    //status(HttpStatus.NOT_FOUND) 404
    USER_NOT_FOUND("존재하지 않는 유저 입니다.", HttpStatus.NOT_FOUND),
    THIS_PRODUCT_DOES_NOT_EXIST("존재하지 않는 상품 입니다.", HttpStatus.NOT_FOUND),
    THIS_CART_DOES_NOT_EXIST("존재하지 않는 장바구니 입니다.", HttpStatus.NOT_FOUND),

    //status(HttpStatus.CONFLICT) 409
    OUT_OF_STOCK("상품이 품절되었습니다.", HttpStatus.CONFLICT),
    PRODUCT_ALREADY_EXISTS("장바구니에 이미 존재하는 상품입니다.", HttpStatus.CONFLICT);

    private final String description;
    private final HttpStatus httpStatus;
}
