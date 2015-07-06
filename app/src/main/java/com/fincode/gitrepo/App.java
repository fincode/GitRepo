package com.fincode.gitrepo;

import android.app.Application;

import com.fincode.gitrepo.constant.Url;
import com.fincode.gitrepo.network.ServerCommunicator;

public class App extends Application {
    private static App instance;

    private ServerCommunicator serverCommunicator;

    public static App inst() {
        if (instance == null) {
            throw new IllegalStateException("You're trying to access App too early");
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serverCommunicator = new ServerCommunicator(Url.SERVER_API);
        instance = this;
    }

    public ServerCommunicator getCommunicator() {
        return serverCommunicator;
    }
}
