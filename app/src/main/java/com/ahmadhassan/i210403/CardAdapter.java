// file 2
package com.ahmadhassan.i210403;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final LayoutInflater inflater;
    private List<Mentors> mentors;

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
    }

    @Override
    public int getItemCount() {
        return mentors.size();
    }

}
