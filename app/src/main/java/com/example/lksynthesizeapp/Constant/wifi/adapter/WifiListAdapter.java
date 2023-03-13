package com.example.lksynthesizeapp.Constant.wifi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lksynthesizeapp.Constant.wifi.bean.WifiBean;
import com.example.lksynthesizeapp.R;

import java.util.ArrayList;
import java.util.List;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.VerticalViewHolder> {

    private List<WifiBean> mList = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener onItemClickListener = null;

    public WifiListAdapter(Context context, List<WifiBean> list) {
        mContext = context;
        mList = list;

    }

    /**
     * 直接赋值
     * 更新
     * 数据
     */
    public void setVerticalDataList(List<WifiBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VerticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.layout_wifi_list, null);
        return new VerticalViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull VerticalViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        Log.d("TAG", "======刷新数====222222222222222====");

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onItemClickListener.OnItemClick(view, position);
            }
        });

        holder.wifiName.setText(mList.get(position).getWifiName() + "");
        if (mList.get(position).getLevel()>-70){
            holder.wifiTypePic.setImageResource(R.mipmap.state_wifi);//-70 信号最好  -85dbm　2格  -100dbm 1格
        }else if (mList.get(position).getLevel()>-85){
            holder.wifiTypePic.setImageResource(R.mipmap.three_wifi);//-70 信号最好  -85dbm　2格  -100dbm 1格
        }else {
            holder.wifiTypePic.setImageResource(R.mipmap.two_wifi);//-70 信号最好  -85dbm　2格  -100dbm 1格
        }

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder {

        private ImageView wifiTypePic;
        private TextView wifiName;
        private TextView carTotalKm;

        public VerticalViewHolder(View itemView) {
            super(itemView);
            wifiTypePic = itemView.findViewById(R.id.wifi_type_pic);
            wifiName = itemView.findViewById(R.id.wifi_name);
            carTotalKm = itemView.findViewById(R.id.car_total_km);
        }

    }

    /**
     * 接口
     * 回调
     */
    public static interface OnItemClickListener {
        void OnItemClick(View view, int position);

        void OnItemLonClick(int position);
    }

    public void setMyOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
