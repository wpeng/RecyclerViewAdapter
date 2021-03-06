package com.othershe.recyclerviewadapter;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.othershe.baseadapter.interfaces.OnItemClickListener;
import com.othershe.baseadapter.interfaces.OnLoadMoreListener;
import com.othershe.baseadapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CommonItemActivity extends AppCompatActivity {

    private CommonRefreshAdapter mAdapter;

    private RecyclerView mRecyclerView;

    private boolean isFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        //初始化adapter
        mAdapter = new CommonRefreshAdapter(this, null, true);

        //初始化EmptyView
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_layout, (ViewGroup) mRecyclerView.getParent(), false);
        mAdapter.setEmptyView(emptyView);

        //初始化 开始加载更多的loading View
        mAdapter.setLoadingView(R.layout.load_loading_layout);
        mAdapter.setLoadingHeaderView(R.layout.load_loading_layout);
        //加载失败，更新footer view提示
        mAdapter.setLoadFailedView(R.layout.load_failed_layout);
        mAdapter.setLoadHeaderFailedView(R.layout.load_failed_layout);
        //加载完成，更新footer view提示
        mAdapter.setLoadEndView(R.layout.load_end_layout);
        mAdapter.setLoadHeaderEndView(R.layout.load_end_layout);

        //设置加载更多触发的事件监听
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(boolean isReload) {
                loadMore();
            }

            @Override
            public void onLoadHeaderMore() {
                loadPreviousMore();
            }
        });

        //设置item点击事件监听
        mAdapter.setOnItemClickListener(new OnItemClickListener<String>() {

            @Override
            public void onItemClick(ViewHolder viewHolder, String data, int position) {
                Toast.makeText(CommonItemActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mAdapter);


        //延时3s刷新列表
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> data = new ArrayList<>();
                for (int i = nextIndex; i < nextIndex + 10; i++) {
                    data.add("" + i);
                }
                nextIndex += 10;
                //刷新数据
                mAdapter.setNewData(data);
//
//                TextView t1 = new TextView(CommonItemActivity.this);
//                t1.setText("我是header-1");
//                mAdapter.addHeaderView(t1);
//                TextView t2 = new TextView(CommonItemActivity.this);
//                t2.setText("我是header-2");
//                mAdapter.addHeaderView(t2);
            }
        }, 1000);
    }


    private void loadMore() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mAdapter.getItemCount() >= 20 && isFailed) {
                    isFailed = false;
                    mAdapter.loadFailed();
                } else if (mAdapter.getItemCount() >= MAX_COUNT) {
                    mAdapter.loadEnd();
                } else {
                    final List<String> data = new ArrayList<>();
                    for (int i = nextIndex; i < nextIndex + 10; i++) {
                        data.add("" + i);
                    }
                    nextIndex += 10;
                    //刷新数据
                    mAdapter.setLoadMoreData(data);
                }
            }
        }, 1000);
    }

    private static final int MAX_COUNT = 100;
    private int previousIndex = 0;
    private int nextIndex = 0;

    private void loadPreviousMore() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mAdapter.getItemCount() >= 20 && isFailed) {
                    isFailed = false;
                    mAdapter.loadHeaderFailed();
                } else if (mAdapter.getItemCount() >= MAX_COUNT) {
                    mAdapter.loadHeaderEnd();
                } else {
                    final List<String> data = new ArrayList<>();
                    for (int i = previousIndex - 10; i < previousIndex; i++) {
                        data.add("" + i);
                    }
                    previousIndex -= 10;
                    //刷新数据
                    mAdapter.setLoadHeaderMoreData(data);
                }
            }
        }, 5000);
    }
}
