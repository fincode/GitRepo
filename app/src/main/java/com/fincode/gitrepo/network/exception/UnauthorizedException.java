package com.fincode.gitrepo.network.exception;

// Ошибка аутенфикации
public class UnauthorizedException extends Exception {

	private static final long serialVersionUID = 7526472295222776147L;

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException(String message, Throwable throwable) {
		super(message, throwable);
	}

}