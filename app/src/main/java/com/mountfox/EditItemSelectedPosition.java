package com.mountfox;

import android.util.Log;

/**
 * Created by RITS on 7/12/2016.
 */
public class EditItemSelectedPosition {
public static int selecteditem;
    public  EditItemSelectedPosition(int ii)
    {
        selecteditem=ii;
        Log.d("Item has selected","position is ::"+selecteditem);
    }

    public static int getSelecteditem()
    {
        return selecteditem;
    }
}
