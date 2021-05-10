package com.bear.englishaide;

import java.util.ArrayList;

class Word {
    int fami=1;
    long id;
    //@SerializedName("Name")
    String word;
    ArrayList<Mean> meanList;

    void meanListInit(){
        meanList = new ArrayList<Mean>();
    }
}
