package ru.moneydeal.app.auth;

import androidx.annotation.Nullable;

import java.util.List;

import ru.moneydeal.app.ApplicationModified;
import ru.moneydeal.app.network.AuthorizationTokenInterceptor;

public class TokenRepo implements AuthorizationTokenInterceptor.ITokenRepo {
    private final AuthDao mAuthDao;

    public TokenRepo(ApplicationModified context) {
        mAuthDao = context.getDB().getAuthDao();
    }

    @Nullable
    @Override
    public String getToken() {
        List<String> tokens = mAuthDao.getTokens();
        if (tokens.size() == 0) {
            return null;
        }

        return tokens.get(0);
    }
}
