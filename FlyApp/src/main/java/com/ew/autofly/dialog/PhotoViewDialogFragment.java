package com.ew.autofly.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.ew.autofly.R;
import com.github.chrisbanes.photoview.PhotoView;


public class PhotoViewDialogFragment extends DialogFragment {
    private PhotoView pvMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            if (dm.widthPixels > dm.heightPixels)
                dialog.getWindow().setLayout((int) (dm.widthPixels * 0.7), (int) (dm.heightPixels * 0.9));
            else
                dialog.getWindow().setLayout((int) (dm.heightPixels * 0.7), (int) (dm.widthPixels * 0.9));
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCanceledOnTouchOutside(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_photo_view, container, false);
        pvMap = (PhotoView) view.findViewById(R.id.pv_map);
        String path = getArguments().getString("path");
        if (null != path)
            Glide.with(getContext()).load(path).error(R.drawable.default_mission_photo).into(pvMap);
        return view;
    }
}
