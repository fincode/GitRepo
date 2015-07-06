package com.fincode.gitrepo.network.exception;

// Ошибка: У репозитория нет коммитов 
public class EmptyRepositoryException extends Exception {

	private static final long serialVersionUID = 7526472295222744147L;

	public EmptyRepositoryException(String message) {
		super(message);
	}

	public EmptyRepositoryException(String message, Throwable throwable) {
		super(message, throwable);
	}

}