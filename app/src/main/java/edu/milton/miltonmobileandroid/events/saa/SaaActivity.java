package edu.milton.miltonmobileandroid.events.saa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.util.JsonHttp;

public class SaaActivity extends Activity {
    int year;
    int month;
    int day;
    SaaListAdapter adapter;
    TextView dateLabel;
    ArrayList<SaaEvent> eventsToShow = new ArrayList<SaaEvent>();
    private String LOG_TAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOG_TAG = this.getLocalClassName();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_saa_fragment);
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
                SaaEvent saaEvent = eventsToShow.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(SaaActivity.this);
                builder.setTitle("Event Details");
                LayoutInflater inflater = SaaActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.events_saa_view_expanded_event, null);
                builder.setView(dialogView);

                TextView titleView = (TextView) dialogView.findViewById(R.id.events_saa_view_expanded_event_title_textview);
                titleView.setText(saaEvent.getEventTitle());

                TextView descView = (TextView) dialogView.findViewById(R.id.events_saa_view_expanded_event_description_textview);
                descView.setText(saaEvent.getEventDescription());

                TextView locView = (TextView) dialogView.findViewById(R.id.events_saa_view_expanded_event_location_textview);
                locView.setText(saaEvent.getEventLocation());

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
                title.setText(saaEvent.getEventTitle());
                TextView desc = (TextView) dialog.findViewById(R.id.view_expanded_event_description_textview);
                title.setText(saaEvent.getEventDescription());
                TextView loc = (TextView) dialog.findViewById(R.id.view_expanded_event_location_textview);
                title.setText(saaEvent.getEventLocation());*/
                dialog.show();
                //display an alert dialog of the saaEvent details with the expanded view
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
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("saa.ma1geek.org")
                .appendPath("getActivities.php")
                .appendQueryParameter("date", yearS + "-" + monthS + "-" + dayS + "");
        String myUrl = builder.build().toString();
        JsonHttp.request(myUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
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
                            eventsToShow.add(new SaaEvent(c));
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                }
            }

        });
    }

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
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_select_date:
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
                break;
            case R.id.action_filter_events:
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
    public static class SaaFragment extends Fragment {

        public SaaFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.events_saa_fragment, container, false);
            return rootView;
        }
    }

}
