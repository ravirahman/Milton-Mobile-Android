package edu.milton.miltonmobileandroid.events.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.util.JsonHttp;

public class ActivitiesFragment extends Fragment implements View.OnClickListener {
    int year;
    int month;
    int day;
    ActivitiesListAdapter adapter;
    TextView dateLabel;
    ArrayList<ActivitesEvent> eventsToShow = new ArrayList<ActivitesEvent>();
    private String LOG_TAG = ActivitiesFragment.class.getName();

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.events_activities_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Time now = new Time();
        now.setToNow();
        this.year = now.year;
        this.month = now.month +1;
        this.day = now.monthDay;

        if (savedInstanceState != null) {
            this.year = savedInstanceState.getInt("year", now.year);
            this.month = savedInstanceState.getInt("month", now.month+1);
            this.day = savedInstanceState.getInt("day", now.monthDay);
        }
        dateLabel = (TextView) getActivity().findViewById(R.id.eventsHelper);
        adapter = new ActivitiesListAdapter(getActivity(),eventsToShow);

        ListView view = (ListView) getActivity().findViewById(R.id.eventsList);

        view.setAdapter(adapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.notifyDataSetChanged();
                ActivitesEvent activitesEvent = eventsToShow.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Event Details");
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.events_activities_view_expanded_event, null);
                builder.setView(dialogView);

                TextView titleView = (TextView) dialogView.findViewById(R.id.events_saa_view_expanded_event_title_textview);
                titleView.setText(activitesEvent.getEventTitle());

                TextView descView = (TextView) dialogView.findViewById(R.id.events_saa_view_expanded_event_description_textview);
                descView.setText(activitesEvent.getEventDescription());

                TextView timeView = (TextView) dialogView.findViewById(R.id.events_saa_view_expanded_event_time_textview);


               //STRING DATE ADJUSTMENT
                String from = (activitesEvent.getEventBeginTime().getHours() % 12) + ":";
                if (activitesEvent.getEventBeginTime().getMinutes()<10) {
                    from += "0";
                }
                from += activitesEvent.getEventBeginTime().getMinutes();
                if (activitesEvent.getEventBeginTime().getHours()>12) {
                    from += " PM";
                }
                if (activitesEvent.getEventBeginTime().getHours()<=12) {
                    from += " AM";
                }

                //STRING DATE ADJUSTMENT
                String to = (activitesEvent.getEventEndTime().getHours() % 12) + ":";
                if (activitesEvent.getEventEndTime().getMinutes()<10) {
                    to += "0";
                }
                to += activitesEvent.getEventEndTime().getMinutes();
                if (activitesEvent.getEventEndTime().getHours()>12) {
                    to += " PM";
                }
                if (activitesEvent.getEventEndTime().getHours()<=12) {
                    to += " AM";
                }

                timeView.setText(from + " - " + to);

                TextView locView = (TextView) dialogView.findViewById(R.id.events_saa_view_expanded_event_location_textview);
                locView.setText(activitesEvent.getEventLocation());

                //builder.setView((View) findViewById(R.id.view_expanded_event_view));
                //now customize the view

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                /*
                TextView title = (TextView) dialog.findViewById(R.id.view_expanded_event_title_textview);
                title.setText(activitesEvent.getEventTitle());
                TextView desc = (TextView) dialog.findViewById(R.id.view_expanded_event_description_textview);
                title.setText(activitesEvent.getEventDescription());
                TextView loc = (TextView) dialog.findViewById(R.id.view_expanded_event_location_textview);
                title.setText(activitesEvent.getEventLocation());*/
                dialog.show();
                //display an alert dialog of the activitesEvent details with the expanded view
            }
        });
        loadEventsForDay(year,month,day,false);

        // Gesture detection
        gestureDetector = new GestureDetector(getActivity(), new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        // Gesture detection
        gestureDetector = new GestureDetector(getActivity(), new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        getActivity().findViewById(R.id.events_saa_fragment).setOnClickListener(ActivitiesFragment.this);
        getActivity().findViewById(R.id.events_saa_fragment).setOnTouchListener(gestureListener);

    }

    @Override
    public void onClick(View v) {

    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) { //Left Swipe, go forward
                    switch(month) {
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                        case 8:
                        case 10:
                        case 12: //31 day months
                            if (day < 31) {
                                loadEventsForDay(year,month,day+1,false);
                                break;
                            }
                            //next month
                            if (month < 12) {
                                loadEventsForDay(year,month+1,1,false); //first day of next month
                                break;
                            }
                            //next year
                            loadEventsForDay(year+1,1,1,false); //first day of next month
                            break;
                        case 4:
                        case 6:
                        case 9:
                        case 11: //30 day months
                            if (day < 30) {
                                loadEventsForDay(year,month,day+1,false);
                                break;
                            }
                            //next month
                            if (month < 12) {
                                loadEventsForDay(year,month+1,1,false); //first day of next month
                                break;
                            }
                            //next year
                            loadEventsForDay(year+1,1,1,false); //first day of next month
                            break;
                        case 2:
                            //it's february :(
                            //determine if leap year
                            if (year % 400 == 0) {
                                if (day < 29) {
                                    loadEventsForDay(year,month,day+1,false);
                                    break;
                                }
                                loadEventsForDay(year,month+1,1,false); //first day of next month
                                break;
                            }
                            if (year % 100 == 0 || year % 4 > 0) {
                                //not a leap year. 28 days in month
                                if (day < 28) {
                                    loadEventsForDay(year,month,day+1,false);
                                    break;
                                }
                                loadEventsForDay(year,month+1,1,false); //first day of next month
                                break;
                            }
                            if (year % 4 == 0) {
                                //a leap year. 29 days in month
                                if (day < 29) {
                                    loadEventsForDay(year,month,day+1,false);
                                    break;
                                }
                                loadEventsForDay(year,month+1,1,false); //first day of next month
                                break;
                            }
                    }
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {//Right swipe. going backwards
                    if (day > 1) {
                        loadEventsForDay(year,month,day-1,false);
                        return false;
                    }
                    //ok first day of month and we're swiping. figure out the next day
                    switch(month -1) { //we want to know the month that we're going in to
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                        case 8:
                        case 10:
                        case 0: //31 day months
                            if (month -1 > 0) {
                                loadEventsForDay(year,month-1,31,false); //last day of previous month
                                break;
                            }
                            loadEventsForDay(year-1,12,31,false); //last day of previous year
                            break;
                        case 4:
                        case 6:
                        case 9:
                        case 11: //30 day months
                            loadEventsForDay(year,month-1,30,false); //last day of previous month
                            break;
                        case 2:
                            //it's february :(
                            //determine if leap year
                            if (year % 400 == 0) { //29 day feb
                                loadEventsForDay(year,month-1,29,false); //last day of previous month
                                break;
                            }
                            if (year % 100 == 0 || year % 4 > 0) {
                                //not a leap year. 28 days in month
                                loadEventsForDay(year,month-1,28,false); //last day of previous month
                                break;
                            }
                            if (year % 4 == 0) {
                                //a leap year. 29 days in month
                                loadEventsForDay(year,month-1,29,false); //last day of previous month
                                break;
                            }
                    }
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    private void loadEventsForDay(int year, int month, int day, boolean forcerefresh) {
        this.year = year;
        this.month = month;
        this.day = day;
        eventsToShow.clear();
        dateLabel.setText("Date: " + month + "/" + day + "/" + year);
        adapter.notifyDataSetChanged();
        //the logic:
        //1. Get the all events calendar feed for the day; load from cache first if possible.

        /*File cachedir = this.getCacheDir();
        String eventscache = cachedir.getPath() + "/eventcache";
        File eventcachedir = new File(eventscache);
        if (!eventcachedir.exists()) {
            eventcachedir.mkdir();
        }
        //now go and
        final File thisicalfeed = new File(eventscache + "/" + year + month + day + ".ics");
        if (!forcerefresh) {
            //check the cache first
            if (thisicalfeed.exists()) {
                //make sure it is within 24 hours
                if (thisicalfeed.lastModified() >= System.currentTimeMillis() - 1000 * 60 * 60 * 24) {
                    populateViewFromIcal(thisicalfeed);
                    return;
                }
            }
        }*/
        if (year < 100) {
            year += 2000;
        }
        String yearS = Integer.toString(year);
        String monthS = Integer.toString(month);
        if (monthS.length() < 2) {
            monthS = "0" + "" + month;
        }
        String dayS = Integer.toString(day);
        if (dayS.length() < 2) {
            dayS = "0" + "" + day;
        }
        Uri.Builder builder = new Uri.Builder()
                .scheme("http")
                .authority("saa.ma1geek.org")
                .appendPath("getActivities.php")
                .appendQueryParameter("date", yearS + "-" + monthS + "-" + dayS + "");
        String myUrl = builder.build().toString();
        JsonHttp.request(myUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                JSONArray activities;
                try {
                    activities = response.getJSONArray("Activities");
                    for (int i = 0; i < activities.length(); i++) {
                        JSONObject c = new JSONObject();
                        if (activities.getJSONObject(i).equals(null)) {
                            c = null;
                        } else {
                            c = activities.getJSONObject(i);

                        }
                        if (!c.equals(null)) {
                            eventsToShow.add(new ActivitesEvent(c));
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                }
            }

        });
    }
//todo add buttons to action bar
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.events_activities_menu, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_select_date:
                DatePickerDialog dialog = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear++; // range is 0-11
                        ActivitiesFragment.this.year = year;
                        ActivitiesFragment.this.month = monthOfYear;
                        ActivitiesFragment.this.day = dayOfMonth;

                        loadEventsForDay(year,monthOfYear,dayOfMonth,false);

                        //refresh with certain day
                    }
                },year,month-1,day);
                dialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    /*private void showFeedSelectionDialog() {

        CharSequence[] feedLabels = new CharSequence[saaEventFeeds.size()];
        ArrayList<Integer> feedids = new ArrayList<Integer>();
        for (SchoolEventFeed feed : saaEventFeeds) {
            feedLabels[saaEventFeeds.indexOf(feed)] = feed.name;
            feedids.add(feed.id);
        }

        //now get from preferences what is checked
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String checkedfeedsS = preferences.getString("checkedEventFeeds","[]");
        boolean[] checkedFeeds = new boolean[saaEventFeeds.size()];
        for (int i = 0; i < checkedFeeds.length; i++) {
            checkedFeeds[i] = false;
        }

        try {
            JSONArray jsonArray = new JSONArray(checkedfeedsS);
            for (int i = 0; i < jsonArray.length(); i++) {
                checkedFeeds[feedids.indexOf(jsonArray.getInt(i))] = true;
            }


        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setMultiChoiceItems(feedLabels,checkedFeeds, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                //look up the id of which from the order
                SchoolEventFeed feed = saaEventFeeds.get(which);
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ActivitiesFragment.this);
                String checkedfeedsS = preferences.getString("checkedEventFeeds","[]");

                try {
                    JSONArray jsonArray = new JSONArray(checkedfeedsS);
                    if (isChecked) {
                        //add to array of not exists
                        boolean exists = false;
                        while (exists == false) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (jsonArray.getInt(i) == feed.id) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (exists == false) {
                                jsonArray.put(feed.id);
                            }
                        }
                    }
                    else {
                        //remove from array if exists
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (jsonArray.getInt(i) == feed.id) {
                                jsonArray.remove(i);
                            }
                        }
                    }
                    preferences.edit().putString("checkedEventFeeds",jsonArray.toString()).apply();

                }
                catch(Exception e) {

                }
                //get the json from preferences
                //add/remove this feed
                //save the preferences
                //now save in preferences what was checked
            }
        });
        builder.create().show();
    }*/

}
