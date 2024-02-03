package com.ahmadhassan.i210403;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView jobTitle;
    TextView rate;
    TextView availability;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.MentorName);
        jobTitle = itemView.findViewById(R.id.JobTitle);
        rate = itemView.findViewById(R.id.Rate);
        availability = itemView.findViewById(R.id.Availability);
    }
}