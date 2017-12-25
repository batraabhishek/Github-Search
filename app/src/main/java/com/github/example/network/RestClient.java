package com.github.example.network;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hardik on 23/12/17.
 */

public class RestClient {

    private static Retrofit mSharedInstance;
    private static final String GITHUB_BASE_URL = "http://api.github.com";
    private static Gson gson;
    private static APIService apiService;

    private static Retrofit getRetrofitInstance() {
        if (mSharedInstance == null) {
            gson = new GsonBuilder().setLenient().create();
            mSharedInstance = new Retrofit.Builder().baseUrl(GITHUB_BASE_URL)
                    .client(getHttpClientInterceptor().build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }

        return mSharedInstance;
    }

    public static APIService getAPIInvokerInstance() {
        if (apiService == null) {
            apiService = getRetrofitInstance().create(APIService.class);
        }
        return apiService;
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static OkHttpClient.Builder getHttpClientInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builderHttpClient = new OkHttpClient.Builder();
        builderHttpClient.connectTimeout(5, TimeUnit.MINUTES);
        builderHttpClient.readTimeout(5, TimeUnit.MINUTES);
        builderHttpClient.writeTimeout(5, TimeUnit.MINUTES);
        builderHttpClient.addInterceptor(logging);
        builderHttpClient.addInterceptor(new RequestInterceptor());
        builderHttpClient.retryOnConnectionFailure(true);
        return builderHttpClient;
    }
}
