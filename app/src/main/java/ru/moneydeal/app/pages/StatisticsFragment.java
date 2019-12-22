package ru.moneydeal.app.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import ru.moneydeal.app.group.StatisticEntity;

public class StatisticsFragment extends Fragment {
    private static String GROUP_ID = "GROUP_ID";
    private GroupViewModel mGroupViewModel;
    private RecyclerView mRecyclerView;
    private MyDataAdapter mDataAdapter;
    private String mGroupId;

    public static StatisticsFragment getInstance(String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString(GROUP_ID, groupId);
        StatisticsFragment fragment = new StatisticsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_stats, container, false);

        mGroupViewModel = new ViewModelProvider(getActivity()).get(GroupViewModel.class);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return view;
        }

        mGroupId = bundle.getString(GROUP_ID);

        bindRecyclerView(view);
        return view;
    }

    private void bindRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.stats_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDataAdapter = new MyDataAdapter(mGroupViewModel);

        mRecyclerView.setAdapter(mDataAdapter);
    }

    class MyDataAdapter extends RecyclerView.Adapter<MyViewHolder> {
        List<StatisticEntity> mData;
        GroupViewModel mGroupViewModel;

        public MyDataAdapter(GroupViewModel viewModel) {
            if (viewModel == null) {
                return;
            }

            mGroupViewModel = viewModel;
            mGroupViewModel.getStatistics(mGroupId).observe(getViewLifecycleOwner(), stats -> {
                mData = stats;
                this.notifyDataSetChanged();
            });
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.group_stats_item, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            StatisticEntity data = mData.get(position);

            holder.mFrom.setText(data.from);
            holder.mTo.setText(data.to);
            holder.mAmount.setText(getString(R.string.amount_string, data.amount));
        }

        @Override
        public int getItemCount() {
            return mData == null ?  0 : mData.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView mFrom;
        private final TextView mTo;
        private final TextView mAmount;

        public MyViewHolder(View itemView) {
            super(itemView);

            mFrom = itemView.findViewById(R.id.from_stats);
            mTo = itemView.findViewById(R.id.to_stats);
            mAmount = itemView.findViewById(R.id.amount_stats);
        }
    }
}
