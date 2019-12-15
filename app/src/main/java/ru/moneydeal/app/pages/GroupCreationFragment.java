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

import ru.moneydeal.app.GroupCreationViewModel;
import ru.moneydeal.app.IRouter;
import ru.moneydeal.app.R;

public class GroupCreationFragment extends Fragment {
    private GroupCreationViewModel mGroupCreationModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.group_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mGroupCreationModel = new ViewModelProvider(getActivity()).get(GroupCreationViewModel.class);

        final Button createButton = view.findViewById(R.id.creationGroupButton);
        final EditText nameInput = view.findViewById(R.id.groupCreationNameInput);
        final EditText descriptionInput = view.findViewById(R.id.groupCreationDescriptionInput);

        mGroupCreationModel.getProgress().observe(getViewLifecycleOwner(), creationState -> {
            if (creationState == GroupCreationViewModel.GroupCreationState.FAILED) {
                int color = getResources().getColor(android.R.color.holo_red_dark);

                nameInput.setTextColor(color);
                descriptionInput.setTextColor(color);
            } else if (creationState == GroupCreationViewModel.GroupCreationState.SUCCESS) {
                Toast.makeText(getContext(), "Success creation", Toast.LENGTH_LONG).show();
                switchToHistory();
            }
        });

        createButton.setOnClickListener(v -> mGroupCreationModel.createGroup(
                nameInput.getText().toString(),
                descriptionInput.getText().toString()
        ));
    }

    private void switchToHistory() {
        IRouter activity = (IRouter) getActivity();

        if (activity != null) {
            activity.showHistory();
        }
    }
}
