package edu.milton.miltonmobileandroid.saa;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import edu.milton.miltonmobileandroid.R;

@SuppressLint("SimpleDateFormat")
public class SimpleEvent {
//{"id":"1025","eventName":"Observatory Hill","eventDescription":"Come Hook Up!","signUp":"Yes","date":"2013-11-23","startTime":"09:30:00","endTime":"12:00:00","votes":"0"}

    private int numericalID;
    private String eventName;
    private String eventDescription;
    private boolean signUp;
    private Date eventBeginTime;
    private Date eventEndTime;
    private Date eventDate;
    private SimpleDateFormat dateParser;
    private SimpleDateFormat timeParser;
    private String eventLocation;

    public String getEventLocation() {
        return eventLocation;
    }

    private int votes;

    public SimpleEvent() {
        eventName = "Event Name";
        eventDescription = "Event Description";

    }

    public int getNumericalID() {
        return numericalID;
    }
    public String getEventName() {
        return eventName;
    }
    public String getEventDescription() {
        return eventDescription;
    }
    public boolean isSignUp() {
        return signUp;
    }

    public int getVotes() {
        return votes;
    }
    public SimpleEvent(JSONObject jobj) {
        dateParser = new SimpleDateFormat("yyyy-MM-dd");
        timeParser = new SimpleDateFormat("HH:mm:ss");
        try {
            if (!jobj.isNull("id")) {
                numericalID = Integer.parseInt(jobj.getString("id"));
            }
            if (!jobj.isNull("votes")) {
                votes = Integer.parseInt(jobj.getString("votes"));
            }
            if (!jobj.isNull("eventName")) {
                eventName = jobj.getString("eventName");
            }
            if (!jobj.isNull("eventDescription")) {
                eventDescription = jobj.getString("eventDescription");
            }
            if (!jobj.isNull("eventLocation")) {
                eventLocation = jobj.getString("eventLocation");
            } else {
                eventLocation = "Unknown Location";
            }
            if (!jobj.isNull("signUp")) {
                String b = jobj.getString("signUp");
                if (b.equals("Yes")||b.equals("yes")) {
                    signUp = true;
                }
                else if (b.equals("No")||b.equals("no")) {
                    signUp = false;
                }
                else {
                    Log.d("SAAEvent","Did not recieve the expected value for signup string");
                    signUp = false;
                }
            }
            if (!jobj.isNull("date")) {
                eventDate = dateParser.parse(jobj.getString("date"));
            }
            if (!jobj.isNull("startTime")) {
                eventBeginTime = timeParser.parse(jobj.getString("startTime"));
            }
            if (!jobj.isNull("endTime")) {
                eventEndTime = timeParser.parse(jobj.getString("endTime"));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    public Date getEventBeginTime() {
        return eventBeginTime;
    }

    public Date getEventEndTime() {
        return eventEndTime;
    }

    public Date getEventDate() {
        return eventDate;
    }


}