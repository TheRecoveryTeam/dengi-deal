package ru.moneydeal.app;

import androidx.annotation.NonNull;

public interface IRouter {
    void showSplash();
    void showRegister();
    void showHistory();
    void showLogin();
    void showFragmentGroup(@NonNull String groupId);
    void showGroupCreation();
    void showCheck(@NonNull String checkId);
}
