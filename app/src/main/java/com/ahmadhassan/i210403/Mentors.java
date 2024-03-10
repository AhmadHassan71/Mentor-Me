package com.ahmadhassan.i210403;

import android.graphics.Color;

public class Mentors {
    private String name;
    private String jobTitle;
    private String rate;
    private String availability;

    private String favorite;

    public Mentors(String name, String jobTitle, String rate, String Availability, String Favorite) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.rate = rate;
        if(Availability.equals("Available") || Availability.equals(" 🟢 Available")){
            this.availability = " 🟢 Available";
            // make text color green

        } else {
            this.availability = " ⚪ Not Available";
            // make text color silver
        }
        if(Favorite.equals("Favorite")){
            this.favorite = "❤️";
        } else {
            this.favorite = "🩶";
        }

    }

    public Mentors(){

    }

    // Getters
    public String getName() {
        return name;
    }
    public String getJobTitle() {
        return jobTitle;
    }
    public String getRate() {
        return rate;
    }
    public String getAvailability() {
        return availability;
    }

    public String getFavorite() {
        return favorite;
    }
}
