package com.jimmy.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinguochong on 16-10-11.
 */
public class PermissionImpl implements Permission {

    private Activity activity;
    private Fragment fragment;
    private List<String> notApplyPermissions;
    private List<String> deniedPermissions;

    private String[] permissions;//所有的权限
    private int[] permissionStatus;//对应permissions的授权状态

    private int requestCode;

    private boolean isFirstApply = true;

    private IFirstApplyListener firstApplyListener;
    private IAgainApplyListener againApplyListener;

    PermissionImpl(Activity activity) {
        this.activity = activity;
    }

    PermissionImpl(Fragment fragment) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
    }

    @Override
    public PermissionImpl permission(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("The permissions can not be null.");
        }
        this.permissions = permissions;
        permissionStatus = new int[permissions.length];
        return this;
    }

    @Override
    public PermissionImpl requestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    @Override
    public void send() {
        checkSelfPermissions();
    }

    @Override
    public PermissionImpl setFirstApplyListener(IFirstApplyListener firstApplyListener) {
        this.firstApplyListener = firstApplyListener;
        return this;
    }

    @Override
    public PermissionImpl setAgainApplyListener(IAgainApplyListener againApplyListener) {
        this.againApplyListener = againApplyListener;
        return this;
    }


    private void checkSelfPermissions() {
        notApplyPermissions = new ArrayList<>();
        String permission;
        for (int i = 0; i < permissions.length; i++) {
            permission = permissions[i];
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                notApplyPermissions.add(permission);
                permissionStatus[i] = PackageManager.PERMISSION_DENIED;
            }
        }
        if (notApplyPermissions.size() == 0) {
            //callback success
            callback();
            return;
        }
        checkDeniedPermission();
    }

    private void checkDeniedPermission() {
        deniedPermissions = new ArrayList<>();
        for (String p : notApplyPermissions) {
            isFirstApply(p);
        }
        toast2User();
    }

    private void isFirstApply(String permission) {
        //既有第一次申请又有再次申请的情况：弹出申请多个权限的窗口，部分点击了拒绝，这时候杀进程，再次进入
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            //只要拒绝过一次就不是第一次申请权限
            isFirstApply = false;
            deniedPermissions.add(permission);
        }
    }

    private void toast2User() {
        if (isFirstApply) {
            for (int i = 0; i < permissionStatus.length; i++) {
                permissionStatus[i] = PackageManager.PERMISSION_DENIED;
            }
            firstApply();
        } else {
            againApply();
        }
    }

    private void firstApply() {
        if (firstApplyListener != null) {
            firstApplyListener.firstApply(new IApplyCallback() {
                @Override
                public void ok() {
                    request();
                }

                @Override
                public void cancel() {
                    callback();
                }
            });
            return;
        }
        request();
    }

    private void againApply() {
        if (againApplyListener != null) {
            String[] dps = deniedPermissions.toArray(new String[deniedPermissions.size()]);
            againApplyListener.againApply(dps, new IApplyCallback() {
                @Override
                public void ok() {
                    request();
                }

                @Override
                public void cancel() {
                    callback();
                }
            });
            return;
        }
        request();
    }

    private void request() {
        if (fragment != null) {
            fragment.requestPermissions(notApplyPermissions.toArray(new String[notApplyPermissions.size()]), requestCode);
            return;
        }
        ActivityCompat.requestPermissions(activity,
                notApplyPermissions.toArray(new String[notApplyPermissions.size()]),
                requestCode);
    }

    private void callback() {//需要自建回调
        if (fragment != null) {
            fragment.onRequestPermissionsResult(requestCode, permissions, permissionStatus);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.onRequestPermissionsResult(requestCode, permissions, permissionStatus);
       /* }else if (activity instanceof ActivityCompat.OnRequestPermissionsResultCallback)
            ((ActivityCompat.OnRequestPermissionsResultCallback) activity).onRequestPermissionsResult(requestCode, permissions, permissionStatus);*/
        }

    }
}
