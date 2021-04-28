package com.bear.englishaide;

import java.util.Arrays;

public enum SortType {

    NEW_TO_OLD(0),
    OLD_TO_NEW(1),
    A_TO_Z(2),
    Z_TO_A(3);

    private int key;

    SortType(int key) {
        this.key = key;
    }
    public int getKey() {
        return key;
    }
    public static SortType getType(int key) {
        return Arrays.asList(SortType.values()).stream().filter(x->x.getKey()==key).findFirst().get();
    }
}
