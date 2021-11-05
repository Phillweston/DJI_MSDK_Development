package com.ew.autofly.dialog.common;

import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ew.autofly.R;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.utils.io.file.FileInfo;
import com.ew.autofly.utils.io.file.FileUtils;
import com.flycloud.autofly.base.util.DensityUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.mrapp.android.dialog.MaterialDialog;

import static android.widget.RelativeLayout.CENTER_VERTICAL;
import static android.widget.RelativeLayout.END_OF;


public abstract class BaseSortFileDialog extends BaseDialogFragment {

    private int mSortCondition = FileUtils.SortCondition.TIME;
    private boolean isSortAscent = false;

    @Override
    protected void initTitleBar(View rootView) {
        super.initTitleBar(rootView);
        addSortButton();
    }

    private void addSortButton() {
        ImageView sortBtn = new ImageView(getContext());
        int size = DensityUtils.dip2px(getContext(), 25);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size, size);
        layoutParams.setMarginStart(DensityUtils.dip2px(getContext(), 10));
        layoutParams.addRule(END_OF, R.id.tv_title);
        layoutParams.addRule(CENTER_VERTICAL);
        sortBtn.setLayoutParams(layoutParams);
        sortBtn.setImageResource(R.drawable.ic_sort);
        addTitleView(sortBtn);
        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortFileDialog();
            }
        });
    }

    private void showSortFileDialog() {

        CharSequence[] list = {getString(R.string.sort_file_by_time), getString(R.string.sort_file_by_name)};
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext(), R.style.TransparentDialogTheme);
        builder.setItemColor(getResources().getColor(R.color.black));
        builder.setTitle(R.string.sort_file);
        builder.setTitleColor(getResources().getColor(R.color.black));
        builder.setSingleChoiceItems(list, mSortCondition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int position) {
                mSortCondition = position;
            }
        });
        builder.setNegativeButton(R.string.sort_file_descent, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isSortAscent = false;
                sortFiles();
            }
        });
        builder.setPositiveButton(R.string.sort_file_ascent, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                isSortAscent = true;
                sortFiles();
            }
        });
        builder.create().show();
    }

    protected List<FileInfo> getSortFiles(String path) {
        List<FileInfo> allFiles = new ArrayList<>();
        File folder = new File(path);
        File[] files = FileUtils.fileFilter(folder);
        if (files != null) {
            allFiles = FileUtils.getFileInfosFromFileArray(files, mSortCondition, isSortAscent);
        }
        return allFiles;
    }

    protected abstract void sortFiles();
}
