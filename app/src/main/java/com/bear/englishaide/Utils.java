package com.bear.englishaide;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * 共用函式庫
 */
class Utils {
    /*static public float convertPxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }*/
    static public float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    static public List<Integer> intArrayToList(int[] ary) {
        List<Integer> intList = new ArrayList(ary.length);
        for (int i : ary) {
            intList.add(i);
        }
        return intList;
    }
}
