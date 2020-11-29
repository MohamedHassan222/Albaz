package com.user.albaz.rafiq.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;


public class LoadingDialog extends ProgressDialog {

    public LoadingDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setIndeterminate(true);
        setMessage("Please wait...");
    }
}
