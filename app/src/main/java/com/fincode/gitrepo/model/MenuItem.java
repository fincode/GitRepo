package com.fincode.gitrepo.model;

import android.graphics.drawable.Drawable;

public class MenuItem {

	private String title;
	private Drawable icon;

	public MenuItem() {
		super();
		this.title = "";
		this.icon = null;		// TO-DO change NO-ICON 
	}

	public MenuItem(String title, Drawable icon) {
		super();
		this.title = title;
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

}
