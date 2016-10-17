package com.jimmy.permission;

/**
 * Created by jinguochong on 16-10-8.
 */
public interface IPermissionCallback {
    void onSucceed(int requestCode);

    void onFailed(int requestCode, String... denyPermissions);
}
