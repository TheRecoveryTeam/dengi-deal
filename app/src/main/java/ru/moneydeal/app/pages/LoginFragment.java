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

import ru.moneydeal.app.IActivity;
import ru.moneydeal.app.R;
import ru.moneydeal.app.RegisterViewModel;

public class LoginFragment extends Fragment {
    private RegisterViewModel mLoginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoginViewModel = new ViewModelProvider(getActivity()).get(RegisterViewModel.class);

        final Button loginButton = view.findViewById(R.id.loginInnerButton);
        final EditText loginInput = view.findViewById(R.id.loginInputLogin);
        final EditText passwordInput = view.findViewById(R.id.passwordInputLogin);

        mLoginViewModel.getProgress().observe(getViewLifecycleOwner(), loginState -> {
            if (loginState == RegisterViewModel.LoginState.FAILED) {
                int color = getResources().getColor(android.R.color.holo_red_dark);

                loginInput.setTextColor(color);
                passwordInput.setTextColor(color);
            } else if (loginState == RegisterViewModel.LoginState.ERROR) {
                int color = getResources().getColor(android.R.color.holo_orange_light);

                loginInput.setTextColor(color);
                passwordInput.setTextColor(color);
            } else if (loginState == RegisterViewModel.LoginState.SUCCESS) {
                Toast.makeText(getContext(), "Success login", Toast.LENGTH_LONG).show();
                switchToHistory();
            }
        });


        loginButton.setOnClickListener(v -> mLoginViewModel.login(
                loginInput.getText().toString(),
                passwordInput.getText().toString()));
    }

    private void switchToHistory() {
        IActivity activity = (IActivity) getActivity();

        if (activity == null) {
            return;
        }

        activity.showHistory();
    }
}
