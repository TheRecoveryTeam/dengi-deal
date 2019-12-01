package ru.moneydeal.app.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationTokenInterceptor implements Interceptor {
    public interface ITokenRepo {
        @Nullable
        String getToken();
    }

    private final ITokenRepo mTokenRepo;

    AuthorizationTokenInterceptor(ITokenRepo tokenRepo) {
        mTokenRepo = tokenRepo;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = mTokenRepo.getToken();
        Log.d("Interceptor", "token " + token);

        Request request = chain.request();

        if (token != null) {
            request = newRequestWithAccessToken(request, token);
        }

        return chain.proceed(request);
    }

    @NonNull
    private Request newRequestWithAccessToken(@NonNull Request request, @NonNull String accessToken) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }
}
