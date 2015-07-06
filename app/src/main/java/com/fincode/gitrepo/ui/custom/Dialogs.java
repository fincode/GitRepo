package com.fincode.gitrepo.ui.custom;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fincode.gitrepo.R;


public class Dialogs {

	// Создания экземпляра пустого диалогового окна
	public static MaterialDialog newInstanceEmptyDialog(final Context context,
			int resId) {
		boolean wrapInScrollView = true;

		return new MaterialDialog.Builder(context)
				.customView(resId, wrapInScrollView)
				.backgroundColorRes(R.color.md_white)
				.callback(new MaterialDialog.ButtonCallback() {
					@Override
					public void onPositive(MaterialDialog dialog) {
					}

					@Override
					public void onNeutral(MaterialDialog dialog) {
					}
				}).build();
	}

	

}
