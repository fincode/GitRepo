package com.fincode.gitrepo.network;

import com.fincode.gitrepo.network.exception.AlreadyExistException;
import com.fincode.gitrepo.network.exception.BadRequestException;
import com.fincode.gitrepo.network.exception.EmptyRepositoryException;
import com.fincode.gitrepo.network.exception.NoConnectivityException;
import com.fincode.gitrepo.network.exception.UnauthorizedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

// Класс-обработчик ошибок запросов
public class RetrofitErrorHandler implements ErrorHandler {
	@Override
	public Throwable handleError(RetrofitError cause) {
        Throwable c = cause.getCause();
        if (c != null && c instanceof NoConnectivityException)
            return new NoConnectivityException(c.getMessage());
        Response r = cause.getResponse();
		if (r != null) {
			// Ошибка аутенфикации
			if (r.getStatus() == 401) {
				return new UnauthorizedException(cause.getMessage());
			}
			// Неверный запрос
			if (r.getStatus() == 400) {
				return new BadRequestException(cause.getMessage());
			}
			try {
				// Чтение полученного ответа
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(r.getBody().in()));

				StringBuilder out = new StringBuilder();
				String newLine = System.getProperty("line.separator");
				String line;
				while ((line = reader.readLine()) != null) {
					out.append(line);
					out.append(newLine);
				}
				// Список коммитов пуст
                String message = out.toString();
                if (message.contains("Git Repository is empty")) {
					return new EmptyRepositoryException(cause.getMessage());
				} else if (message.contains("name already exists on this account")) {
                    return new AlreadyExistException("Already Exist");
                }
			} catch (IOException e) {
			}
		}
		return cause;
	}
}