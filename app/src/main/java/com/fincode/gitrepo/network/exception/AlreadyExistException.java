package com.fincode.gitrepo.network.exception;

// Ошибка: Уже существует
public class AlreadyExistException extends Exception {

	private static final long serialVersionUID = 7526472295644776147L;

	public AlreadyExistException(String message) {
		super(message);
	}

	public AlreadyExistException(String message, Throwable throwable) {
		super(message, throwable);
	}

}