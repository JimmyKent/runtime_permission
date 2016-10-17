package com.jimmy.runtimepermission;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jimmy.permission.AndPermission;
import com.jimmy.permission.IPermissionCallback;
import com.jimmy.permission.annotation.PermissionFailed;
import com.jimmy.permission.annotation.PermissionSucceed;

/**
 * Created by jinguochong on 16-9-23.
 */
public class SubFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "jimmy SubFragment";
    private static final int REQUEST_ACCESS_FINE_LOCATION = 112;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sub, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_request_single).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request_single: {
                requestCameraPermission();
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, //null
                new IPermissionCallback() {
            @Override
            public void onSucceed(int requestCode) {
                if (requestCode == REQUEST_ACCESS_FINE_LOCATION)
                    Toast.makeText(getActivity(), "申请定位权限成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int requestCode, String... denyPermissions) {
                if (requestCode == REQUEST_ACCESS_FINE_LOCATION)
                    Toast.makeText(getActivity(), "申请定位权限失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void requestCameraPermission() {
        AndPermission.with(this)
                .requestCode(REQUEST_ACCESS_FINE_LOCATION)
                .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                .send();
    }

    @PermissionSucceed(REQUEST_ACCESS_FINE_LOCATION)
    private void getLocationYes() {
        Toast.makeText(getActivity(), "申请定位权限成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFailed(REQUEST_ACCESS_FINE_LOCATION)
    private void getLocationNo() {
        Toast.makeText(getActivity(), "申请定位权限失败", Toast.LENGTH_SHORT).show();
    }
}
