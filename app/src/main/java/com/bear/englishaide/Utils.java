package com.bear.englishaide;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

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
    static public void showDelDialog(Context context, IDialogListener iDialogListener, String word){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.delConfirm, word));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                iDialogListener.onClickPositiveButton();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                iDialogListener.onClickNegativeButton();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
