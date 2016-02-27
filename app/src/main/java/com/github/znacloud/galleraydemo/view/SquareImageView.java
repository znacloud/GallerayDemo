package com.github.znacloud.galleraydemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.github.znacloud.galleraydemo.presentor.ImageLoader;

/**
 * Created by Stephan on 2016/1/24.
 */
public class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
