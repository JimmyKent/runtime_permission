package com.jimmy.runtimepermission;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jimmy.permission.AndPermission;
import com.jimmy.permission.IAgainApplyListener;
import com.jimmy.permission.IApplyCallback;
import com.jimmy.permission.IFirstApplyListener;
import com.jimmy.permission.IPermissionCallback;

/**
 * 申请一个权限有提示
 * 申请多个权限有提示
 */
public class AndPermissionAty extends AppCompatActivity implements View.OnClickListener {

    private String TAG = AndPermissionAty.class.getSimpleName();

    private int REQUEST_CODE_SMS_CAMERA = 100;
    private int REQUEST_CODE_SMS_BLUETOOTH = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_andpermission);
        findViewById(R.id.btn_a_permission).setOnClickListener(this);
        findViewById(R.id.btn_many_permission).setOnClickListener(this);
        findViewById(R.id.btn_many_permission_toast).setOnClickListener(this);

        getSupportFragmentManager().beginTransaction().add(R.id.parent_fragment, new ParentFragment(), "ParentFragment").commit();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_a_permission:
                AndPermission.with(this)
                        .requestCode(REQUEST_CODE_SMS_BLUETOOTH)
                        .permission(Manifest.permission.BLUETOOTH)
                        .send();
                break;
            case R.id.btn_many_permission:
                AndPermission.with(this)
                        .requestCode(REQUEST_CODE_SMS_CAMERA)
                        .permission(Manifest.permission.READ_SMS, Manifest.permission.CAMERA)
                        .send();
                break;
            case R.id.btn_many_permission_toast:
                AndPermission.with(this)
                        .requestCode(REQUEST_CODE_SMS_CAMERA)
                        .permission(Manifest.permission.READ_SMS, Manifest.permission.CAMERA)
                        .setFirstApplyListener(mIFirstApplyListener)
                        .setAgainApplyListener(mIAgainApplyListener)
                        .send();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Activity中没有Fragment，这句话可以注释。
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.e("jimmy", "ScreenSwitchAty onRequestPermissionsResult");

        // 没有Listener，最后的PermissionListener参数不写。
        // AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);

        // 有Listener，最后需要写PermissionListener参数。
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, new IPermissionCallback() {
            @Override
            public void onSucceed(int requestCode) {
                if (requestCode == REQUEST_CODE_SMS_CAMERA)
                    Toast.makeText(AndPermissionAty.this, "申请信息，相机权限成功", Toast.LENGTH_SHORT).show();
                else if (requestCode == REQUEST_CODE_SMS_BLUETOOTH)
                    Toast.makeText(AndPermissionAty.this, "申请蓝牙权限成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int requestCode, String... denyPermissions) {
                if (requestCode == REQUEST_CODE_SMS_CAMERA)
                    Toast.makeText(AndPermissionAty.this, "申请信息，相机权限失败", Toast.LENGTH_SHORT).show();
                else if (requestCode == REQUEST_CODE_SMS_BLUETOOTH)
                    Toast.makeText(AndPermissionAty.this, "申请蓝牙权限失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private IFirstApplyListener mIFirstApplyListener = new IFirstApplyListener() {
        @Override
        public void firstApply(final IApplyCallback callback) {
            new AlertDialog.Builder(AndPermissionAty.this)
                    .setTitle("第一次申请权限")
                    .setMessage("该功能使用到以下权限：短信权限，相机权限。")
                    .setPositiveButton("好，给你", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            callback.ok();
                        }
                    })
                    .setNegativeButton("我拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.cancel();
                            dialog.cancel();
                        }
                    }).show();
        }
    };

    private IAgainApplyListener mIAgainApplyListener = new IAgainApplyListener() {
        @Override
        public void againApply(final String[] denyPermissions, final IApplyCallback callback) {
            StringBuffer sb = new StringBuffer();
            for (String p : denyPermissions) {
                sb.append(p + ",");
            }
            new AlertDialog.Builder(AndPermissionAty.this)
                    .setTitle("友好提醒")
                    .setMessage("您已拒绝过" + sb.toString() + "请把权限赐给我吧！")
                    .setPositiveButton("好，给你", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            callback.ok();
                        }
                    })
                    .setNegativeButton("我拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.cancel();
                            dialog.cancel();
                        }
                    }).show();
        }
    };
}
