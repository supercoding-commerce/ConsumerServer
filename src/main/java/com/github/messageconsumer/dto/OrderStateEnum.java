package com.github.messageconsumer.dto;

public enum OrderStateEnum {
    결제대기(1, "결제대기"),
    결제완료(2, "결제완료"),
    배송대기(3, "배송대기"),
    배송중(4, "배송중"),
    배송완료(5, "배송완료"),

    주문실패(6, "주문실패");

    private final int value;
    private final String label;

    OrderStateEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getByCode(int code) {
        switch(code){
            case 1: return 결제대기.label;
            case 2: return 결제완료.label;
            case 3: return 배송대기.label;
            case 4: return 배송중.label;
            case 5: return 배송완료.label;
            case 6: return 주문실패.label;
            default: return 결제대기.label;
        }
    }
}
