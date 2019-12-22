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

import java.util.Map;

import ru.moneydeal.app.CheckViewModel;
import ru.moneydeal.app.R;
import ru.moneydeal.app.checks.CheckChunkEntity;
import ru.moneydeal.app.checks.CheckEntity;
import ru.moneydeal.app.userList.UserEntity;

public class CheckFragment extends Fragment {
    private static String CHECK_ID = "CHECK_ID";
    private String mCheckId;
    private CheckViewModel mCheckViewModel;
    private TextView mName;
    private TextView mDescription;
    private TextView mAmount;
    private TextView mPayer;
    private CheckEntity mLoadedCheck;
    private RecyclerView mRecyclerView;
    private MyDataAdapter mDataAdapter;
    private Map<String, UserEntity> mUsers;

    public static CheckFragment getInstance(String checkId) {
        Bundle bundle = new Bundle();
        bundle.putString(CHECK_ID, checkId);
        CheckFragment fragment = new CheckFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCheckViewModel = new ViewModelProvider(getActivity()).get(CheckViewModel.class);

        View view = inflater.inflate(R.layout.check_fragment, container, false);

        Bundle bundle = getArguments();
        if (bundle == null) {
            return view;
        }

        mCheckId = bundle.getString(CHECK_ID);
        if (mCheckId == null) {
            return view;
        }

        mLoadedCheck = mCheckViewModel.getLoadedCheck(mCheckId);
        if (mLoadedCheck == null) {
            return view;
        }

        Log.d("@CheckFragment", "chunk count " + mLoadedCheck.chunks.size());

        mName = view.findViewById(R.id.check_name);
        mDescription = view.findViewById(R.id.check_description);
        mAmount = view.findViewById(R.id.check_amount);
        mPayer = view.findViewById(R.id.check_author);

        mName.setText(mLoadedCheck.name);
        mDescription.setText(mLoadedCheck.description);
        mAmount.setText(getString(R.string.amount_string, mLoadedCheck.amount));

        mCheckViewModel.getCheckUsers(mCheckId).observe(getViewLifecycleOwner(), map -> {
            mUsers = map;
            mPayer.setText(getUserFullName(mLoadedCheck.userId));
            bindRecyclerView(view);
        });

        return view;
    }

    private String getUserFullName(String userId) {
        UserEntity userEntity = mUsers.get(userId);
        if (userEntity == null) {
            return getString(R.string.user_unknown);
        }

        return getString(R.string.user_full_name, userEntity.firstName, userEntity.lastName);
    }

    private String getUserLogin(String userId) {
        UserEntity userEntity = mUsers.get(userId);
        if (userEntity == null) {
            return getString(R.string.user_unknown);
        }

        return userEntity.login;
    }

    private void bindRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.check_chunks_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDataAdapter = new MyDataAdapter();

        mRecyclerView.setAdapter(mDataAdapter);
    }

    class MyDataAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.check_chunk_list_item, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CheckChunkEntity data = mLoadedCheck.chunks.get(position);

            holder.mAmountView.setText(getString(R.string.amount_string, data.amount));
            holder.mNameView.setText(getUserFullName(data.userId));

            holder.mLoginView.setText(getUserLogin(data.userId));
        }

        @Override
        public int getItemCount() {
            return mLoadedCheck == null || mLoadedCheck.chunks == null ?  0 : mLoadedCheck.chunks.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNameView;
        private final TextView mAmountView;
        private final TextView mLoginView;

        public MyViewHolder(View itemView) {
            super(itemView);

            mNameView = itemView.findViewById(R.id.check_chunk_name);
            mAmountView = itemView.findViewById(R.id.check_chunk_amount);
            mLoginView = itemView.findViewById(R.id.check_chunk_login);
        }
    }
}
