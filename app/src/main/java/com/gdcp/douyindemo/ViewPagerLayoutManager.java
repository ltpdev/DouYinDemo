package com.gdcp.douyindemo;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by asus- on 2018/6/5.
 */

public class ViewPagerLayoutManager extends LinearLayoutManager {
    private static final String TAG= "ViewPagerLayoutManager";
    //实现一次只能滑动一个Item，也就是一页。
    private PagerSnapHelper pagerSnapHelper;
    private OnViewPagerListener onViewPagerListener;
    private RecyclerView recyclerView;
    //位移，用来判断移动方向
    private int drift;
    public ViewPagerLayoutManager(Context context) {
        super(context);
        init();
    }

    public ViewPagerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init();
    }
    public ViewPagerLayoutManager(Context context, int orientation) {
        super(context, orientation,false);
        init();
    }

    public ViewPagerLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        pagerSnapHelper=new PagerSnapHelper();
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        pagerSnapHelper.attachToRecyclerView(view);
        this.recyclerView=view;
        recyclerView.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener);
    }
    /**
     * 布局完成后调用
     * @param state
     */
    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (onViewPagerListener != null) onViewPagerListener.onLayoutComplete();
    }


    @Override
    public void onScrollStateChanged(int state) {
        switch (state){
            //空闲
            case RecyclerView.SCROLL_STATE_IDLE:
                View viewIdle=pagerSnapHelper.findSnapView(this);
                int positionIdle=getPosition(viewIdle);
                if (onViewPagerListener != null && getChildCount() == 1) {
                    //
                    onViewPagerListener.onPageSelected(positionIdle,positionIdle == getItemCount() - 1);
                }
                break;
                //拖动
            case RecyclerView.SCROLL_STATE_DRAGGING:
                View viewDrag = pagerSnapHelper.findSnapView(this);
                int positionDrag = getPosition(viewDrag);
                break;
                //要移动到最后位置时
            case RecyclerView.SCROLL_STATE_SETTLING:
                View viewSettling = pagerSnapHelper.findSnapView(this);
                int positionSettling = getPosition(viewSettling);
                break;
        }
        //super.onScrollStateChanged(state);
    }

    /**
     * 设置监听
     * @param listener
     */
    public void setOnViewPagerListener(OnViewPagerListener listener){
        this.onViewPagerListener = listener;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.drift = dx;
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.drift = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }
    private RecyclerView.OnChildAttachStateChangeListener mChildAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {

        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
            if (drift >= 0){
                if (onViewPagerListener != null) onViewPagerListener.onPageRelease(true,getPosition(view));
            }else {
                if (onViewPagerListener != null) onViewPagerListener.onPageRelease(false,getPosition(view));
            }

        }
    };

}
