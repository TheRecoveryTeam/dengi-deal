package ru.moneydeal.app.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ru.moneydeal.app.GroupViewModel;
import ru.moneydeal.app.R;

public class GroupFragment extends Fragment {
    private static String GROUP_ID = "GROUP_ID";
    private GroupViewModel mGroupViewModel;
    private TextView mName;
    private TextView mDescription;
    private String mGroupId;

    public static GroupFragment getInstance(String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString(GROUP_ID, groupId);
        GroupFragment fragment = new GroupFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mGroupViewModel = new ViewModelProvider(getActivity()).get(GroupViewModel.class);

        View view = inflater.inflate(R.layout.group_fragment, container, false);

        Bundle bundle = getArguments();
        if (bundle == null) {
            return view;
        }

        mGroupId = bundle.getString(GROUP_ID);

        bindViews(view);
        createUsersList();
        createChecksList();
        createAddingParticipant();

        return view;
    }

    private void bindViews(View view) {
        mName = view.findViewById(R.id.group_item_name);
        mDescription = view.findViewById(R.id.group_item_description);
    }

    private void createUsersList() {
        GroupUsersFragment groupUsers = GroupUsersFragment.getInstance(mGroupId);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.group_users_container, groupUsers)
                .commit();
    }

    private void createChecksList() {
        GroupChecksFragment groupUsers = GroupChecksFragment.getInstance(mGroupId);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.group_checks_container, groupUsers)
                .commit();
    }

    private void createAddingParticipant() {
        GroupAddingParticipant fragment = GroupAddingParticipant.getInstance(mGroupId);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.group_add_participant_container, fragment)
                .commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mGroupViewModel.getGroup().observe(getViewLifecycleOwner(), group -> {
            if (group == null) {
                return;
            }

            mName.setText(group.name);
            mDescription.setText(group.description);
        });

        if (mGroupId == null) {
            return;
        }
        mGroupViewModel.selectGroup(mGroupId);
    }
}
