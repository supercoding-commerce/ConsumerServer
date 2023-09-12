package com.github.messageconsumer.dto;

public enum CartStateEnum {
    주문대기(0, "주문대기"),
    주문완료(1, "주문완료"),
    주문실패(2, "주문실패");

    private final int value;
    private final String label;

    CartStateEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getByCode(int code) {
        switch(code){
            case 0: return 주문대기.label;
            case 1: return 주문완료.label;
            case 2: return 주문실패.label;
            default: return 주문대기.label;
        }
    }
}
