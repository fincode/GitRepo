package com.fincode.gitrepo.ui;

import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fincode.gitrepo.R;
import com.fincode.gitrepo.ui.adapter.MenuListAdapter;
import com.fincode.gitrepo.ui.custom.Dialogs;
import com.fincode.gitrepo.ui.custom.DrawerHelper;
import com.fincode.gitrepo.ui.custom.ImageLoader;
import com.fincode.gitrepo.model.enums.MenuItem;
import com.fincode.gitrepo.model.User;
import com.fincode.gitrepo.utils.Utils;

public class MainActivity extends AppCompatActivity {

	public static final String FRAGMENT_REPOS = "fragment_repos";

	private DrawerLayout mDrawerLayout;
	private LinearLayout mRlMenu;
	private ActionBarDrawerToggle mDrawerToggle;
	private User mUser;


	public static long sBackPressed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (!initView()) {
			Toast.makeText(this, getString(R.string.error_user_loading),
					Toast.LENGTH_LONG).show();
		}
		initDrawer();
		if (savedInstanceState == null) {
			selectMenuItem(MenuItem.MENU_REPOSITORIES.ordinal());
		}
	}

	// Инициализация хэдера меню
	private void initMenuHeader(ListView listView) {
		View header = getLayoutInflater().inflate(R.layout.menu_header,
				listView, false);
		TextView txtHeaderLogin = (TextView) header
				.findViewById(R.id.txtMenuHeaderLogin);
		txtHeaderLogin.setText(mUser.getLogin());
		TextView txtHeaderName = (TextView) header
				.findViewById(R.id.txtMenuHeaderName);
		txtHeaderName.setText(mUser.getName());
		ImageView imgHeaderProfile = (ImageView) header
				.findViewById(R.id.imgMenuHeaderProfile);
		ImageLoader imageLoader = new ImageLoader(getApplicationContext());
		imageLoader.DisplayImage(mUser.getAvatar_url(), imgHeaderProfile, true);
		listView.addHeaderView(header);
	}

	// Инициализация представления
	public boolean initView() {
		mUser = Utils.GetUserInfo(this);
		if (mUser == null || mUser.getLogin().isEmpty()
				|| mUser.getPassword().isEmpty())
			return false;
		ListView leftDrawerList = (ListView) findViewById(R.id.left_drawer);
		initMenuHeader(leftDrawerList);

		List<com.fincode.gitrepo.model.MenuItem> menuItems = DrawerHelper
				.setMenuItems(MainActivity.this);
		MenuListAdapter adapter = new MenuListAdapter(MainActivity.this,
				menuItems);
		leftDrawerList.setAdapter(adapter);
		leftDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mRlMenu = (LinearLayout) findViewById(R.id.rl_menu);

		ImageView imgExit = (ImageView) mRlMenu
				.findViewById(R.id.imgMenuItemIcon);
		View view = mRlMenu.findViewById(R.id.viewMenuExit);
		view.setOnClickListener(v -> {
				Utils.RemoveUserInfo(MainActivity.this);
				Intent intent = new Intent(MainActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
		});

		imgExit.setImageResource(R.drawable.ic_logout);
		TextView txtExit = (TextView) mRlMenu
				.findViewById(R.id.txtMenuItemTitle);
		txtExit.setText(getString(R.string.lbl_exit));
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		return true;
	}

	// Коллбэк выбора пункта меню
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position > 0)
				selectMenuItem(position - 1);
		}
	}

	// Переход к пункту меню
	private void selectMenuItem(int position) {
		Fragment fragment = null;
		String tag = "";
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch (MenuItem.values()[position]) {
		case MENU_REPOSITORIES:
			tag = FRAGMENT_REPOS;
			Fragment f = fragmentManager.findFragmentByTag(tag);
			fragment = (f != null && f instanceof ReposFragment) ? (ReposFragment) f
					: new ReposFragment();
			break;

		case MENU_SHOW_INFO:
			showUserProfile();
			break;

		case MENU_EXIT:
			Utils.RemoveUserInfo(this);
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
			break;
		}

		if (fragment != null) {
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment, tag).commit();
		}

		mDrawerLayout.closeDrawer(mRlMenu);
	}

	// Отображение информации о пользователе в диалоговом окне
	private void showUserProfile() {
		final MaterialDialog dialog = Dialogs.newInstanceEmptyDialog(this,
				R.layout.dialog_userinfo);
		View view = dialog.getCustomView();
		ImageView imgProfile = (ImageView) view.findViewById(R.id.imgProfile);

		ImageLoader imageLoader = new ImageLoader(getApplicationContext());
		imageLoader.DisplayImage(mUser.getAvatar_url(), imgProfile, false);

		TextView txtLogin = (TextView) view.findViewById(R.id.txtProfileLogin);
		txtLogin.setText(mUser.getLogin());

		TextView txtName = (TextView) view.findViewById(R.id.txtProfileName);
		txtName.setText(String.format("Имя: %s", mUser.getName()));

		TextView txtEmail = (TextView) view.findViewById(R.id.txtProfileEmail);
		txtEmail.setText(String.format("Email: %s", mUser.getEmail()));

		TextView txtFollowing = (TextView) view
				.findViewById(R.id.txtProfileFollowing);
		txtFollowing.setText(String.format("Подписок: %s",
				String.valueOf(mUser.getFollowing())));

		TextView txtFollowers = (TextView) view
				.findViewById(R.id.txtProfileFollowers);
		txtFollowers.setText(String.format("Подписчиков: %s",
				String.valueOf(mUser.getFollowers())));

		TextView txtPublicRepos = (TextView) view
				.findViewById(R.id.txtProfilePublicRepos);
		txtPublicRepos.setText(String.format("Открытых репозиториев: %s",
				String.valueOf(mUser.getPublic_repos())));
		dialog.show();
	}

	@Override
	public void setTitle(CharSequence title) {
		getSupportActionBar().setTitle(title);
	}

	// Инициализация туллбара
	private void initDrawer() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
				R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
			mDrawerLayout.closeDrawers();
			return;
		}
		if (sBackPressed + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
			ImageLoader imgLoader = new ImageLoader(this);
			imgLoader.clearCache();
			android.os.Process.killProcess(android.os.Process.myPid());
		} else
			Toast.makeText(MainActivity.this, R.string.press_back_for_exit,
					Toast.LENGTH_SHORT).show();
		sBackPressed = System.currentTimeMillis();
	}

}