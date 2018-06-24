package com.zhou.ffmpegdemo.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhou.ffmpegdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhou on 2018/5/12.
 */

public class RecycleViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);
        RecyclerView recycleView = findViewById(R.id.recycle_view);
        recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
               // Log.e("test", "newState:" + newState);
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
               // Log.e("test", "dx:" + dx + ",dy:" + dy);

                super.onScrolled(recyclerView, dx, dy);
            }
        });
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            data.add("test数据" + i);
        }
        LinearLayoutManager layout = new MyLayoutManager(this, OrientationHelper.VERTICAL, false);

        recycleView.setLayoutManager(layout);
        recycleView.setAdapter(new MyRecycleViewAdapter(data));

    }

    class MyRecycleViewAdapter extends RecyclerView.Adapter {

        private final List<String> mdata;

        public MyRecycleViewAdapter(List<String> data) {
            mdata = data;
        }

        int i = 0;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item, parent, false);
            itemView.setTag(i);
            i++;
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder h = (MyViewHolder) holder;
            h.mTextView.setText(mdata.get(position));
        }

        @Override
        public int getItemCount() {
            return mdata == null ? 0 : mdata.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private final TextView mTextView;

            public MyViewHolder(View itemView) {
                super(itemView);
                mTextView = itemView.findViewById(R.id.tv_test);
            }
        }

    }


    class MyLayoutManager extends LinearLayoutManager {


        private int offsetRotate;

        public MyLayoutManager(Context context) {
            super(context);
        }

        public MyLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public MyLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }


        @Override
        public boolean canScrollHorizontally() {
            return super.canScrollHorizontally();
        }

        @Override
        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {

            return super.scrollHorizontallyBy(dx, recycler, state);
        }

        @Override
        public boolean canScrollVertically() {
            return super.canScrollVertically();
        }



        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            View viewForPosition = recycler.getViewForPosition(0);
            int tag = (int) viewForPosition.getTag();
            int itemCount = getItemCount();
            Log.e("test", "frist:" + tag+",itemcount:"+itemCount);

            super.onLayoutChildren(recycler, state);
           // detachAndScrapAttachedViews(recycler);

        }

    }
}
