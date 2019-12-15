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

import ru.moneydeal.app.GroupViewModel;
import ru.moneydeal.app.R;
import ru.moneydeal.app.group.GroupRepo;

public class GroupListFragment extends Fragment {
    private GroupViewModel mGroupViewModel;
    private RecyclerView mRecyclerView;
    private MyDataAdapter mDataAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);

        mGroupViewModel = new ViewModelProvider(getActivity()).get(GroupViewModel.class);
        mGroupViewModel.fetchGroups();

        this.bindRecyclerView(view);
        return view;
    }

    private void bindRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.history_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDataAdapter = new MyDataAdapter(mGroupViewModel);

        mRecyclerView.setAdapter(mDataAdapter);
    }

    class MyDataAdapter extends RecyclerView.Adapter<MyViewHolder> {
        GroupRepo.GroupData mData;
        GroupViewModel mGroupViewModel;

        public MyDataAdapter(GroupViewModel viewModel) {
            if (viewModel == null) {
                return;
            }

            mGroupViewModel = viewModel;
            Log.d("GroupList Adater", "subscribe on update");
            mGroupViewModel.getGroups().observe(getViewLifecycleOwner(), groupData -> {
                Log.d("GroupList Adapter", "got new data " + groupData.groups.size());
                mData = groupData;
                this.notifyDataSetChanged();
            });
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d("GroupList", "onCreateViewHolder");

            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.history_item, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            GroupRepo.Group data = mData.groups.get(position);

            holder.mNameView.setText(data.name);
            holder.mDescriptionView.setText(data.description);
        }

        @Override
        public int getItemCount() {
            return mData == null ?  0 : mData.getSize();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNameView;
        private final TextView mDescriptionView;

        public MyViewHolder(View itemView) {
            super(itemView);

            mNameView = itemView.findViewById(R.id.group_item_name);
            mDescriptionView = itemView.findViewById(R.id.group_item_description);
        }
    }
}
