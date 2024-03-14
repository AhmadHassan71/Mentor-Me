package com.ahmadhassan.i210403;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Mentors> mentors;
    private OnItemClickListener onItemClickListener;

    public CardAdapter(List<Mentors> mentors, Context context) {
        this.mentors = mentors;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card, parent, false);
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

        if (mentor.getAvailability().equals("Available")|| mentor.getAvailability().equals(" 🟢 Available")) {
            holder.availability.setTextColor(Color.parseColor("#359400"));
            holder.availability.setText(" 🟢 Available");
        } else {
            holder.availability.setTextColor(Color.LTGRAY);
            holder.availability.setText(" ⚪ Not Available");

        }
        if (mentor.getFavorite().equals("Favorite") || mentor.getFavorite().equals("❤️")){
            holder.Favorite.setText("❤️");

        }else {
            holder.Favorite.setText("🩶");
        }



        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(mentor);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mentors.size();
    }

    // Setter method for the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Interface for click listener
    public interface OnItemClickListener {
        void onItemClick(Mentors mentor);
    }
}
