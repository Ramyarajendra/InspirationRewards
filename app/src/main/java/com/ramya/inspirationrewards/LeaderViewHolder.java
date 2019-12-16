package com.ramya.inspirationrewards;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LeaderViewHolder extends RecyclerView.ViewHolder {
    public TextView inspLeadName;
    public TextView inspLeadPoints;
    public TextView inspLeadPosDept;
    public ImageView inspLeadImge;

    public LeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        inspLeadName = itemView.findViewById(R.id.LeadName);
        inspLeadPosDept = itemView.findViewById(R.id.LeadPosDept);
        inspLeadPoints = itemView.findViewById(R.id.LeadPoints);
        inspLeadImge = itemView.findViewById(R.id.LeadImge);
    }
}
