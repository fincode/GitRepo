package com.fincode.gitrepo.ui.custom;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;

import com.fincode.gitrepo.R;
import com.fincode.gitrepo.model.MenuItem;

//  ласс-помощник дл¤ работы с боковым меню
public class DrawerHelper {

	@SuppressWarnings("deprecation")
	public static List<MenuItem> setMenuItems(Context context) {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		Resources res = context.getResources();
		menuItems.add(new MenuItem(
				context.getString(R.string.lbl_repositories), res
						.getDrawable(R.drawable.ic_repo)));
		menuItems.add(new MenuItem(context.getString(R.string.lbl_user_info),
				res.getDrawable(R.drawable.ic_profile)));
		return menuItems;
	}
}
