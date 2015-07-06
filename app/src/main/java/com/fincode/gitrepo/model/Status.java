package com.fincode.gitrepo.model;

import com.fincode.gitrepo.model.enums.StatusCode;

public class Status {

	private StatusCode code;
	private Object message;

	public Status() {
		this.code = StatusCode.SUCCESS;
		this.message = "";
	}

	public Status(StatusCode code, String message) {
		this.code = code;
		this.message = message;
	}

	public Status(StatusCode code, Object message) {
		this.code = code;
		this.message = message;
	}

	public StatusCode getCode() {
		return code;
	}

	public void setCode(StatusCode code) {
		this.code = code;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

}
