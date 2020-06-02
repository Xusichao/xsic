package com.xsic.xsic.ui.contacts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xsic.xsic.R;
import com.xsic.xsic.base.BaseDataBindingFragment;
import com.xsic.xsic.databinding.FragmentContactsBinding;
import com.xsic.xsic.utils.LogUtil;

public class ContactsFragment extends BaseDataBindingFragment<FragmentContactsBinding,ContactsViewModel> {
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_contacts;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void registerClick() {

    }

    @Override
    protected void subscribeUi(ContactsViewModel viewModel) {

    }

    @Override
    protected void touchToRefresh() {

    }

    @Override
    public void onFragmentVisible() {

    }

    @Override
    public void onClick(View v) {

    }








    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        LogUtil.d("LifeCycleTest",getClass().getName()+" === onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("LifeCycleTest",getClass().getName()+" === onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d("LifeCycleTest",getClass().getName()+" === onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.d("LifeCycleTest",getClass().getName()+" === onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d("LifeCycleTest",getClass().getName()+" === onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d("LifeCycleTest",getClass().getName()+" === onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d("LifeCycleTest",getClass().getName()+" === onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("LifeCycleTest",getClass().getName()+" === onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.d("LifeCycleTest",getClass().getName()+" === onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.d("LifeCycleTest",getClass().getName()+" === onDetach");
    }
}
