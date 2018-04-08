package com.boscotec.medmanager.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.boscotec.medmanager.R;

/**
 * Created by Johnbosco on 24-Mar-18.
 */
public class RecyclerItemDivider extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public RecyclerItemDivider(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.recycler_line_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft() + 20;
        int right = parent.getWidth() - parent.getPaddingRight() - 30 ;

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}