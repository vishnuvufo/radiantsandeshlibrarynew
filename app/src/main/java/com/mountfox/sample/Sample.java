package com.mountfox.sample;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.mountfox.R;

public class Sample extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_entercpin);
    }
}
