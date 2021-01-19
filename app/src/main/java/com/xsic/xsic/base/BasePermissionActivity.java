package com.xsic.xsic.base;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.ViewDataBinding;


public abstract class BasePermissionActivity<T extends ViewDataBinding, E extends BaseViewModel> extends BaseDataBindingActivity<T, E> {


    protected static final int REQ = 100;
    protected static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.CAMERA
    };

    private PermissionCallBack mPermissionCallBack;

    protected interface PermissionCallBack{
        void onGranted();
        void onDenied();
        void onChecked();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void requestOrCheckPermission(Context context, String[] permissions, PermissionCallBack permissionCallBack){
        if (Build.VERSION.SDK_INT < 23 || permissions.length == 0) {
            if (mPermissionCallBack != null){
                mPermissionCallBack.onChecked();
            }
        }else {
            if (permissionCallBack != null){
                mPermissionCallBack = permissionCallBack;
                requestPermissions(permissions,REQ);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionCallBack!=null && requestCode == REQ){
            for (int i=0;i<grantResults.length;i++){
                if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                    //任意一个权限不允许都会回调deny
                    mPermissionCallBack.onDenied();
                    return;
                }
            }
            mPermissionCallBack.onGranted();
        }
    }
}
