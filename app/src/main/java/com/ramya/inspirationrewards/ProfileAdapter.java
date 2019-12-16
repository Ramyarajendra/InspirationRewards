package com.ramya.inspirationrewards;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileViewHolder> {
    private static final String TAG = "ProfileAdapter";
    List<RewardRecords> rAList = new ArrayList<>();
    private ProfileActivity pact;

    public ProfileAdapter(ProfileActivity userProfileActivity, List<RewardRecords> rewardsArrayList) {
        this.pact = userProfileActivity;
        this.rAList = rewardsArrayList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rewardhistory, viewGroup, false);
        itemView.setOnClickListener(pact);
        itemView.setOnLongClickListener(pact);
        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder userProfileViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        RewardRecords rewardRecords = rAList.get(position);
        userProfileViewHolder.userPrDate.setText(rewardRecords.getDate());
        userProfileViewHolder.userPrName.setText(rewardRecords.getName());
        userProfileViewHolder.userPrText.setText(rewardRecords.getNotes());
        userProfileViewHolder.userPrPts.setText("" + rewardRecords.getValue());
    }

    @Override
    public int getItemCount() {
        return rAList.size();
    }
}
