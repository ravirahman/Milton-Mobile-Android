package edu.milton.miltonmobileandroid.flik;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class MenuItem {

    private int numericalID;
    private Date itemDate;
    private String itemName;
    private String itemClass;
    private String itemTime;
    private SimpleDateFormat dateParser;
    private boolean isHeading;
    private int votes;
    private int upvotes;
    private int downvotes;

    public MenuItem(boolean heading, String itemName) {
        setHeading(heading);
        this.setItemName(itemName);
    }

    public MenuItem(boolean heading, JSONObject jobj) {
        setHeading(heading);
        dateParser = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (!jobj.isNull("id")) {
                setNumericalID(Integer.parseInt(jobj.getString("id")));
            }
            downvotes = 0;
            upvotes = 0;
			/*
			 * if (!jobj.isNull("votes")) { votes =
			 * Integer.parseInt(jobj.getString("votes")); }
			 */
            if (!jobj.isNull("mealName")) {
                setItemName(jobj.getString("mealName"));
            }
            if (!jobj.isNull("date")) {
                setItemDate(dateParser.parse(jobj.getString("date")));
            }
            if (!jobj.isNull("mealClass")) {
                setItemClass(jobj.getString("mealClass"));
            }
            if (!jobj.isNull("mealTime")) {
                setItemTime(jobj.getString("mealTime"));
            }
            if (!jobj.isNull("likes")) {
                setVotes(Integer.parseInt(jobj.getString("votes")));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getNumericalID() {
        return numericalID;
    }

    public void setNumericalID(int numericalID) {
        this.numericalID = numericalID;
    }

    public Date getItemDate() {
        return itemDate;
    }

    public String getDateString() {
        return (dateParser.format(itemDate));
    }

    public void setItemDate(Date itemDate) {
        this.itemDate = itemDate;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemClass() {
        return itemClass;
    }

    public void setItemClass(String itemClass) {
        this.itemClass = itemClass;
    }

    public String getItemTime() {
        return itemTime;
    }

    public void setItemTime(String itemTime) {
        this.itemTime = itemTime;
    }

    public boolean isHeading() {
        return isHeading;
    }

    public void setHeading(boolean isHeading) {
        this.isHeading = isHeading;
    }

    public int getVotes() {
        return votes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }
    public int getDownvotes() {
        return downvotes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

}
