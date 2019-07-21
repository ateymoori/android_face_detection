package amir.face.detection.utils.base;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.Display;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PublicMethods {
    private static final int PERMISSION_REQUESTS = 1;

    private static String[] getRequiredPermissions(Activity mActivity) {
        try {
            PackageInfo info =
                    mActivity.getPackageManager()
                            .getPackageInfo(mActivity.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    public static boolean allPermissionsGranted(Activity mActivity) {
        for (String permission : getRequiredPermissions(mActivity)) {
            if (!isPermissionGranted(mActivity, permission)) {
                return false;
            }
        }
        return true;
    }

    public static void getRuntimePermissions(Activity mActivity) {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions(mActivity)) {
            if (!isPermissionGranted(mActivity, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    mActivity, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static float getScreenWidth(Activity mActivity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public static float getScreenHeight(Activity mActivity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }


    public static String saveToInternalStorage(Bitmap bitmapImage,String fileName, Context mContext) {
        File directory = mContext.getDir("imageDir", Context.MODE_PRIVATE);
        File imgPath = new File(directory, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imgPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static Bitmap getBitmapByPath(String path , String fileName) {
        try {
            File f=new File(path, fileName);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

}
