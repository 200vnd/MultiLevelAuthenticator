package com.anhnguyen.multilevelauthenticator.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.anhnguyen.multilevelauthenticator.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

public class MyUtils  {

    public static void ToastCustomSuccess(Activity context) {
        View toastView = context.getLayoutInflater().inflate(R.layout.toast_custom,
                (ViewGroup) context.findViewById(R.id.toast_layout_root));

        final ImageView imageView = toastView.findViewById(R.id.imgToastGif);
        Glide.with(toastView).load(R.drawable.clap).into(new DrawableImageViewTarget(imageView));


        Toast toast = new Toast(context.getApplicationContext());
        // Set custom view in toast.
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0,0);
        toast.show();
    }
}
