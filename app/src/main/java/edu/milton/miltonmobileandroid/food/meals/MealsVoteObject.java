package edu.milton.miltonmobileandroid.food.meals;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("SimpleDateFormat")
public class MealsVoteObject {

    private int numericalID;
    private int mealID;
    private String email;
    private int vote;
    private String date;

    public MealsVoteObject(int numericalID, int mealID, String email, int vote, String date){
        this.numericalID=numericalID;
        this.mealID = mealID;
        this.email = email;
        this.vote = vote;
        this.date = date;
    }
    public MealsVoteObject(int mealID, String email, int vote, String date){
        numericalID = 0;
        this.mealID = mealID;
        this.email = email;
        this.vote = vote;
        this.date = date;
    }

    public MealsVoteObject(JSONObject jobj) {

        try {
            if (!jobj.isNull("id")) {
                setNumericalID(Integer.parseInt(jobj.getString("id")));
            }
			/*
			 * if (!jobj.isNull("votes")) { votes =
			 * Integer.parseInt(jobj.getString("votes")); }
			 */
            if (!jobj.isNull("mealid")) {
                setMealID(Integer.parseInt(jobj.getString("mealid")));
            }

            if (!jobj.isNull("email")) {
                setEmail(jobj.getString("email"));
            }

            if (!jobj.isNull("vote")) {
                setVote(Integer.parseInt(jobj.getString("vote")));

            }
            if(!jobj.isNull("date")){
                setDate(jobj.getString("date"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setDate(String date) {
        this.date = date;

    }
    public String getDate(){
        return date;
    }
    public int getNumericalID() {
        return numericalID;
    }

    public void setNumericalID(int numericalID) {
        this.numericalID = numericalID;
    }

    public int getMealID() {
        return mealID;
    }

    public void setMealID(int mealID) {
        this.mealID = mealID;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
