package com.bear.englishaide;

import java.util.Arrays;

public enum  VocabType {

    ALL(0),
    WORD(1),
    PHRASE(2);

    private int key;

    VocabType(int key) {
        this.key = key;
    }
    public int getKey() {
        return key;
    }
    public static VocabType getType(int key) {
        return Arrays.asList(VocabType.values()).stream().filter(x->x.getKey()==key).findFirst().get();
    }
}
