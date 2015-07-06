package com.fincode.gitrepo.model;

public class Commit {

	private String sha;
	private CommitInfo commit;

	public Commit() {
	}

	public Commit(String sha, CommitInfo commit) {
		this.sha = sha;
		this.commit = commit;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public CommitInfo getInfo() {
		return commit;
	}

	public void setInfo(CommitInfo info) {
		this.commit = info;
	}
}
