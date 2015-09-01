package com.fincode.gitrepo.service;


import android.content.Context;

import com.fincode.gitrepo.App;
import com.fincode.gitrepo.R;
import com.fincode.gitrepo.model.Repository;
import com.fincode.gitrepo.model.User;
import com.fincode.gitrepo.model.enums.MethodName;
import com.fincode.gitrepo.network.ServerCommunicator;
import com.fincode.gitrepo.network.exception.AlreadyExistException;
import com.fincode.gitrepo.network.exception.BadRequestException;
import com.fincode.gitrepo.network.exception.EmptyRepositoryException;
import com.fincode.gitrepo.network.exception.NoConnectivityException;
import com.fincode.gitrepo.network.exception.UnauthorizedException;
import com.joanzapata.android.asyncservice.api.EnhancedService;
import com.joanzapata.android.asyncservice.api.annotation.ApplicationContext;
import com.joanzapata.android.asyncservice.api.annotation.AsyncService;
import com.joanzapata.android.asyncservice.api.annotation.Serial;

import java.net.SocketTimeoutException;

import javax.net.ssl.SSLHandshakeException;

import retrofit.RetrofitError;

@AsyncService
public abstract class WebAsync implements EnhancedService {

    @ApplicationContext
    static Context context;

    @Serial("CACHE")
    public RequestSuccess SendRequest(User user, MethodName method, Repository repo) {
        String sError = "";
        /*Exception exception = null;
        ServerCommunicator communicator = App.inst().getCommunicator();
        try {
            Object result = null;
            switch (method) {
                case Auth:
                    result = communicator.Auth(user);
                    break;
                case GetRepositories:
                    result = communicator.GetRepos(user);
                    break;
                case CreateRepository:
                    result = communicator.CreateRepo(user, repo);
                    break;
                case GetCommits:
                    result = communicator.GetCommits(user, repo);
                    break;
                default:
            }
            return new RequestSuccess(result);
        } catch (NoConnectivityException e) {
            sError = context.getString(R.string.error_internet_access);
            exception = e;
        } catch (SSLHandshakeException e) {
            sError = context.getString(R.string.error_cert);
            exception = e;
        } catch (UnauthorizedException e) {
            sError = context.getString(R.string.error_wrong_login_or_pass);
            exception = e;
        } catch (SocketTimeoutException e) {
            sError = context.getString(R.string.error_server_not_response);
            exception = e;
        } catch (EmptyRepositoryException e) {
            sError = context.getString(R.string.error_empty_repositorie);
            exception = e;
        } catch (AlreadyExistException e) {
            sError = context.getString(R.string.error_repositorie_exist);
            exception = e;
        } catch (BadRequestException e) {
            sError = context.getString(R.string.error_bad_request);
            exception = e;
        } catch (RetrofitError e) {
            sError = context.getString(R.string.error_server_unavailable);
            exception = e;
        } catch (Exception e) {
            sError = context.getString(R.string.error_unknown);
            exception = e;
        }
        send(new RequestError(sError, exception));*/
        return null;
    }

    public static class RequestSuccess {
        private final Object object;

        public RequestSuccess(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return object;
        }
    }

    public static class RequestError {
        private final String message;
        private final Exception error;

        public RequestError(String message) {
            this.message = message;
            this.error = null;
        }

        public RequestError(String message, Exception error) {
            this.message = message;
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public Exception getError() {
            return error;
        }
    }

}
