package com.anhnguyen.multilevelauthenticator.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.anhnguyen.multilevelauthenticator.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

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

    /////////////////////////////////////// Biometric utils /////////////////////////////////
    public static boolean isBiometricPromptEnabled() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P);
    }

    /*
     * Condition I: Check if the android version in device is greater than
     * Marshmallow, since fingerprint authentication is only supported
     * from Android 6.0.
     * Note: If your project's minSdkversion is 23 or higher,
     * then you won't need to perform this check.
     *
     * */
    public static boolean isSdkVersionSupportedFingerprint() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    /*
     * Condition II: Check if the device has fingerprint sensors.
     * Note: If you marked android.hardware.fingerprint as something that
     * your app requires (android:required="true"), then you don't need
     * to perform this check.
     *
     * */
    public static boolean isHardwareSupportedFingerprint(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.isHardwareDetected();
    }

    /*
     * Condition III: Fingerprint authentication can be matched with a
     * registered fingerprint of the user. So we need to perform this check
     * in order to enable fingerprint authentication
     *
     * */
    public static boolean isFingerprintAvailable(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.hasEnrolledFingerprints();
    }

    /*
     * Condition IV: Check if the permission has been added to
     * the app. This permission will be granted as soon as the user
     * installs the app on their device.
     *
     * */
    public static boolean isPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) ==
                PackageManager.PERMISSION_GRANTED;
    }
}
