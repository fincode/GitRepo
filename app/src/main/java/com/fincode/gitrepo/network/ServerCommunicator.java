package com.fincode.gitrepo.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import com.fincode.gitrepo.App;
import com.fincode.gitrepo.constant.Url;
import com.fincode.gitrepo.model.Commit;
import com.fincode.gitrepo.model.Repository;
import com.fincode.gitrepo.model.User;
import com.fincode.gitrepo.network.exception.AlreadyExistException;
import com.fincode.gitrepo.network.exception.BadRequestException;
import com.fincode.gitrepo.network.exception.EmptyRepositoryException;
import com.fincode.gitrepo.network.exception.NoConnectivityException;
import com.fincode.gitrepo.network.exception.UnauthorizedException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;

import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;

public class ServerCommunicator {
    private static final String LOG_TAG = "ServerCommunicator";

    private final IGitHubService gitHubService;
    private final String endpointUrl;

    // Создание экземпляра подключения
    public ServerCommunicator(String endpointUrl) {
        this.endpointUrl = endpointUrl;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endpointUrl)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new RetrofitErrorHandler())
                .setClient(new WrappedClient(new OkClient()))
                .build();

        this.gitHubService = restAdapter.create(IGitHubService.class);
    }

    public class WrappedClient implements Client {
        public WrappedClient(Client wrappedClient) {
            this.wrappedClient = wrappedClient;
        }

        Client wrappedClient;

        @Override
        public Response execute(Request request) throws IOException {
            if (!isNetworkAvailable()) {
                throw new NoConnectivityException("No connectivity");
            }
            return wrappedClient.execute(request);
        }
    }

    // Формирование строки аутентификации
    public String encodeCredentialsForBasicAuthorization(User user) {
        String credentials = user.getLogin() + ":" + user.getPassword();
        String credBase64 = Base64.encodeToString(credentials.getBytes(),
                Base64.DEFAULT).replace("\n", "");
        return "Basic " + credBase64;
    }

    // Авторизация пользователя
    public User Auth(User user) throws SSLHandshakeException, UnauthorizedException,
            SocketTimeoutException, BadRequestException, NoConnectivityException  {
        String auth = encodeCredentialsForBasicAuthorization(user);
        return gitHubService.getUserProfile(auth, user.getLogin());
    }

    // Получение списка репозиториев
    public List<Repository> GetRepos(User user) throws SSLHandshakeException, UnauthorizedException,
            SocketTimeoutException, BadRequestException, NoConnectivityException {
        String auth = encodeCredentialsForBasicAuthorization(user);
        return gitHubService.getRepositories(auth);
    }

    // Создание репозитория
    public Repository CreateRepo(User user, Repository repo) throws SSLHandshakeException, UnauthorizedException,
            SocketTimeoutException, BadRequestException, NoConnectivityException, AlreadyExistException {
        String auth = encodeCredentialsForBasicAuthorization(user);
        return gitHubService.createRepo(auth, repo);
    }

    // Получение списка коммитов
    public List<Commit> GetCommits(User user, Repository repo) throws SSLHandshakeException, UnauthorizedException,
            SocketTimeoutException, BadRequestException, EmptyRepositoryException, NoConnectivityException  {
        String auth = encodeCredentialsForBasicAuthorization(user);
        return gitHubService.getCommits(auth, repo.getOwner().getLogin(), repo.getName());
    }

    // Проверка наличия подключения к серверу
    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) App.inst().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}