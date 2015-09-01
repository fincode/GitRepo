package com.fincode.gitrepo.network;

import com.fincode.gitrepo.model.Commit;
import com.fincode.gitrepo.model.Repository;
import com.fincode.gitrepo.model.User;
import com.fincode.gitrepo.network.exception.AlreadyExistException;
import com.fincode.gitrepo.network.exception.BadRequestException;
import com.fincode.gitrepo.network.exception.EmptyRepositoryException;
import com.fincode.gitrepo.network.exception.NoConnectivityException;
import com.fincode.gitrepo.network.exception.UnauthorizedException;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

// Интерфейс, предоставляющий методы для работы с GitHub
public interface IGitHubService {

    @GET("/user/repos")
    public Observable<List<Repository>> getRepositories(@Header("Authorization") String auth);

    @GET("/users/{user}")
    public Observable<User> getUserProfile(@Header("Authorization") String auth,
                               @Path("user") String user);

    @GET("/repos/{user}/{repo}/commits")
    public Observable<List<Commit>> getCommits(@Header("Authorization") String auth,
                                   @Path("user") String user, @Path("repo") String repo);

    @POST("/user/repos")
    public Observable<Repository> createRepo(@Header("Authorization") String auth,
                                 @Body Repository repo);
}
