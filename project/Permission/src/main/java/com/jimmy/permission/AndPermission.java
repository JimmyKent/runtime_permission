package com.jimmy.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.jimmy.permission.annotation.PermissionFailed;
import com.jimmy.permission.annotation.PermissionSucceed;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinguochong on 16-10-8.
 * 入口
 * http://blog.csdn.net/yanzhenjie1003/article/details/52503533
 * https://blog.coding.net/blog/understanding-marshmallow-runtime-permission
 * http://www.jianshu.com/p/2746a627c6d2
 *
 *
 */

public class AndPermission {

    public static Permission with(Activity activity) {
        return new PermissionImpl(activity);
    }

    public static Permission with(Fragment fragment) {
        return new PermissionImpl(fragment);
    }

    public static void onRequestPermissionsResult(Object o, int requestCode, String[] permissions, int[] grantResults, IPermissionCallback callback) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++)
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                deniedPermissions.add(permissions[i]);
        if (callback == null) {
            //注解回调
            // XXX 如果ParentFragment 和SubFragment 使用了相同的code
            annotationCallback(o, deniedPermissions.size() > 0 ? PermissionFailed.class : PermissionSucceed.class, requestCode);
        } else if (deniedPermissions.size() > 0) {
            callback.onFailed(requestCode, deniedPermissions.toArray(new String[deniedPermissions.size()]));
        } else {
            callback.onSucceed(requestCode);
        }

        //实现Fragment嵌套穿透
        if (o instanceof Fragment) {
            Fragment parentFragment = (Fragment) o;
            List<Fragment> fragments = parentFragment.getChildFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment != null) {
                        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    }
                }
            }
        }
    }

    private static <T extends Annotation> void annotationCallback(Object o, Class<T> clazz, int requestCode) {

        Method[] methods = findMethodForRequestCode(o.getClass(), clazz, requestCode);
        //methods 包括 PermissionSucceed PermissionFailed
        try {
            for (Method method : methods) {
                if (!method.isAccessible()) method.setAccessible(true);
                method.invoke(o, null);
            }
        } catch (Exception e) {
            Log.e("AndPermission", "Callback methods fail.");
        }
    }

    private static <T extends Annotation> Method[] findMethodForRequestCode(Class<?> source, Class<T> annotation, int requestCode) {
        List<Method> methods = new ArrayList<>(1);
        for (Method method : source.getDeclaredMethods())
            if (method.isAnnotationPresent(annotation))
                if (isSameRequestCode(method, annotation, requestCode))
                    methods.add(method);
        return methods.toArray(new Method[methods.size()]);
    }

    private static <T extends Annotation> boolean isSameRequestCode(Method method, Class<T> annotation, int requestCode) {
        if (PermissionSucceed.class.equals(annotation))
            return method.getAnnotation(PermissionSucceed.class).value() == requestCode;
        else if (PermissionFailed.class.equals(annotation))
            return method.getAnnotation(PermissionFailed.class).value() == requestCode;
        return false;
    }

    private AndPermission() {
    }

    /* XXX http://www.jianshu.com/p/2746a627c6d2
        特殊权限，有两个
        SYSTEM_ALERT_WINDOW，设置悬浮窗
        WRITE_SETTINGS 修改系统设置
        使用startActivityForResult启动授权界面来完成

        http://www.jianshu.com/p/2746a627c6d2
    */

   /* public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    @TargetApi(Build.VERSION_CODES.M)
    public void requestDrawOverLays() {
        if (!Settings.canDrawOverlays(MainActivity.this)) {
            Toast.makeText(this, "can not DrawOverlays", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + MainActivity.this.getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        } else {
            // Already hold the SYSTEM_ALERT_WINDOW permission, do addview or something.
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // SYSTEM_ALERT_WINDOW permission not granted...
                Toast.makeText(this, "Permission Denieddd by user.Please Check it in Settings", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Allowed", Toast.LENGTH_SHORT).show();
                // Already hold the SYSTEM_ALERT_WINDOW permission, do addview or something.
            }
        }
    }*/
}
