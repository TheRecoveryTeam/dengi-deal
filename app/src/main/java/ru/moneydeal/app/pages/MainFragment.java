package ru.moneydeal.app.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.moneydeal.app.IActivity;
import ru.moneydeal.app.R;

public class MainFragment extends Fragment {
    private Button mRegisterButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        bindLoginButton(view);

        return view;
    }

    private void bindLoginButton(View view) {
        mRegisterButton = view.findViewById(R.id.registerButton);

        mRegisterButton.setOnClickListener(v -> {
            IActivity activity = (IActivity) getActivity();

            if (activity == null) {
                return;
            }

            activity.showRegister();
        });
    }
}
