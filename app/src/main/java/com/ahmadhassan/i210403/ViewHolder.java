package com.ahmadhassan.i210403;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView jobTitle;
    TextView rate;
    TextView availability;
    TextView Favorite;





    private OnItemClickListener itemClickListener;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.MentorName);
        jobTitle = itemView.findViewById(R.id.JobTitle);
        rate = itemView.findViewById(R.id.Rate);
        availability = itemView.findViewById(R.id.Availability);
        Favorite = itemView.findViewById(R.id.Favorite);

        // Set click listener for the itemView
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ensure the itemClickListener is not null and call its onItemClick method
                if (itemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(position);
                    }
                }
            }
        });
    }

    // Setter method for itemClickListener
    public void setItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    // Interface for click listener
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
