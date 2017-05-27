package com.sakurafish.pockettoushituryou.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.ViewWebviewBinding;
import com.sakurafish.pockettoushituryou.viewmodel.WebViewViewModel;

import javax.inject.Inject;

import static com.sakurafish.pockettoushituryou.view.activity.WebViewActivity.EXTRA_URL;

public class WebViewFragment extends BaseFragment {

    public static final String TAG = WebViewFragment.class.getSimpleName();

    @Inject
    WebViewViewModel viewModel;

    private ViewWebviewBinding binding;

    public static WebViewFragment newInstance(@NonNull String url) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    public WebViewFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_webview, container, false);
        binding = DataBindingUtil.bind(view);
        binding.setViewModel(viewModel);

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setUrl(getArguments().getString(EXTRA_URL, ""));
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.webView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.webView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.webView.destroy();
        viewModel.destroy();
    }
}
