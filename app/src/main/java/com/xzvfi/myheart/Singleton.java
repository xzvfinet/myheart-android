package com.xzvfi.myheart;

/**
 * Created by xzvfi on 2016-07-15.
 */
public class Singleton {

    private static Singleton mInstance;
    public static Singleton getInstance() {
        if (mInstance == null) {
            mInstance = new Singleton();
        }
        return mInstance;
    }

    private static NetworkService networkService;
    public static NetworkService getNetworkService() {
        if (networkService == null) {
            networkService = NetworkService.retrofit.create(NetworkService.class);
        }
        return networkService;
    }

}
