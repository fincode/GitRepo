package com.fincode.gitrepo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

	private String login;
	private String avatar_url;
	private String name;
	private String password;
	private String email;
	private String repos_url;
	private String date;
	private int following;
	private int followers;
	private int public_repos;

	public User(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public User(String login, String avatarUrl, String name, String password,
			String email, String reposUrl, String date, int following,
			int followers, int publicRepos) {
		this.login = login;
		this.avatar_url = avatarUrl;
		this.name = name;
		this.password = password;
		this.email = email;
		this.repos_url = reposUrl;
		this.date = date;
		this.following = following;
		this.followers = followers;
		this.public_repos = publicRepos;

	}

	public User() {
		this.login = "";
		this.avatar_url = "";
		this.name = "";
		this.password = "";
		this.email = "";
		this.repos_url = "";
		this.date = "";
		this.following = 0;
		this.followers = 0;
		this.public_repos = 0;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getFollowing() {
		return following;
	}

	public void setFollowing(int following) {
		this.following = following;
	}

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

	public String getRepos_url() {
		return repos_url;
	}

	public void setRepos_url(String repos_url) {
		this.repos_url = repos_url;
	}

	public int getPublic_repos() {
		return public_repos;
	}

	public void setPublic_repos(int public_repos) {
		this.public_repos = public_repos;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(login);
		parcel.writeString(avatar_url);
		parcel.writeString(name);
		parcel.writeString(password);
		parcel.writeString(email);
		parcel.writeString(repos_url);
		parcel.writeString(date);
		parcel.writeInt(following);
		parcel.writeInt(followers);
		parcel.writeInt(public_repos);

	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};

	private User(Parcel parcel) {
		login = parcel.readString();
		avatar_url = parcel.readString();
		name = parcel.readString();
		password = parcel.readString();
		email = parcel.readString();
		repos_url = parcel.readString();
		date = parcel.readString();
		following = parcel.readInt();
		followers = parcel.readInt();
		public_repos = parcel.readInt();
	}
}
