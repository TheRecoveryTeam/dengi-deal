package ru.moneydeal.app.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.moneydeal.app.IRouter;
import ru.moneydeal.app.R;

public class MainFragment extends Fragment {
    private Button mRegisterButton;
    private Button mLoginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        bindRegisterButton(view);
        bindLoginButton(view);

        return view;
    }

    private void bindRegisterButton(View view) {
        mRegisterButton = view.findViewById(R.id.registerButton);

        mRegisterButton.setOnClickListener(v -> {
            IRouter activity = (IRouter) getActivity();

            if (activity == null) {
                return;
            }

            activity.showRegister();
        });
    }

    private void bindLoginButton(View view) {
        mLoginButton = view.findViewById(R.id.signInButton);

        mLoginButton.setOnClickListener(v -> {
            IRouter activity = (IRouter) getActivity();

            if (activity == null) {
                return;
            }

            activity.showLogin();
        });
    }
}
