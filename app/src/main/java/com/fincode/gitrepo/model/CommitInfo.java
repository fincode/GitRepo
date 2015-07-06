package com.fincode.gitrepo.model;

public class CommitInfo {

	private String message;
	private User author;

	public CommitInfo() {
		this.message = "";
		this.author = new User();
	}

	public CommitInfo(String message, User author) {
		this.message = message;
		this.author = author;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

}
