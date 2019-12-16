package com.ramya.inspirationrewards;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileViewHolder extends RecyclerView.ViewHolder {
    public TextView userPrDate;
    public TextView userPrName;
    public TextView userPrPts;
    public TextView userPrText;
    public ImageView userPrImge;

    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
        userPrDate = itemView.findViewById(R.id.rewarddate);
        userPrName = itemView.findViewById(R.id.rewardname);
        userPrPts = itemView.findViewById(R.id.rewardpts);
        userPrText = itemView.findViewById(R.id.rewardtext);
        userPrImge = itemView.findViewById(R.id.rewardimg);
    }
}