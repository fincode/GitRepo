package com.fincode.gitrepo.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fincode.gitrepo.R;
import com.fincode.gitrepo.model.MenuItem;

public class MenuListAdapter extends BaseAdapter {

	private List<MenuItem> mMenuItems;
	private Activity mActivity;

	static class ViewHolder {
		public ImageView imgIcon;
		public TextView txtTitle;
	}

	public MenuListAdapter(Activity activity, List<MenuItem> menuItems) {
		super();
		this.mActivity = activity;
		this.mMenuItems = menuItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = mActivity.getLayoutInflater();
			view = inflater.inflate(R.layout.list_item_menu, parent, false);
			holder = new ViewHolder();
			holder.imgIcon = (ImageView) view
					.findViewById(R.id.imgMenuItemIcon);
			holder.txtTitle = (TextView) view
					.findViewById(R.id.txtMenuItemTitle);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		MenuItem item = mMenuItems.get(position);
		if (item != null) {
			Drawable icon = item.getIcon();
			if (icon != null)
				holder.imgIcon.setImageDrawable(icon);
			holder.txtTitle.setText(item.getTitle());
		}

		return view;
	}

	@Override
	public int getCount() {
		return mMenuItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mMenuItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
