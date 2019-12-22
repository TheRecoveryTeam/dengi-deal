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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.moneydeal.app.CheckViewModel;
import ru.moneydeal.app.GroupViewModel;
import ru.moneydeal.app.IRouter;
import ru.moneydeal.app.R;
import ru.moneydeal.app.checks.BaseCheckEntity;
import ru.moneydeal.app.checks.CheckChunkEntity;
import ru.moneydeal.app.checks.CheckEntity;
import ru.moneydeal.app.checks.CheckRepo;
import ru.moneydeal.app.network.CheckApi;
import ru.moneydeal.app.userList.UserEntity;

public class CheckCreateFragment extends Fragment {
    private static String GROUP_ID = "GROUP_ID";
    private static String IS_UNEQUAL_CHUNKS_MODE = "IS_UNEQUAL_CHUNKS_MODE";

    private Boolean mIsUnequalChunksMode = false;
    private CheckViewModel mCheckViewModel;
    private GroupViewModel mGroupViewModel;
    private String mGroupId;
    private View mEqBlock;
    private View mUneqBlock;
    private Button mToggleButton;
    private List<UserEntity> mGroupUsers;
    private RecyclerView mRecyclerView;
    private MyDataAdapter mDataAdapter;

    public static CheckCreateFragment getInstance(String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString(GROUP_ID, groupId);
        CheckCreateFragment fragment = new CheckCreateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewModelProvider modelProvider = new ViewModelProvider(getActivity());
        mCheckViewModel = modelProvider.get(CheckViewModel.class);
        mGroupViewModel = modelProvider.get(GroupViewModel.class);

        View view = inflater.inflate(R.layout.check_create_fragment, container, false);

        Bundle bundle = getArguments();
        if (bundle == null) {
            return view;
        }

        mGroupId = bundle.getString(GROUP_ID);
        if (mGroupId == null) {
            return view;
        }

        restoreIsUnequalChunksMode(savedInstanceState);

        mToggleButton = view.findViewById(R.id.check_create_toggle_btn);
        mEqBlock = view.findViewById(R.id.check_create_equal_chunks_block);
        mUneqBlock = view.findViewById(R.id.check_create_unequal_chunks_block);

        mToggleButton.setOnClickListener(v -> {
            mIsUnequalChunksMode = !mIsUnequalChunksMode;
            updateBlocksVisibility();
        });

        updateBlocksVisibility();

        mGroupViewModel.getGroupUsers().observe(getViewLifecycleOwner(), users -> {
            mGroupUsers = users;
            bindRecyclerView(view);
        });
        mGroupViewModel.fetchGroupUsers(mGroupId);

        EditText nameInput = view.findViewById(R.id.check_create_name_input);
        EditText descriptionInput = view.findViewById(R.id.check_create_description_input);
        EditText amountInput = view.findViewById(R.id.check_create_amount_input);
        Button saveButton = view.findViewById(R.id.check_create_save_btn);

        View.OnClickListener saveClickListener = v -> {
            if (mGroupUsers == null || mGroupUsers.size() == 0) {
                return;
            }

            if (mIsUnequalChunksMode) {
                return;
            }

            String amountValue = amountInput.getText().toString();
            Integer amountPerChunk = Integer.parseInt(amountValue) / mGroupUsers.size();

            ArrayList<CheckChunkEntity> chunks = new ArrayList<>();
            for (UserEntity user : mGroupUsers) {
                chunks.add(new CheckChunkEntity(null, user.id, amountPerChunk));
            }

            mCheckViewModel.createCheck(new BaseCheckEntity(
                    nameInput.getText().toString(),
                    descriptionInput.getText().toString(),
                    mGroupId,
                    chunks
            )).observe(getViewLifecycleOwner(), progress -> {
                if (progress == CheckRepo.Progress.SUCCESS) {
                    handleCheckCreated();
                }
            });
        };
        saveButton.setOnClickListener(saveClickListener);

        return view;
    }

    void handleCheckCreated() {
        IRouter activity = (IRouter) getActivity();

        if (activity != null) {
            activity.back();
        }
    }

    void restoreIsUnequalChunksMode(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mIsUnequalChunksMode = false;
        } else {
            mIsUnequalChunksMode = savedInstanceState.getBoolean(IS_UNEQUAL_CHUNKS_MODE);
            Log.d("@ onCreateView", IS_UNEQUAL_CHUNKS_MODE + " " + mIsUnequalChunksMode);
            if (mIsUnequalChunksMode == null) {
                mIsUnequalChunksMode = false;
            }
        }

    }

    void updateBlocksVisibility() {
        if (mIsUnequalChunksMode) {
            mEqBlock.setVisibility(View.GONE);
            mUneqBlock.setVisibility(View.VISIBLE);
            mToggleButton.setText(getString(R.string.split_for_equal_chunks));
        } else {
            mEqBlock.setVisibility(View.VISIBLE);
            mUneqBlock.setVisibility(View.GONE);
            mToggleButton.setText(getString(R.string.split_for_unequal_chunks));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("@ onSaveInstanceState", IS_UNEQUAL_CHUNKS_MODE + " " + mIsUnequalChunksMode);
        outState.putBoolean(IS_UNEQUAL_CHUNKS_MODE, mIsUnequalChunksMode);
    }

    private void bindRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.check_create_chunk_list);

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
                    .inflate(R.layout.check_create_chunk_item, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            UserEntity data = mGroupUsers.get(position);

            holder.mNameView.setText(getString(R.string.user_full_name, data.firstName, data.lastName));
            holder.mLoginView.setText(data.login);
        }

        @Override
        public int getItemCount() {
            return mGroupUsers == null ?  0 : mGroupUsers.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNameView;
        private final EditText mAmountInput;
        private final TextView mLoginView;

        public MyViewHolder(View itemView) {
            super(itemView);

            mNameView = itemView.findViewById(R.id.check_create_chunk_name);
            mAmountInput = itemView.findViewById(R.id.check_create_chunk_amount);
            mLoginView = itemView.findViewById(R.id.check_create_chunk_login);
        }
    }
}
