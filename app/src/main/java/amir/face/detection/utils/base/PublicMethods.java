package amir.face.detection.utils.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.view.Display;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class PublicMethods {
    public static final String LOG_TAG = "mlkit_tag";
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

    public static float getScreenWidth(Activity mActivity){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }
    public static float getScreenHeight(Activity mActivity){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

}
