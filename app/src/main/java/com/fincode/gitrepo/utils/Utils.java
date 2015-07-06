package com.fincode.gitrepo.utils;

import java.io.InputStream;
import java.io.OutputStream;

import com.fincode.gitrepo.constant.Other;
import com.fincode.gitrepo.constant.Preferences;
import com.fincode.gitrepo.constant.Tag;
import com.fincode.gitrepo.model.User;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

	// Удаление информации о пользователе
	public static void RemoveUserInfo(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Preferences.APP_PREFERENCES, Context.MODE_PRIVATE);
		settings.edit().clear().commit();
	}

	// Копирование потоков
	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	// Загрузка информации о пользователе
	public static User GetUserInfo(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				Preferences.APP_PREFERENCES, Context.MODE_PRIVATE);
		String login = preferences.getString(Preferences.APP_PREFERENCES_LOGIN,
				"");
		String password = preferences.getString(Tag.PASSWORD, "");
		String name = preferences.getString(Tag.USER_NAME, "");
		String avatarUrl = preferences.getString(Tag.AVATAR_URL, "");
		String email = preferences.getString(Tag.EMAIL, "");
		String reposUrl = preferences.getString(Tag.REPOS_URL, "");
		int following = preferences.getInt(Tag.FOLLOWING, 0);
		int followers = preferences.getInt(Tag.FOLLOWERS, 0);
		int publicRepos = preferences.getInt(Tag.PUBLIC_REPOS, 0);
		if (login.isEmpty() || password.isEmpty()) {
			return null;
		}

		try {
			login = PasswordUtils.decrypt(Other.MASTER, login);
			password = PasswordUtils.decrypt(Other.MASTER, password);
		} catch (Exception e) {
			return null;
		}
		return new User(login, avatarUrl, name, password, email, reposUrl, "",
				following, followers, publicRepos);
	}

	// Сохранение информации о пользователе
	public static boolean SaveUser(Context context, User user) {
		SharedPreferences prefs = context.getSharedPreferences(
				Preferences.APP_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();

		try {
			editor.putString(Tag.USER_NAME, user.getName());
			editor.putString(Tag.AVATAR_URL, user.getAvatar_url());
			editor.putString(Tag.EMAIL, user.getEmail());
			editor.putString(Tag.REPOS_URL, user.getRepos_url());
			editor.putInt(Tag.FOLLOWING, user.getFollowing());
			editor.putInt(Tag.FOLLOWERS, user.getFollowers());
			editor.putInt(Tag.PUBLIC_REPOS, user.getPublic_repos());
			// Шифрование логина и пароля
			editor.putString(Tag.LOGIN,
					PasswordUtils.encrypt(Other.MASTER, user.getLogin()));
			editor.putString(Tag.PASSWORD,
					PasswordUtils.encrypt(Other.MASTER, user.getPassword()));
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			editor.commit();
		}

	}
}
