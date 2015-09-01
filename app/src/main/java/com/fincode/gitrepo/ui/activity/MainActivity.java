package com.fincode.gitrepo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fincode.gitrepo.App;
import com.fincode.gitrepo.R;
import com.fincode.gitrepo.ui.custom.CircleTransform;
import com.fincode.gitrepo.ui.fragment.ReposFragment;
import com.fincode.gitrepo.ui.DialogsFabric;
import com.fincode.gitrepo.model.User;
import com.fincode.gitrepo.utils.Utils;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final long DRAWER_CLOSE_DELAY_MS = 150;
    private static final String NAV_ITEM_ID = "navItemId";

    private final Handler mDrawerActionHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private android.view.MenuItem mPreviousMenuItem;
    private int mNavItemId;


    private LinearLayout mRlMenu;
    private User mUser;


    public static long sBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUser = Utils.GetUserInfo(this);
        if (userLoadingError()) {
            Toast.makeText(this, getString(R.string.error_user_loading),
                    Toast.LENGTH_LONG).show();
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (null == savedInstanceState) {
            mNavItemId = R.id.drawer_item_repos;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        initMenuHeader(navigationView);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        navigationView.getMenu().findItem(R.id.drawer_item_repos).setChecked(true);
        mPreviousMenuItem = navigationView.getMenu().findItem(R.id.drawer_item_repos).setChecked(true);
        navigate(mNavItemId);
    }

    private boolean userLoadingError() {
        return mUser == null || mUser.getLogin().isEmpty()
                || mUser.getPassword().isEmpty();
    }

    // Инициализация хэдера меню
    private void initMenuHeader(NavigationView navigationView) {
        View header = navigationView.inflateHeaderView(R.layout.drawer_header);
        TextView txtHeaderLogin = (TextView) header
                .findViewById(R.id.txt_menu_header_login);
        txtHeaderLogin.setText(mUser.getLogin());
        TextView txtHeaderName = (TextView) header
                .findViewById(R.id.txt_menu_header_name);
        txtHeaderName.setText(mUser.getName());
        ImageView imgHeaderProfile = (ImageView) header
                .findViewById(R.id.img_menu_header_profile);

        String url = mUser.getAvatar_url();
        if (url != null && !url.isEmpty())
            Picasso.with(App.inst()).load(url)
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.no_image)
                    .transform(new CircleTransform(0))
                    .into(imgHeaderProfile);
    }

    private final static String FRAGMENT_REPOS = "repos";

    // Переход к пункту меню
    private void navigate(final int itemId) {
        String tag = String.valueOf(itemId);
        Fragment fragment = null;
        switch (itemId) {
            case R.id.drawer_item_repos:
                tag = FRAGMENT_REPOS;
                fragment = new ReposFragment();
                break;

            case R.id.drawer_item_profile:
                showUserProfile();
                return;

            case R.id.drawer_item_logout:
                Utils.RemoveUserInfo(this);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
        }

        Fragment buf = getSupportFragmentManager().findFragmentByTag(tag);
        if (buf != null)
            fragment = buf;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment, tag);
        ft.addToBackStack(tag);
        ft.commit();
    }

    // Отображение информации о пользователе в диалоговом окне
    private void showUserProfile() {
        final MaterialDialog dialog = DialogsFabric.newInstanceEmptyDialog(this,
                R.layout.dialog_userinfo);
        View view = dialog.getCustomView();
        ImageView imgProfile = (ImageView) view.findViewById(R.id.imgProfile);

        String url = mUser.getAvatar_url();
        if (url != null && !url.isEmpty())
            Picasso.with(App.inst()).load(url)
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.no_image)
                    .transform(new CircleTransform(1))
                    .into(imgProfile);

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

    @Override
    public boolean onNavigationItemSelected(final android.view.MenuItem menuItem) {
        if (mPreviousMenuItem != menuItem) {
            if (menuItem.getItemId() != R.id.drawer_item_profile) {
                menuItem.setChecked(true);
                if (mPreviousMenuItem != null) {
                    mPreviousMenuItem.setChecked(false);
                }
                mPreviousMenuItem = menuItem;
                mNavItemId = menuItem.getItemId();
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mDrawerActionHandler.postDelayed(() -> navigate(menuItem.getItemId()), DRAWER_CLOSE_DELAY_MS);
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(final android.view.MenuItem item) {
        if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
            return mDrawerToggle.onOptionsItemSelected(item);
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
            android.os.Process.killProcess(android.os.Process.myPid());
        } else
            Toast.makeText(MainActivity.this, R.string.press_back_for_exit,
                    Toast.LENGTH_SHORT).show();
        sBackPressed = System.currentTimeMillis();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

}