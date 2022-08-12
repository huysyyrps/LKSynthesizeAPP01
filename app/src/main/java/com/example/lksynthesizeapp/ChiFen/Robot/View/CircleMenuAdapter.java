package com.example.lksynthesizeapp.ChiFen.Robot.View;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lksynthesizeapp.ChiFen.bean.ItemInfo;
import com.example.lksynthesizeapp.R;

import java.util.List;


public class CircleMenuAdapter extends BaseAdapter {
	private List<ItemInfo> data;
	public CircleMenuAdapter(Context context, List<ItemInfo> itemInfos) {
		this.data = itemInfos;
	}

	@Override
	public int getCount() {

		if (data == null || data.isEmpty()) {
			return 0;
		}
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(parent.getContext(), R.layout.item_circle_menu, null);
			holder = new ViewHolder();
			holder.iv = (ImageView) convertView.findViewById(R.id.iv_circle_menu_item);
			holder.tv = (TextView) convertView.findViewById(R.id.tv_circle_menu_item);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		ItemInfo item = data.get(position);
		if (item != null) {
			if (item.getImgId()==1){
				holder.iv.setColorFilter(R.color.translucent_color);;
			}else {
				holder.iv.setImageResource(item.getImgId());;
			}
		}
		holder.tv.setText(item.getText());
		return convertView;
	}

	class ViewHolder {
		ImageView iv;
		TextView tv;
	}

}
