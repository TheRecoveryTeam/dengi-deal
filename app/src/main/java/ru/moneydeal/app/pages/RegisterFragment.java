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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import ru.moneydeal.app.IActivity;
import ru.moneydeal.app.R;
import ru.moneydeal.app.RegisterViewModel;

public class RegisterFragment extends Fragment {
    private RegisterViewModel mRegisterViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_fragment, container, false);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRegisterViewModel = new ViewModelProvider(getActivity()).get(RegisterViewModel.class);

        final Button registerButton = view.findViewById(R.id.registerButton1);
        final EditText loginInput = view.findViewById(R.id.loginInput);
        final EditText passwordInput = view.findViewById(R.id.passwordInput);

        mRegisterViewModel.getProgress().observe(getViewLifecycleOwner(), loginState -> {
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


        registerButton.setOnClickListener(v -> mRegisterViewModel.login(
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
