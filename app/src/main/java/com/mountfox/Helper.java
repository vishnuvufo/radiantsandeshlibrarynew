package com.mountfox;

import android.content.Context;
import android.widget.Toast;

public class Helper {
	public static void showLongToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
}
