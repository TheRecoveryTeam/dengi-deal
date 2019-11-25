package ru.moneydeal.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import ru.moneydeal.app.pages.HistoryFragment;
import ru.moneydeal.app.pages.RegisterFragment;
import ru.moneydeal.app.pages.MainFragment;

public class MainActivity extends AppCompatActivity implements IActivity {

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
            transaction.add(R.id.fragment_container, new MainFragment());
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }



    public void showRegister() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }

    public void showHistory() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HistoryFragment())
                .addToBackStack(null)
                .commit();
    }
}
