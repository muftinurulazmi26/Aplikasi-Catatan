package com.dev.mffa.mynote.Network;

import android.content.Context;
import android.text.TextUtils;

import com.dev.mffa.mynote.App.Const;
import com.dev.mffa.mynote.Lib.DbHelper;
import com.dev.mffa.mynote.Utils.PrefUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static int REQUEST_TIMEOUT = 60;
    private static OkHttpClient okHttpClient;

    public static Retrofit getRetrofit(Context context){
        if (okHttpClient == null){
            initOkhttp(context);
        }
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(Const.BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static void initOkhttp(final Context context) {
        DbHelper dbHelper = new DbHelper(context);

        OkHttpClient.Builder okhttpBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT,TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT,TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okhttpBuilder.addInterceptor(interceptor);
        okhttpBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("Accept","application/json")
                        .addHeader("Content-Type","application/json");

                // Adding Authorization token (API Key)
                // Requests will be denied without API key
                if (!TextUtils.isEmpty(PrefUtils.getApiKey(context))){
                    requestBuilder.addHeader("Authorization",PrefUtils.getApiKey(context));
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        okHttpClient = okhttpBuilder.build();
    }
}
