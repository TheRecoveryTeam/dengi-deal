package ru.moneydeal.app;

import androidx.annotation.NonNull;

public interface IRouter {
    void back();

    void showRegister();
    void showHistory();
    void showLogin();
    void showFragmentGroup(@NonNull String groupId);
    void showGroupCreation();
    void showCheck(@NonNull String checkId);
    void showCheckCreate(@NonNull String groupId);
    void showStatistics(@NonNull String groupId);
}
