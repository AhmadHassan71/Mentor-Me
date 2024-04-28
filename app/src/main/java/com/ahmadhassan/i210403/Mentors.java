package com.ahmadhassan.i210403;
import java.io.Serializable;
import android.graphics.Color;

public class Mentors implements  Serializable {

    private String mentorId;
    private String name;
    private String jobTitle;
    private String rate;
    private String availability;

    private String favorite;

    private String description;

    private String company;

    private String profilePicture;


    public Mentors(String mentorId, String name, String jobTitle, String rate, String Availability, String Favorite, String ProfilePic, String Description, String Company)  {
        this.mentorId = mentorId;
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
        if(Favorite.equals("Favorite") || Favorite.equals("❤️")){
            this.favorite = "❤️";
        } else {
            this.favorite = "🩶";
        }

        this.profilePicture = ProfilePic;

        this.description = Description;
        this.company = Company;

    }

    public Mentors(){

    }
    // Getters
    public String getMentorId() {
        return mentorId;
    }
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getFavorite() {
        return favorite;
    }

    public String getDescription() {
        return description;
    }
    public String getCompany() {
        return company;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }
}
