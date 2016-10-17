package com.jimmy.runtimepermission;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jimmy.permission.AndPermission;
import com.jimmy.permission.IPermissionCallback;

/**
 * Created by jinguochong on 16-9-23.
 */
public class ParentFragment extends Fragment {

    private static final String TAG = "jimmy ParentFragment";
    private static final int REQUEST_CALENDAR = 111;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parent, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // view.findViewById(R.id.ll_parent_content);
        getFragmentManager().beginTransaction().add(R.id.ll_parent_content, new SubFragment(), "SubFragment").commit();

        final int sub = R.id.ll_parent_content;
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new SubFragment();
        ft.add(sub, fragment, "SubFragment");
        ft.commitAllowingStateLoss();

        view.findViewById(R.id.btn_request_single).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestCameraPermission();

                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("jimmy","ParentFragment onRequestPermissionsResult");
        doNext(requestCode, permissions, grantResults);

        //这里使用相同的requestCode是可以穿透的，穿透在Module里面做了
        /*List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }*/
    }

    private void doNext(int requestCode, String[] permissions, int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, new IPermissionCallback() {
            @Override
            public void onSucceed(int requestCode) {
                if (requestCode == REQUEST_CALENDAR)
                    Toast.makeText(getActivity(), "申请日历权限成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int requestCode, String... denyPermissions) {
                if (requestCode == REQUEST_CALENDAR)
                    Toast.makeText(getActivity(), "申请日历权限失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestCameraPermission() {
        AndPermission.with(this)
                .requestCode(REQUEST_CALENDAR)
                .permission(Manifest.permission.READ_CALENDAR)
                .send();
    }


}
