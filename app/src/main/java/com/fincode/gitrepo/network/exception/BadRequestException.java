package com.fincode.gitrepo.network.exception;

// Ошибка: неверный запрос
public class BadRequestException extends Exception {

	private static final long serialVersionUID = 7526472295622776147L;

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException(String message, Throwable throwable) {
		super(message, throwable);
	}

}