package com.ahmadhassan.i210403;

import android.graphics.Color;

public class Mentors {
    private String name;
    private String jobTitle;
    private String rate;
    private String Availability;

    public Mentors(String name, String jobTitle, String rate, String Availability) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.rate = rate;
        if(Availability.equals("Available")) {
            this.Availability = " 🟢 Available";
            // make text color green

        } else {
            this.Availability = " ⚪ Not Available";
            // make text color silver
        }

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
        return Availability;
    }
}
