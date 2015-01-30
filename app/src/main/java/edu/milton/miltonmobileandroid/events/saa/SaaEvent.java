//Geoffrey Owens 2013. version 1.0.0. please don't change me.
package edu.milton.miltonmobileandroid.events.saa;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SaaEvent {
    private String eventTitle;		//name of the event
    private String eventDescription;//medium length description of the event
    private Date eventDate;			//date the event occurs on. can be obtained in YYYY-MM-DD format via toString()
    private Date eventBeginTime;	//time of beginning of event. toString() returns time in {t 'hh:mm:ss'} format.
    private Date eventEndTime;		//time of end of event
    private boolean boarders;		//whether the event applies to boarders
    private boolean clI;			//whether the event applies to class I students
    private boolean clII;			//whether the event applies to class II students
    private boolean clIII;			//etc
    private boolean clIV;			//etc
    private boolean dayStudents;	//etc
    private String eventCategory; 	//category the event falls under
    private int numericalID;
    private int votes;

    public int getNumericalID() {
        return numericalID;
    }

    public void setNumericalID(int numericalID) {
        this.numericalID = numericalID;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public boolean isSignUp() {
        return signUp;
    }

    public void setSignUp(boolean signUp) {
        this.signUp = signUp;
    }

    private String eventLocation;
    private boolean signUp;

    //request constructor
    public SaaEvent(String eventTitle, String eventDescription, Date eventDate, Date eventBeginTime, Date eventEndTime, boolean boarders, boolean clI, boolean clII, boolean clIII, boolean clIV, boolean dayStudents, String eventCategory) {
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventBeginTime = eventBeginTime;
        this.eventEndTime = eventEndTime;
        this.boarders = boarders;
        this.clI = clI;
        this.clII = clII;
        this.clIII = clIII;
        this.clIV = clIV;
        this.dayStudents = dayStudents;
        this.eventCategory = eventCategory;
    }

    //light constructor, sets availability to all users.
    public SaaEvent(String eventTitle, String eventDescription, Date eventDate, Date eventBeginTime, Date eventEndTime) {
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventBeginTime = eventBeginTime;
        this.eventEndTime = eventEndTime;
        this.boarders = true;
        this.clI = true;
        this.clII = true;
        this.clIII = true;
        this.clIV = true;
        this.dayStudents = true;
    }
    public String getEventCategory() {
        return eventCategory;
    }
    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }
    public String getEventTitle() {
        return eventTitle;
    }
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
    public String getEventDescription() {
        return eventDescription;
    }
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
    public Date getEventDate() {
        return eventDate;
    }
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
    public Date getEventBeginTime() {
        return eventBeginTime;
    }
    public void setEventBeginTime(Date eventBeginTime) {
        this.eventBeginTime = eventBeginTime;
    }
    public Date getEventEndTime() {
        return eventEndTime;
    }
    public void setEventEndTime(Date eventEndTime) {
        this.eventEndTime = eventEndTime;
    }
    public boolean isBoarders() {
        return boarders;
    }
    public void setBoarders(boolean boarders) {
        this.boarders = boarders;
    }
    public boolean isClI() {
        return clI;
    }
    public void setClI(boolean clI) {
        this.clI = clI;
    }
    public boolean isClII() {
        return clII;
    }
    public void setClII(boolean clII) {
        this.clII = clII;
    }
    public boolean isClIII() {
        return clIII;
    }
    public void setClIII(boolean clIII) {
        this.clIII = clIII;
    }
    public boolean isClIV() {
        return clIV;
    }
    public void setClIV(boolean clIV) {
        this.clIV = clIV;
    }
    public boolean isDayStudents() {
        return dayStudents;
    }
    public void setDayStudents(boolean dayStudents) {
        this.dayStudents = dayStudents;
    }

    public SaaEvent() {
        eventTitle = "SaaEvent Name";
        eventDescription = "SaaEvent Description";
    }

    public SaaEvent(JSONObject jobj) {
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeParser = new SimpleDateFormat("HH:mm:ss");
        try {
            if (!jobj.isNull("id")) {
                numericalID = Integer.parseInt(jobj.getString("id"));
            }
            if (!jobj.isNull("votes")) {
                votes = Integer.parseInt(jobj.getString("votes"));
            }
            if (!jobj.isNull("eventName")) {
                eventTitle = jobj.getString("eventName");
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

}
