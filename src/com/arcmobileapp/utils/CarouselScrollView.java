package com.arcmobileapp.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class CarouselScrollView extends HorizontalScrollView {

	  
    
	private ScrollViewListener scrollViewListener = null;
    public CarouselScrollView(Context context) {
        super(context);
    }

    public CarouselScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CarouselScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

}
