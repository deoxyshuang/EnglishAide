package com.bear.englishaide;

import android.content.Context;

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
    static public int findKeyByVal(int[] ary, int val) {
        for(int i=0;i<ary.length;i++){
            if(ary[i]==val) return i;
        }
        return -1;
    }
}
