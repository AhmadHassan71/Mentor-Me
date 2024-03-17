package com.ahmadhassan.i210403;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

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
        final DatabaseReference DatabaseRef = FirebaseDatabase.getInstance().getReference("Favorite");

        Mentors mentor = mentors.get(position);

        holder.name.setText(mentor.getName().split(" ")[0]);

        holder.jobTitle.setText(mentor.getJobTitle());
        holder.rate.setText(mentor.getRate());
        holder.availability.setText(mentor.getAvailability());
        holder.Favorite.setText(mentor.getFavorite());

        if (mentor.getAvailability().equals("Available") || mentor.getAvailability().equals(" 🟢 Available")) {
            holder.availability.setTextColor(Color.parseColor("#359400"));
            holder.availability.setText(" 🟢 Available");
        } else {
            holder.availability.setTextColor(Color.LTGRAY);
            holder.availability.setText(" ⚪ Not Available");
        }

        // Check if current mentor is in the favorites for current user
        DatabaseRef.orderByChild("userId").equalTo(Objects.requireNonNull(UserInstance.INSTANCE.getInstance()).getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isFavorite = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Favorite favorite = snapshot.getValue(Favorite.class);
                            if (favorite != null && favorite.getMentorId().equals(mentor.getMentorId())) {
                                isFavorite = true;
                                break;
                            }
                        }
                        // Update the favorite status
                        if (isFavorite) {
                            mentor.setFavorite("❤️");
                        } else {
                            mentor.setFavorite("🩶");
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });

        holder.Favorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Toggle the favorite state
                if (mentor.getFavorite().equals("Favorite") || mentor.getFavorite().equals("❤️")) {
                    mentor.setFavorite("🩶");
                    // Remove from the database
                    DatabaseRef.child(Objects.requireNonNull(UserInstance.INSTANCE.getInstance()).getUserId()).child(mentor.getMentorId()).removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Favorite", "Removed from favorites");
                                } else {
                                    Log.d("Favorite", "Failed to remove from favorites");
                                }
                            });
                } else {
                    mentor.setFavorite("❤️");
                    // Add to the database
                    Favorite favorite = new Favorite(mentor.getMentorId(), (Objects.requireNonNull(UserInstance.INSTANCE.getInstance()).getUserId()));
                    DatabaseRef.child(UserInstance.INSTANCE.getInstance().getUserId()).child(mentor.getMentorId()).setValue(favorite)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Favorite", "Added to favorites");
                                } else {
                                    Log.d("Favorite", "Failed to add to favorites");
                                }
                            });
                }
                notifyItemChanged(position); // Notify adapter that data set has changed
            }
        });

        if (!Objects.equals(mentor.getProfilePicture(), "")) {
            Picasso.get().load(mentor.getProfilePicture()).into(holder.ProfilePic);
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
