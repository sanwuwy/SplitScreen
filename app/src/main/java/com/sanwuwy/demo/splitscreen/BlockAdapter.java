package com.sanwuwy.demo.splitscreen;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class BlockAdapter extends BaseQuickAdapter<BlockItem, BaseViewHolder> {

    private static final String TAG = BlockAdapter.class.getSimpleName();

    private static final int BLOCK_ROWS = 18;   // 区块的行数

    private Context mContext;

    public BlockAdapter(Context context, List<BlockItem> list) {
        super(R.layout.item_block, list);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, BlockItem item) {
        int screenHeight = SizeUtils.getScreenHeight(mContext);
        View view = helper.getView(R.id.block_item_iv);
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        params.height = (screenHeight / BLOCK_ROWS) - SizeUtils.getDividerIntrinsicHeight(mContext);
        view.setLayoutParams(params);
        Log.i(TAG, "screenHeight = " + screenHeight + ", params.height = " + params.height +
                ", dividerIntrinsicHeight = " + SizeUtils.getDividerIntrinsicHeight(mContext));
        if (item.getState() == MainActivity.OPEN_DETECT) {  // 如果开启侦测
            view.setBackgroundColor(mContext.getResources().getColor(R.color.block_select_color));
        } else {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.block_unselect_color));
        }
    }
}
