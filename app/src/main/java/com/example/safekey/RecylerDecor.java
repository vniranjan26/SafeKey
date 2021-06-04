package com.example.safekey;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecylerDecor extends RecyclerView.ItemDecoration {
    int sidePadding, topPadding;

    public RecylerDecor(int sidePadding, int topPadding)  {
        this.sidePadding = sidePadding;
        this.topPadding = topPadding;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = topPadding;
        outRect.top = topPadding;
        outRect.left = sidePadding;
        outRect.right = sidePadding;

    }
}
