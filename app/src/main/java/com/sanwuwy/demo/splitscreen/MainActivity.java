package com.sanwuwy.demo.splitscreen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int CLOSE_DETECT = 0;  // 关闭侦测
    public static final int OPEN_DETECT = 1;   // 开启侦测
    private static final int BLOCK_COLUNMS = 22;   // 区块的列数
    private static final int BLOCK_TOTALS = 396;   // 区块的总数

    private RecyclerView mRecyclerView;
    private TextView mSelect;
    private TextView mErase;
    private TextView mClear;
    private TextView mCancel;
    private TextView mSave;

    // 这里偷个懒，使用了一个第三方的框架的Adapter，也可以使用继承自原生RecyclerView.Adapter<K>的其他Adapter
    private BlockAdapter mAdapter;
    private List<BlockItem> mList;

    /**
     * 是否是选取模式，如果不是选取模式，那么就是擦除模式，模式是擦除模式
     */
    private boolean mIsSelectMode;

    /**
     * 代表区块选中状态的数据，长度为 22 * 18  = 396，测试数据
     */
    private String zone = "10000000000000000000000000000000000000000000000000000000000000000" +
            "00000000000000000000000000000000000000011111100000000000000000000000000000000000" +
            "00000000000000000000000000000000000000000000000000000111110000000000000000000000" +
            "00000000000000000000000000000000000000000000000000000000000000000000000000111110" +
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000" +
            "00000000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.cover_rv);
        mSelect = (TextView) findViewById(R.id.select);
        mErase = (TextView) findViewById(R.id.erase);
        mClear = (TextView) findViewById(R.id.clear);
        mCancel = (TextView) findViewById(R.id.cancel);
        mSave = (TextView) findViewById(R.id.save);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, BLOCK_COLUNMS));
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        initListener();
        initData();
        mAdapter = new BlockAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void initData() {
        mList = new ArrayList<>();
        if (!TextUtils.isEmpty(zone) && zone.length() == BLOCK_TOTALS) {
            for (int i = 0; i < BLOCK_TOTALS; i++) {
                mList.add(new BlockItem(i, zone.charAt(i) == 49 ? OPEN_DETECT : CLOSE_DETECT)); // 需要将char 和 int 做一个转换
            }
        }
    }

    private void initListener() {
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                // 这里必须返回 true，表示拦截RecyclerView的触摸事件，禁止触摸事件向下传递给RecyclerView的子控件
                return true;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());  // 获取触摸点位置对应的RecyclerView子控件，可能为null
                if (child != null) {
                    int position = rv.getChildAdapterPosition(child); // 获取子控件在Adapter中的位置
                    Log.i(TAG, "position = " + position);
                    BlockItem blockItem = mAdapter.getData().get(position);
                    if (mIsSelectMode) { // 如果是选取模式
                        blockItem.setState(OPEN_DETECT);
                        child.setBackgroundColor(getResources().getColor(R.color.block_select_color));
                    } else {  // 如果是擦除模式
                        blockItem.setState(CLOSE_DETECT);
                        child.setBackgroundColor(getResources().getColor(R.color.block_unselect_color));
                    }
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
        mSelect.setOnClickListener(this);
        mErase.setOnClickListener(this);
        mClear.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        resetTextState();
        switch (v.getId()) {
            case R.id.select:
                mIsSelectMode = true;
                mSelect.setTextColor(getResources().getColor(R.color.colorPrimary));
                mSelect.setBackgroundColor(getResources().getColor(R.color.default_text_bg_color));
                break;
            case R.id.erase:
                mIsSelectMode = false;
                mErase.setTextColor(getResources().getColor(R.color.colorPrimary));
                mErase.setBackgroundColor(getResources().getColor(R.color.default_text_bg_color));
                break;
            case R.id.clear:
                mIsSelectMode = false;
                mClear.setTextColor(getResources().getColor(R.color.colorPrimary));
                mClear.setBackgroundColor(getResources().getColor(R.color.default_text_bg_color));
                clearAllBlock();
                break;
            case R.id.cancel:
                mCancel.setBackgroundColor(getResources().getColor(R.color.default_text_bg_color));
                cancelZone();
                break;
            case R.id.save:
                mSave.setBackgroundColor(getResources().getColor(R.color.default_text_bg_color));
                saveZone();
                break;
        }
    }

    /**
     * 取消区块修改操作
     */
    private void cancelZone() {
        if (!TextUtils.isEmpty(zone) && zone.length() == BLOCK_TOTALS) {
            for (int i = 0; i < BLOCK_TOTALS; i++) {
                int c = zone.charAt(i) == 49 ? OPEN_DETECT : CLOSE_DETECT;
                mAdapter.getData().get(i).setState(c);
                if (c == OPEN_DETECT) { // 如果是侦测区块
                    mRecyclerView.getChildAt(i)
                            .setBackgroundColor(getResources().getColor(R.color.block_select_color));
                } else {
                    mRecyclerView.getChildAt(i)
                            .setBackgroundColor(getResources().getColor(R.color.block_unselect_color));
                }
            }
        }
    }

    /**
     * 保存区块修改操作
     */
    private void saveZone() {
        zone = null;
        for (int i = 0; i < BLOCK_TOTALS; i++) {
            if (i == 0) { // 这里在i = 0是必须给zone重新赋值，如果直接 += 的话，最后的结果就是： zone=null001011..., 长度会变成400
                zone = String.valueOf(mAdapter.getData().get(i).getState());
            } else {
                zone += String.valueOf(mAdapter.getData().get(i).getState());
            }
        }
        Log.i(TAG, "zone = " + zone + ",\nzone.length = " + zone.length());
    }

    /**
     * 清除所有区块的选择状态
     */
    private void clearAllBlock() {
        for (int i = 0; i < BLOCK_TOTALS; i++) {
            View view = mRecyclerView.getChildAt(i);
            view.setBackgroundColor(getResources().getColor(R.color.block_unselect_color));
            BlockItem blockItem = mAdapter.getData().get(i);
            blockItem.setState(CLOSE_DETECT);
        }
    }

    public void resetTextState() {
        mSelect.setTextColor(getResources().getColor(R.color.default_text_color));
        mSelect.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mErase.setTextColor(getResources().getColor(R.color.default_text_color));
        mErase.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mClear.setTextColor(getResources().getColor(R.color.default_text_color));
        mClear.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mCancel.setTextColor(getResources().getColor(R.color.default_text_color));
        mCancel.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mSave.setTextColor(getResources().getColor(R.color.default_text_color));
        mSave.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
}
