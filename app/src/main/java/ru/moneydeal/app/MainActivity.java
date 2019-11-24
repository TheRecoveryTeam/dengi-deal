package ru.moneydeal.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ru.moneydeal.app.pages.PhoneFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new PhoneFragment())
                    .commit();
        }
    }
}
