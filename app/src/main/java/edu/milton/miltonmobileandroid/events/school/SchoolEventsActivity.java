package edu.milton.miltonmobileandroid.events.school;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import edu.milton.miltonmobileandroid.R;

public class SchoolEventsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.events_school_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_school_events, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

        /*builder.scheme("http")
                .authority("trumba.com")
                .appendPath("calendars")
                .appendPath("milton-academy.ics")
                .appendQueryParameter("days", "1")
                .appendQueryParameter("startdate", year + month + day + "");
        String myUrl = builder.build().toString();
        client.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36");

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
            ArrayList<SchoolEventFeed> selectedFeeds = new ArrayList<SchoolEventFeed>();
            ArrayList<Integer> feedids = new ArrayList<Integer>(); //convert feed ids and orders visa versa
            for (SchoolEventFeed feed : eventFeeds) {
                feedids.add(feed.id);
            }
            ArrayList<SchoolEventFeed> feedsToBeFetched = new ArrayList<SchoolEventFeed>();
            for (SchoolEventFeed feed : selectedFeeds) {
                feedsToBeFetched.add(feed);
            }

            for (VEvent vEvent : vEvents) {
                Log.v(LOG_TAG,"SaaEvent: " + vEvent.getSummary().getValue());
                //Log.v(LOG_TAG,vEvent.getExperimentalComponents().)
                //Log the components
                //Log the properties
                //vEvent.getExperimentalComponent("X-TRUMBA-CUSTOMFIELD").getExperimentalProperty("TYPE").getValue();
            }


            //2. Parse the list
            //3. Create vEvents; add to the arraylist in the CORRECT ORDER;
            //4. If necessarry, force a refres

            //first see what feeds are checked




            for (SchoolEventFeed feed : selectedFeeds) {


                //ok, now you have the feeds. Parse it and update the listview.
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class SchoolEventsFragment extends Fragment {

        public SchoolEventsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.events_school_fragment, container, false);
            return rootView;
        }
    }
}
