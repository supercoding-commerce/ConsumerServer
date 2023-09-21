package com.github.messageconsumer.dto;

public enum CartStateEnum {
    주문대기(0, "주문대기"),
    결제대기(1, "결제대기"),
    결제완료(2, "결제완료"),
    등록보류(3, "장바구니 등록 보류");

    private final int value;
    private final String label;

    CartStateEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getByCode(int code) {
        switch(code){
            case 0: return 주문대기.label;
            case 1: return 결제대기.label;
            case 2: return 결제완료.label;
            case 3: return 등록보류.label;
            default: return 주문대기.label;
        }
    }
}