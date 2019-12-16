package com.ramya.inspirationrewards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class LeaderAdapter extends RecyclerView.Adapter<LeaderViewHolder> {
    private static final String TAG = "LeaderAdapter";
    private List<LeaderBoardInspiration> LeaderArrayList;
    private LeaderBoardActivityInspiration LeaderBoardActInspiration;

    public LeaderAdapter(LeaderBoardActivityInspiration LeaderBoardActivityInspiration, List<LeaderBoardInspiration> inspLeaderArrayList) {
        this.LeaderArrayList = inspLeaderArrayList;
        this.LeaderBoardActInspiration = LeaderBoardActivityInspiration;
    }

    @NonNull
    @Override
    public LeaderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View iV = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.leaderboard_entry, viewGroup, false);
        iV.setOnClickListener(LeaderBoardActInspiration);
        iV.setOnLongClickListener(LeaderBoardActInspiration);
        return new LeaderViewHolder(iV);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderViewHolder GetSetLeaderViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        LeaderBoardInspiration leadinsp = LeaderArrayList.get(position);
        GetSetLeaderViewHolder.inspLeadName.setText(leadinsp.getLastName() + ", " + leadinsp.getFirstName());
        GetSetLeaderViewHolder.inspLeadPosDept.setText(leadinsp.getPosition() + ", " + leadinsp.getDepartment());
        GetSetLeaderViewHolder.inspLeadPoints.setText(""+leadinsp.rewardPtAward);
        String imgStr = leadinsp.getImageBytes();
        byte[] imgBytes = Base64.decode(imgStr, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        GetSetLeaderViewHolder.inspLeadImge.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return LeaderArrayList.size();
    }
}
