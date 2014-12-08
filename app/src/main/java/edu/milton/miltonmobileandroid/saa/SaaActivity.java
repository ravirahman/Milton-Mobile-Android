package edu.milton.miltonmobileandroid.saa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.util.JSONParser;

public class SaaActivity extends Activity {
    ArrayList<EventFeed> eventFeeds = new ArrayList<EventFeed>();
    AsyncHttpClient client = new AsyncHttpClient();
    int year;
    int month;
    int day;
    SaaListAdapter adapter;
    TextView dateLabel;
    ArrayList<Event> eventsToShow = new ArrayList<Event>();
    private String LOG_TAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOG_TAG = this.getLocalClassName();
        //hard-coding in the possible calendar feeds. Ideally, this list would be pulled from the server
        eventFeeds.add(new EventFeed(123913,"SAA","Upper School Student Activities"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_events);
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
        dateLabel = (TextView) findViewById(R.id.eventsHelper);
        adapter = new SaaListAdapter(this,eventsToShow);

        ListView view = (ListView) findViewById(R.id.eventsList);

        view.setAdapter(adapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.notifyDataSetChanged();
                Event event = eventsToShow.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(SaaActivity.this);
                builder.setTitle("Event Details");
                LayoutInflater inflater = SaaActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.view_expanded_event, null);
                builder.setView(dialogView);

                TextView titleView = (TextView) dialogView.findViewById(R.id.view_expanded_event_title_textview);
                titleView.setText(event.getEventTitle());

                TextView descView = (TextView) dialogView.findViewById(R.id.view_expanded_event_description_textview);
                descView.setText(event.getEventDescription());

                TextView locView = (TextView) dialogView.findViewById(R.id.view_expanded_event_location_textview);
                locView.setText(event.getEventTitle());

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
                title.setText(event.getEventTitle());
                TextView desc = (TextView) dialog.findViewById(R.id.view_expanded_event_description_textview);
                title.setText(event.getEventDescription());
                TextView loc = (TextView) dialog.findViewById(R.id.view_expanded_event_location_textview);
                title.setText(event.getEventLocation());*/
                dialog.show();
                //display an alert dialog of the event details with the expanded view
            }
        });
        loadEventsForDay(year,month,day,false);


    }
    private void loadEventsForDay(int year, int month, int day, boolean forcerefresh) {
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
            year+=2000;
        }
        String hello;
        String yearS = Integer.toString(year);
        String monthS = Integer.toString(month);
        if (monthS.length() < 2) {
            monthS = "0" + "" + month;
        }
        String dayS = Integer.toString(day);
        if (dayS.length() < 2) {
            dayS = "0" + "" + day;
        }
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("saa.ma1geek.org")
                .appendPath("getActivities.php")
                .appendQueryParameter("date", yearS + "-" + monthS + "-"  + dayS + "");
        String myUrl = builder.build().toString();
        Log.v(LOG_TAG,"my url is: " + myUrl);
        client.get(this,myUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.v(LOG_TAG,"my response is " + response);
                try {
                    JSONObject jresponse = new JSONObject(response);
                    //{
                    JSONArray activities = jresponse.getJSONArray("Activities");
                    for (int i = 0; i < activities.length(); i++) {
                        JSONObject c = new JSONObject();
                        if (activities.getJSONObject(i).equals(null)) {
                            c = null;
                            Log.d("check null here", "null = true");
                        } else {
                            c = activities.getJSONObject(i);

                        }
                        if (!c.equals(null)) {
                            Log.v(LOG_TAG,c.toString());
                            eventsToShow.add(new Event(c));
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                catch (JSONException e) {

                }
            }
        });
        /*builder.scheme("http")
                .authority("trumba.com")
                .appendPath("calendars")
                .appendPath("milton-academy.ics")
                .appendQueryParameter("days", "1")
                .appendQueryParameter("startdate", year + month + day + "");
        String myUrl = builder.build().toString();
        client.get(this, myUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] s) {
                //save the ical feed to the cache dir
                try {
                    if (!thisicalfeed.exists()) {
                        thisicalfeed.createNewFile();
                    }
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(thisicalfeed));
                    bos.write(s);
                    bos.flush();
                    bos.close();
                    populateViewFromIcal(thisicalfeed);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });*/


    }
    /*
    private void populateViewFromIcal(File icalfile) {
        try {
            ICalendar calendar = Biweekly.parse(icalfile).first();
            ArrayList<VEvent> vEvents = (ArrayList<VEvent>) calendar.getEvents();
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String checkedfeedsS = preferences.getString("checkedEventFeeds", "[]");
            if (checkedfeedsS.equals("[]")) {
                showFeedSelectionDialog();
                return;
            }

            JSONArray checkedFeeds = new JSONArray(checkedfeedsS);
            //this array contains the id's of the feeds that are checked
            ArrayList<EventFeed> selectedFeeds = new ArrayList<EventFeed>();
            ArrayList<Integer> feedids = new ArrayList<Integer>(); //convert feed ids and orders visa versa
            for (EventFeed feed : eventFeeds) {
                feedids.add(feed.id);
            }
            ArrayList<EventFeed> feedsToBeFetched = new ArrayList<EventFeed>();
            for (EventFeed feed : selectedFeeds) {
                feedsToBeFetched.add(feed);
            }

            for (VEvent vEvent : vEvents) {
                Log.v(LOG_TAG,"Event: " + vEvent.getSummary().getValue());
                //Log.v(LOG_TAG,vEvent.getExperimentalComponents().)
                //Log the components
                //Log the properties
                //vEvent.getExperimentalComponent("X-TRUMBA-CUSTOMFIELD").getExperimentalProperty("TYPE").getValue();
            }


            //2. Parse the list
            //3. Create vEvents; add to the arraylist in the CORRECT ORDER;
            //4. If necessarry, force a refres

            //first see what feeds are checked




            for (EventFeed feed : selectedFeeds) {


                //ok, now you have the feeds. Parse it and update the listview.
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_select_date) {
            DatePickerDialog dialog = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    monthOfYear++; // range is 0-11
                    SaaActivity.this.year = year;
                    SaaActivity.this.month = monthOfYear;
                    SaaActivity.this.day = dayOfMonth;

                    loadEventsForDay(year,monthOfYear,dayOfMonth,false);

                    //refresh with certain day
                }
            },year,month-1,day);
            dialog.show();

        }

        if (id == R.id.action_filter_events) {
            //showFeedSelectionDialog();
            new AlertDialog.Builder(this)
                    .setTitle("Not Implimented")
                    .setMessage("Will be in future update")
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

        }

        return super.onOptionsItemSelected(item);
    }
    /*private void showFeedSelectionDialog() {

        CharSequence[] feedLabels = new CharSequence[eventFeeds.size()];
        ArrayList<Integer> feedids = new ArrayList<Integer>();
        for (EventFeed feed : eventFeeds) {
            feedLabels[eventFeeds.indexOf(feed)] = feed.name;
            feedids.add(feed.id);
        }

        //now get from preferences what is checked
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String checkedfeedsS = preferences.getString("checkedEventFeeds","[]");
        boolean[] checkedFeeds = new boolean[eventFeeds.size()];
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
                EventFeed feed = eventFeeds.get(which);
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SaaActivity.this);
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
    public static class EventsFragment extends Fragment {

        public EventsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_events, container, false);
            return rootView;
        }
    }

}
