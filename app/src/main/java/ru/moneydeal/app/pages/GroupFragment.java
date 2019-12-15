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
    private GroupViewModel mGroupViewModel;
    private TextView mName;
    private TextView mDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mGroupViewModel = new ViewModelProvider(getActivity()).get(GroupViewModel.class);

        View view = inflater.inflate(R.layout.group_fragment, container, false);

        bindViews(view);

        return view;
    }

    private void bindViews(View view) {
        mName = view.findViewById(R.id.group_item_name);
        mDescription = view.findViewById(R.id.group_item_description);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}