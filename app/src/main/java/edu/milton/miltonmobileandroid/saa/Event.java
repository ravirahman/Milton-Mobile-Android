//Geoffrey Owens 2013. version 1.0.0. please don't change me.
package edu.milton.miltonmobileandroid.saa;

import java.sql.Date;
import java.sql.Time;

public class Event {
    private String eventTitle;		//name of the event
    private String eventDescription;//medium length description of the event
    private Date eventDate;			//date the event occurs on. can be obtained in YYYY-MM-DD format via toString()
    private Time eventBeginTime;	//time of beginning of event. toString() returns time in {t 'hh:mm:ss'} format.
    private Time eventEndTime;		//time of end of event
    private boolean boarders;		//whether the event applies to boarders
    private boolean clI;			//whether the event applies to class I students
    private boolean clII;			//whether the event applies to class II students
    private boolean clIII;			//etc
    private boolean clIV;			//etc
    private boolean dayStudents;	//etc
    private String eventCategory; 	//category the event falls under

    //full constructor
    public Event(String eventTitle, String eventDescription, Date eventDate, Time eventBeginTime, Time eventEndTime, boolean boarders, boolean clI, boolean clII, boolean clIII, boolean clIV, boolean dayStudents, String eventCategory) {
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
    public Event(String eventTitle, String eventDescription, Date eventDate, Time eventBeginTime, Time eventEndTime) {
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
    public Time getEventBeginTime() {
        return eventBeginTime;
    }
    public void setEventBeginTime(Time eventBeginTime) {
        this.eventBeginTime = eventBeginTime;
    }
    public Time getEventEndTime() {
        return eventEndTime;
    }
    public void setEventEndTime(Time eventEndTime) {
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


}
