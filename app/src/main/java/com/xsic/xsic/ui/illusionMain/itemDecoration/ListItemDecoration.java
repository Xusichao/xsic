package com.xsic.xsic.ui.illusionMain.itemDecoration;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.xsic.xsic.R;

public class ListItemDecoration extends RecyclerView.ItemDecoration {
    private Context mContext;

    public ListItemDecoration(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();

        if (lp.getSpanIndex() == 0) {
            outRect.right = (int) mContext.getResources().getDimension(R.dimen.dp_4);
        }else if (lp.getSpanIndex() == 1){
            outRect.left = (int) mContext.getResources().getDimension(R.dimen.dp_4);
        }

    }
}
