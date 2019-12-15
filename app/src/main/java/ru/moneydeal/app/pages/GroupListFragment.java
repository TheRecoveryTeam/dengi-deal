package ru.moneydeal.app.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.moneydeal.app.GroupViewModel;
import ru.moneydeal.app.IRouter;
import ru.moneydeal.app.MainActivity;
import ru.moneydeal.app.R;
import ru.moneydeal.app.group.GroupEntity;

public class GroupListFragment extends Fragment {
    private GroupViewModel mGroupViewModel;
    private RecyclerView mRecyclerView;
    private MyDataAdapter mDataAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_list_fragment, container, false);

        mGroupViewModel = new ViewModelProvider(getActivity()).get(GroupViewModel.class);
        mGroupViewModel.fetchGroups();

        bindRecyclerView(view);
        return view;
    }

    private void bindRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.history_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDataAdapter = new MyDataAdapter(mGroupViewModel);

        mRecyclerView.setAdapter(mDataAdapter);
    }

    class MyDataAdapter extends RecyclerView.Adapter<MyViewHolder> {
        List<GroupEntity> mData;
        GroupViewModel mGroupViewModel;

        public MyDataAdapter(GroupViewModel viewModel) {
            if (viewModel == null) {
                return;
            }

            mGroupViewModel = viewModel;
            mGroupViewModel.getGroups().observe(getViewLifecycleOwner(), groupData -> {
                mData = groupData;
                this.notifyDataSetChanged();
            });
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.group_list_item, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            GroupEntity data = mData.get(position);

            holder.mNameView.setText(data.name);
            holder.mDescriptionView.setText(data.description);
        }

        @Override
        public int getItemCount() {
            return mData == null ?  0 : mData.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNameView;
        private final TextView mDescriptionView;

        public MyViewHolder(View itemView) {
            super(itemView);

            mNameView = itemView.findViewById(R.id.group_item_name);
            mDescriptionView = itemView.findViewById(R.id.group_item_description);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position > mDataAdapter.mData.size()) {
                    return;
                }

                GroupEntity entity = mDataAdapter.mData.get(position);
                if (entity == null) {
                    return;
                }

                IRouter activity = (IRouter) getActivity();
                if (activity == null) {
                    return;
                }

                activity.showFragmentGroup(entity.id);
            });
        }
    }
}
