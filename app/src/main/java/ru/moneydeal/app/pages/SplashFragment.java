package ru.moneydeal.app.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ru.moneydeal.app.AuthViewModel;
import ru.moneydeal.app.IRouter;
import ru.moneydeal.app.R;

public class SplashFragment extends Fragment {
    private AuthViewModel mAuthViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.splash_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAuthViewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);

        mAuthViewModel.getProgress().observe(getViewLifecycleOwner(), loginState -> {
            if (loginState == AuthViewModel.LoginState.SUCCESS) {
                switchToHistory();
            } else if (loginState == AuthViewModel.LoginState.FAILED) {
                switchToRegister();
            }
        });

        mAuthViewModel.checkAuth();
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
