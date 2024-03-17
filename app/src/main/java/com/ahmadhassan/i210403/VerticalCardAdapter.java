package com.ahmadhassan.i210403;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
        final DatabaseReference DatabaseRef = FirebaseDatabase.getInstance().getReference("Favorite");

        Mentors mentor = mentors.get(position);

        holder.name.setText(mentor.getName());
        holder.jobTitle.setText(mentor.getJobTitle());
        holder.rate.setText(mentor.getRate());
        holder.availability.setText(mentor.getAvailability());

        if (mentor.getAvailability().equals(" 🟢 Available")) {
            holder.availability.setTextColor(Color.parseColor("#359400"));
        } else {
            holder.availability.setTextColor(Color.LTGRAY);
        }

        if (!Objects.equals(mentor.getProfilePicture(), "")) {
            Picasso.get().load(mentor.getProfilePicture()).into(holder.ProfilePic);
        }


        // Check if the mentor is favorited by the current user
        String userId = Objects.requireNonNull(UserInstance.INSTANCE.getInstance()).getUserId();
        DatabaseRef.get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Favorite favorite = snapshot.getValue(Favorite.class);
                if (favorite != null && favorite.getMentorId().equals(mentor.getMentorId()) && favorite.getUserId().equals(userId)) {
                    mentor.setFavorite("❤️");
                    holder.Favorite.setText("❤️");
                }
                else{
                    mentor.setFavorite("🩶");
                    holder.Favorite.setText("🩶");
                }
            }
            notifyDataSetChanged();
        });

//        holder.Favorite.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Toggle the favorite state
//                if (mentor.getFavorite().equals("Favorite") || mentor.getFavorite().equals("❤️")) {
//                    mentor.setFavorite("🩶");
//                    holder.Favorite.setText("🩶");
//                    //Remove from the database
//                    DatabaseRef.child(userId).child(mentor.getMentorId()).removeValue();
//                } else {
//                    mentor.setFavorite("❤️");
//                    holder.Favorite.setText("❤️");
//                    // Add to the database
//                    Favorite favorite = new Favorite(mentor.getMentorId(), userId);
//                    DatabaseRef.child(userId).child(mentor.getMentorId()).setValue(favorite);
//                }
//                notifyItemChanged(position); // Notify adapter that data set has changed
//            }
//        });

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
