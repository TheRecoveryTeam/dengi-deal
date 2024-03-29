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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.moneydeal.app.CheckViewModel;
import ru.moneydeal.app.IRouter;
import ru.moneydeal.app.R;
import ru.moneydeal.app.checks.CheckEntity;
import ru.moneydeal.app.checks.CheckRepo;

public class GroupChecksFragment extends Fragment {
    private static String GROUP_ID = "GROUP_ID";
    private CheckViewModel mCheckViewModel;
    private String mGroupId;
    private RecyclerView mRecyclerView;
    private MyDataAdapter mDataAdapter;

    public static GroupChecksFragment getInstance(String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString(GROUP_ID, groupId);
        GroupChecksFragment fragment = new GroupChecksFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCheckViewModel = new ViewModelProvider(getActivity()).get(CheckViewModel.class);

        View view = inflater.inflate(
                R.layout.group_checks_fragment,
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
        view.findViewById(R.id.group_checks_add_btn).setOnClickListener(v -> {
            IRouter activity = (IRouter) getActivity();
            if (activity == null) {
                return;
            }

            activity.showCheckCreate(mGroupId);
        });

        return view;
    }

    private void bindRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.group_checks_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDataAdapter = new MyDataAdapter(mCheckViewModel);

        mRecyclerView.setAdapter(mDataAdapter);
    }

    class MyDataAdapter extends RecyclerView.Adapter<MyViewHolder> {
        CheckRepo.GroupChecks mData;

        public MyDataAdapter(CheckViewModel viewModel) {
            if (viewModel == null) {
                return;
            }

            mCheckViewModel.getChecks().observe(getViewLifecycleOwner(), data -> {
                mData = data;
                this.notifyDataSetChanged();
            });

            mCheckViewModel.fetchChecks(mGroupId);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.group_check_list_item, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CheckEntity data = mData.entities.get(position);

            holder.mNameView.setText(data.name);
            holder.mDescriptionView.setText(data.description);
            holder.mAmountView.setText(getString(R.string.amount_string, data.amount));

        }

        @Override
        public int getItemCount() {
            return mData == null || mData.entities == null ?  0 : mData.entities.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNameView;
        private final TextView mDescriptionView;
        private final TextView mAmountView;

        public MyViewHolder(View itemView) {
            super(itemView);

            mNameView = itemView.findViewById(R.id.group_check_name);
            mDescriptionView = itemView.findViewById(R.id.group_check_description);
            mAmountView = itemView.findViewById(R.id.group_check_amount);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                CheckRepo.GroupChecks data = mDataAdapter.mData;
                if (data == null) {
                    return;
                }

                List<CheckEntity> entities = data.entities;
                if (entities == null || position > entities.size()) {
                    return;
                }

                CheckEntity entity = entities.get(position);
                if (entity == null) {
                    return;
                }

                IRouter activity = (IRouter) getActivity();
                if (activity == null) {
                    return;
                }

                activity.showCheck(entity.id);
            });
        }
    }
}
