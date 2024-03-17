package com.ahmadhassan.i210403;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class VerticalCardAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Mentors> mentors;
    private OnItemClickListener itemClickListener;

    public VerticalCardAdapter(List<Mentors> mentors, Context context) {
        this.mentors = mentors;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.verticalcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mentors mentor = mentors.get(position);

        holder.name.setText(mentor.getName());
        holder.jobTitle.setText(mentor.getJobTitle());
        holder.rate.setText(mentor.getRate());
        holder.availability.setText(mentor.getAvailability());
        holder.Favorite.setText(mentor.getFavorite());

        if (mentor.getAvailability().equals(" 🟢 Available")) {
            holder.availability.setTextColor(Color.parseColor("#359400"));
        } else {
            holder.availability.setTextColor(Color.LTGRAY);
        }

        if(!Objects.equals(mentor.getProfilePicture(), "")) {
            Picasso.get().load(mentor.getProfilePicture()).into(holder.ProfilePic);
        }

        // Set click listener
        holder.setItemClickListener(new ViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (itemClickListener != null) {
                    // Pass the clicked mentor to the activity
                    itemClickListener.onItemClick(mentor);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mentors.size();
    }

    // Setter method for the itemClickListener
    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    // Interface for click listener
    public interface OnItemClickListener {
        void onItemClick(Mentors mentor);
    }
}
