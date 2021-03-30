package com.bear.englishaide;

import java.util.ArrayList;

class Word {
    int fami=1;
    int id;
    //@SerializedName("Name")
    String word;
    ArrayList<Mean> meanList;

    void meanListInit(){
        meanList = new ArrayList<Mean>();
    }
}
