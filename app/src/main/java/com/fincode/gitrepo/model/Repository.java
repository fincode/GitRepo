package com.fincode.gitrepo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Repository implements Parcelable {

	private String name;
	private String description;
	private User owner;
	private int forks_count;
	private int watchers_count;

	public Repository(String name, String description, User owner,
			String avatarUrl, int forksCount, int watchersCount) {
		this.name = name;
		this.description = description;
		this.owner = owner;
		this.forks_count = forksCount;
		this.watchers_count = watchersCount;
	}

	public Repository() {
		this.name = "";
		this.description = "";
		this.owner = new User();
		this.forks_count = 0;
		this.watchers_count = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getForksCount() {
		return forks_count;
	}

	public void setForksCount(int forksCount) {
		this.forks_count = forksCount;
	}

	public int getWatchersCount() {
		return watchers_count;
	}

	public void setWatchersCount(int watchersCount) {
		this.watchers_count = watchersCount;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(description);
		parcel.writeString(name);
		parcel.writeParcelable(owner, flags);
		parcel.writeInt(forks_count);
		parcel.writeInt(watchers_count);
	}

	public static final Parcelable.Creator<Repository> CREATOR = new Parcelable.Creator<Repository>() {
		public Repository createFromParcel(Parcel in) {
			return new Repository(in);
		}

		public Repository[] newArray(int size) {
			return new Repository[size];
		}
	};

	private Repository(Parcel parcel) {
		description = parcel.readString();
		name = parcel.readString();
		owner = (User) parcel.readParcelable(User.class.getClassLoader());
		forks_count = parcel.readInt();
		watchers_count = parcel.readInt();
	}
}
