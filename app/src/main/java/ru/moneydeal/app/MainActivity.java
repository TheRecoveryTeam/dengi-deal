package ru.moneydeal.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import ru.moneydeal.app.pages.CheckCreateFragment;
import ru.moneydeal.app.pages.CheckFragment;
import ru.moneydeal.app.pages.GroupCreationFragment;
import ru.moneydeal.app.pages.GroupFragment;
import ru.moneydeal.app.pages.GroupListFragment;
import ru.moneydeal.app.pages.LoginFragment;
import ru.moneydeal.app.pages.RegisterFragment;
import ru.moneydeal.app.pages.SplashFragment;
import ru.moneydeal.app.pages.StatisticsFragment;

public class MainActivity extends AppCompatActivity implements IRouter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            initState();
        }
    }

    private void initState() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragmentList = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragmentList == null || !fragmentList.isVisible()) {
            transaction.add(R.id.fragment_container, new SplashFragment());
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void showRegister() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegisterFragment())
                .commit();
    }

    @Override
    public void showHistory() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new GroupListFragment())
                .commit();
    }

    @Override
    public void showLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    @Override
    public void showFragmentGroup(String groupId) {
        GroupFragment instance = GroupFragment.getInstance(groupId);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, instance)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showGroupCreation() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new GroupCreationFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showCheck(@NonNull String checkId) {
        CheckFragment instance = CheckFragment.getInstance(checkId);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, instance)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showCheckCreate(@NonNull String groupId) {
        CheckCreateFragment instance = CheckCreateFragment.getInstance(groupId);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, instance)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showStatistics(@NonNull String groupId) {
        StatisticsFragment instance = StatisticsFragment.getInstance(groupId);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, instance)
                .addToBackStack(null)
                .commit();
    }
}
