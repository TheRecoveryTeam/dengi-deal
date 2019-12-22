package ru.moneydeal.app.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ru.moneydeal.app.GroupViewModel;
import ru.moneydeal.app.R;
import ru.moneydeal.app.group.ParticipantEntity;

public class GroupAddingParticipant extends Fragment {
    private static String GROUP_ID = "GROUP_ID";
    private GroupViewModel mGroupViewModel;
    private String mGroupId;
    private TextView mLogin;
    private Button mSearchButton;
    private EditText mLoginInput;
    private Button mParticipantButton;
    private ParticipantEntity mParticipant;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mGroupViewModel = new ViewModelProvider(getActivity()).get(GroupViewModel.class);

        View view = inflater.inflate(
                R.layout.group_add_user,
                container,
                false
        );

        Bundle bundle = getArguments();
        if (bundle == null) {
            return view;
        }

        mGroupId = bundle.getString(GROUP_ID);
        if (mGroupId == null) {
            return view;
        }
        bindViews(view);
        return view;
    }

    private void bindViews(View view) {
        mLoginInput = view.findViewById(R.id.participantLoginInput);
        mLogin = view.findViewById(R.id.group_participant_login);
        bindSearchButton(view);
        bindAddButton(view);
    }

    private void bindSearchButton(View view) {
        mSearchButton = view.findViewById(R.id.searchParticipantButton);

        mSearchButton.setOnClickListener(v -> {
            mParticipantButton.setVisibility(View.INVISIBLE);
            mLogin.setVisibility(View.INVISIBLE);

            mGroupViewModel.fetchParticipant(mLoginInput.getText().toString(), mGroupId).observe(getViewLifecycleOwner(), participant -> {
                mParticipant = participant;
                if (participant != null) {
                    mLogin.setText(participant.login);
                    mParticipantButton.setVisibility(View.VISIBLE);
                    mLogin.setVisibility(View.VISIBLE);
                } else {
                    mParticipantButton.setVisibility(View.INVISIBLE);
                    mLogin.setVisibility(View.VISIBLE);
                    mLoginInput.setText("");
                    mLogin.setText(getString(R.string.user_not_found));
                }
            });
        });
    }

    private void bindAddButton(View view) {
        mParticipantButton = view.findViewById(R.id.group_participant_button);

        mParticipantButton.setOnClickListener(v -> {
            mGroupViewModel.addUser(mParticipant.id, mGroupId).observe(getViewLifecycleOwner(), participant -> {
                if (participant != null) {
                    mParticipantButton.setVisibility(View.INVISIBLE);
                    mLogin.setVisibility(View.INVISIBLE);
                    mLoginInput.setText("");
                    mGroupViewModel.getGroupUsers(mGroupId);
                }
            });
        });
    }

    public static GroupAddingParticipant getInstance(String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString(GROUP_ID, groupId);
        GroupAddingParticipant fragment = new GroupAddingParticipant();
        fragment.setArguments(bundle);
        return fragment;
    }
}
