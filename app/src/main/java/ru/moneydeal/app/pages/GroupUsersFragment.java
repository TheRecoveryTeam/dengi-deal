package ru.moneydeal.app.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.moneydeal.app.GroupViewModel;
import ru.moneydeal.app.R;
import ru.moneydeal.app.userList.UserEntity;

public class GroupUsersFragment extends Fragment {
    private static String GROUP_ID = "GROUP_ID";
    private GroupViewModel mGroupViewModel;
    private RecyclerView mRecyclerView;
    private MyDataAdapter mDataAdapter;
    private String mGroupId;


    public static GroupUsersFragment getInstance(String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString(GROUP_ID, groupId);
        GroupUsersFragment fragment = new GroupUsersFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mGroupViewModel = new ViewModelProvider(getActivity()).get(GroupViewModel.class);

        View view = inflater.inflate(
                R.layout.group_users_fragment,
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

        bindRecyclerView(view);

        return view;
    }

    private void bindRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.group_users_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDataAdapter = new MyDataAdapter(mGroupViewModel);

        mRecyclerView.setAdapter(mDataAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }

        mGroupViewModel.getGroupUsers(mGroupId).observe(getViewLifecycleOwner(), users -> {
            for(UserEntity userEntity: users) {
                Log.d("@ GroupFragment", userEntity.login);
            }
        });
    }

    class MyDataAdapter extends RecyclerView.Adapter<MyViewHolder> {
        List<UserEntity> mData;
        GroupViewModel mGroupViewModel;

        public MyDataAdapter(GroupViewModel viewModel) {
            if (viewModel == null) {
                return;
            }

            mGroupViewModel = viewModel;
            mGroupViewModel.getGroupUsers(mGroupId).observe(getViewLifecycleOwner(), usersData -> {
                mData = usersData;
                this.notifyDataSetChanged();
            });
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.group_user_list_item, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            UserEntity data = mData.get(position);

            String name = getString(R.string.user_full_name, data.firstName, data.lastName);
            holder.mNameView.setText(name);
            holder.mLoginView.setText(data.login);
        }

        @Override
        public int getItemCount() {
            return mData == null ?  0 : mData.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNameView;
        private final TextView mLoginView;

        public MyViewHolder(View itemView) {
            super(itemView);

            mNameView = itemView.findViewById(R.id.group_user_name);
            mLoginView = itemView.findViewById(R.id.group_user_login);
        }
    }
}
