package ru.moneydeal.app.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ru.moneydeal.app.IRouter;
import ru.moneydeal.app.R;
import ru.moneydeal.app.AuthViewModel;

public class LoginFragment extends Fragment {
    private AuthViewModel mAuthViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAuthViewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);

        final Button loginButton = view.findViewById(R.id.loginInnerButton);
        final EditText loginInput = view.findViewById(R.id.loginInputLogin);
        final EditText passwordInput = view.findViewById(R.id.passwordInputLogin);
        final Button goToRegisterButton = view.findViewById(R.id.goToRegisterButton);

        mAuthViewModel.getProgress().observe(getViewLifecycleOwner(), authState -> {
            if (authState == AuthViewModel.AuthState.FAILED) {
                int color = getResources().getColor(android.R.color.holo_red_dark);

                loginInput.setTextColor(color);
                passwordInput.setTextColor(color);
            } else if (authState == AuthViewModel.AuthState.SUCCESS) {
                Toast.makeText(getContext(), "Success login", Toast.LENGTH_LONG).show();
                switchToHistory();
            }
        });

        loginButton.setOnClickListener(v -> mAuthViewModel.login(
                loginInput.getText().toString(),
                passwordInput.getText().toString()));

        goToRegisterButton.setOnClickListener(v -> switchToRegister());
    }

    private void switchToRegister() {
        IRouter activity = (IRouter) getActivity();

        if (activity == null) {
            return;
        }

        activity.showRegister();
    }

    private void switchToHistory() {
        IRouter activity = (IRouter) getActivity();

        if (activity == null) {
            return;
        }

        activity.showHistory();
    }
}
