package edu.milton.miltonmobileandroid.flik;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.flik.MenuItem;
import edu.milton.miltonmobileandroid.flik.VoteObject;
import edu.milton.miltonmobileandroid.util.JSONParser;

@SuppressLint({ "SimpleDateFormat", "ValidFragment" })
public class FlikListFrag extends ListFragment implements
        LoaderCallbacks<Cursor> {

    private ProgressDialog pDialog;
    private static final String READ_EVENTS_URL = "http://flik.ma1geek.org/getMeals.php";
    private static final String READ_VOTES_URL = "http://flik.ma1geek.org/getvotes.php";
    private static final String SUBMIT_VOTE_URL = "http://flik.ma1geek.org/vote.php";
    private static final String UPDATE_VOTE_URL = "http://flik.ma1geek.org/update.php";
    private JSONArray retrievedEntrees = null;
    private JSONArray retrievedDesserts = null;
    private String email;
    private JSONArray retrievedFlikLive = null;
    private JSONArray retrievedSides = null;
    private JSONArray retrievedSoups = null;
    private JSONArray retrievedVotes = null;
    private ArrayList<MenuItem> Foods;
    private ArrayList<VoteObject> votes;
    // private ArrayList<Integer> voteState;
    // point -> x stores mealid, y stores vote
    private ArrayList<Point> votesToSend;
    private ArrayList<Point> votesToUpdate;
    private HashMap<Integer, VoteObject> myvotes;
    private String date;
    private int dateShift;

    // private String type = "Entree";

    @SuppressLint("ValidFragment")
    public FlikListFrag(int position, String email) {
        // TODO Auto-generated constructor stub
        dateShift = position;
        this.email = email;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, dateShift);
        // System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        date = formattedDate;
        Log.d("date", date);
        // date = "2013-11-23";
        // use to demonstrate if there are no items for current date
        new LoadMeals().execute();
        Log.d("FlikListFrag", "fragment created");
    }

    public void updateJSONdata() {

        Foods = new ArrayList<MenuItem>();
        votes = new ArrayList<VoteObject>();
        votesToSend = new ArrayList<Point>();
        myvotes = new HashMap<Integer, VoteObject>();
        votesToUpdate = new ArrayList<Point>();

        //Lunch
        // get Entrees
        try {

            JSONParser jParserEntrees = new JSONParser();
            JSONObject jsonEntrees = jParserEntrees
                    .getJSONFromUrl(READ_EVENTS_URL + "?type=Entree&date="
                            + date+"&time=Lunch");

            retrievedEntrees = jsonEntrees.getJSONArray("Entree");
            Foods.add(new MenuItem(true, "Lunch"));
            Foods.add(new MenuItem(true, "Entrees"));
            for (int i = 0; i < retrievedEntrees.length(); i++) {
                JSONObject c = retrievedEntrees.getJSONObject(i);
                // System.out.println(c);
                Foods.add(new MenuItem(false, c));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get Sides
        try {
            JSONParser jParserSides = new JSONParser();
            JSONObject jsonSides = jParserSides.getJSONFromUrl(READ_EVENTS_URL
                    + "?type=Side&date=" + date+"&time=Lunch");

            retrievedSides = jsonSides.getJSONArray("Side");
            Foods.add(new MenuItem(true, "Sides"));
            for (int i = 0; i < retrievedSides.length(); i++) {
                JSONObject c = retrievedSides.getJSONObject(i);
                // System.out.println(c);
                Foods.add(new MenuItem(false, c));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // get Flik Live
        try {
            JSONParser jParserFlikLive = new JSONParser();
            JSONObject jsonFlikLive = jParserFlikLive
                    .getJSONFromUrl(READ_EVENTS_URL + "?type=Flik+Live&date="
                            + date+"&time=Lunch");


            retrievedFlikLive = jsonFlikLive.getJSONArray("Flik Live");
            Foods.add(new MenuItem(true, "Flik Live"));
            for (int i = 0; i < retrievedFlikLive.length(); i++) {
                JSONObject c = retrievedFlikLive.getJSONObject(i);
                // System.out.println(c);
                Foods.add(new MenuItem(false, c));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // get Dessert
        try {
            JSONParser jParserDessert = new JSONParser();
            JSONObject jsonDessert = jParserDessert
                    .getJSONFromUrl(READ_EVENTS_URL + "?type=Dessert&date="
                            + date+"&time=Lunch");


            retrievedDesserts = jsonDessert.getJSONArray("Dessert");
            Foods.add(new MenuItem(true, "Desserts"));
            for (int i = 0; i < retrievedDesserts.length(); i++) {
                JSONObject c = retrievedDesserts.getJSONObject(i);
                // System.out.println(c);
                Foods.add(new MenuItem(false, c));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // get Soup
        try {
            JSONParser jParserSoups = new JSONParser();
            JSONObject jsonSoups = jParserSoups.getJSONFromUrl(READ_EVENTS_URL
                    + "?type=Soup&date=" + date+"&time=Lunch");


            retrievedSoups = jsonSoups.getJSONArray("Soup");
            Foods.add(new MenuItem(true, "Soups"));
            for (int i = 0; i < retrievedSoups.length(); i++) {
                JSONObject c = retrievedSoups.getJSONObject(i);
                // System.out.println(c);
                Foods.add(new MenuItem(false, c));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Dinner
        // get Entrees
        try {

            JSONParser jParserEntrees = new JSONParser();
            JSONObject jsonEntrees = jParserEntrees
                    .getJSONFromUrl(READ_EVENTS_URL + "?type=Entree&date="
                            + date+"&time=Dinner");

            retrievedEntrees = jsonEntrees.getJSONArray("Entree");
            Foods.add(new MenuItem(true, "Dinner"));
            Foods.add(new MenuItem(true, "Entrees"));
            for (int i = 0; i < retrievedEntrees.length(); i++) {
                JSONObject c = retrievedEntrees.getJSONObject(i);
                // System.out.println(c);
                Foods.add(new MenuItem(false, c));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get Sides
        try {
            JSONParser jParserSides = new JSONParser();
            JSONObject jsonSides = jParserSides.getJSONFromUrl(READ_EVENTS_URL
                    + "?type=Side&date=" + date+"&time=Dinner");

            retrievedSides = jsonSides.getJSONArray("Side");
            Foods.add(new MenuItem(true, "Sides"));
            for (int i = 0; i < retrievedSides.length(); i++) {
                JSONObject c = retrievedSides.getJSONObject(i);
                // System.out.println(c);
                Foods.add(new MenuItem(false, c));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // get Flik Live
        try {
            JSONParser jParserFlikLive = new JSONParser();
            JSONObject jsonFlikLive = jParserFlikLive
                    .getJSONFromUrl(READ_EVENTS_URL + "?type=Flik+Live&date="
                            + date+"&time=Dinner");


            retrievedFlikLive = jsonFlikLive.getJSONArray("Flik Live");
            Foods.add(new MenuItem(true, "Flik Live"));
            for (int i = 0; i < retrievedFlikLive.length(); i++) {
                JSONObject c = retrievedFlikLive.getJSONObject(i);
                // System.out.println(c);
                Foods.add(new MenuItem(false, c));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // get Dessert
        try {
            JSONParser jParserDessert = new JSONParser();
            JSONObject jsonDessert = jParserDessert
                    .getJSONFromUrl(READ_EVENTS_URL + "?type=Dessert&date="
                            + date+"&time=Dinner");


            retrievedDesserts = jsonDessert.getJSONArray("Dessert");
            Foods.add(new MenuItem(true, "Desserts"));
            for (int i = 0; i < retrievedDesserts.length(); i++) {
                JSONObject c = retrievedDesserts.getJSONObject(i);
                // System.out.println(c);
                Foods.add(new MenuItem(false, c));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // get Soup
        try {
            JSONParser jParserSoups = new JSONParser();
            JSONObject jsonSoups = jParserSoups.getJSONFromUrl(READ_EVENTS_URL
                    + "?type=Soup&date=" + date+"&time=Dinner");


            retrievedSoups = jsonSoups.getJSONArray("Soup");
            Foods.add(new MenuItem(true, "Soups"));
            for (int i = 0; i < retrievedSoups.length(); i++) {
                JSONObject c = retrievedSoups.getJSONObject(i);
                // System.out.println(c);
                Foods.add(new MenuItem(false, c));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        // get all votes
        try {
            JSONParser jParserVotes = new JSONParser();
            JSONObject jsonVotes = jParserVotes.getJSONFromUrl(READ_VOTES_URL
                    + "?date=" + date);
            //Log.d("test2", jsonVotes.toString());
            retrievedVotes = jsonVotes.getJSONArray("Votes");
            for (int i = 0; i < retrievedVotes.length(); i++) {
                JSONObject j = retrievedVotes.getJSONObject(i);
                VoteObject vobj = new VoteObject(j);
                votes.add(vobj);
                if (vobj.getEmail().equalsIgnoreCase(email)) {
                    myvotes.put(vobj.getMealID(), vobj);
                }
                for (MenuItem food : Foods) {
                    if (vobj.getMealID() == food.getNumericalID()) {
                        food.setVotes(food.getVotes() + vobj.getVote());
                        if(vobj.getVote()>=1){
                            food.setUpvotes(food.getUpvotes()+vobj.getVote());
                        }
                        if(vobj.getVote()<=-1){
                            food.setDownvotes(food.getDownvotes()+vobj.getVote());
                        }

                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class LoadMeals extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Meals...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            // we will develop this method in version 2
            Log.d("LoadEvents", "attempting to load meals");
            try {
                updateJSONdata();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            updateEventList();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // do something with the data

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flik, null);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub

    }

    public void updateEventList() {
        FlikArrayAdapter adapter = new FlikArrayAdapter(getActivity(), Foods);
        setListAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //new Vote().execute();

    }

    public void updateVotes() {
        List<NameValuePair> send = new ArrayList<NameValuePair>();
        List<NameValuePair> update = new ArrayList<NameValuePair>();
        JSONParser parser = new JSONParser();
        for (Point p : votesToSend) {
            send.add(new BasicNameValuePair("email", email));
            send.add(new BasicNameValuePair("mealid", "" + p.x));
            send.add(new BasicNameValuePair("vote", "" + p.y));
            send.add(new BasicNameValuePair("date", date));
        }
        for (Point p : votesToUpdate) {
            update.add(new BasicNameValuePair("email", email));
            update.add(new BasicNameValuePair("mealid", "" + p.x));
            update.add(new BasicNameValuePair("vote", "" + p.y));
            update.add(new BasicNameValuePair("date", date));
        }
        parser.makeHttpRequest(SUBMIT_VOTE_URL, "POST", send);
        parser.makeHttpRequest(UPDATE_VOTE_URL, "POST", update);
        votesToSend.clear();
        votesToUpdate.clear();
    }

    public class Vote extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            // we will develop this method in version 2
            Log.d("LoadEvents", "updating votes");
            try {
                updateVotes();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            // updateEventList();
        }
    }

    class FlikArrayAdapter extends ArrayAdapter<Object> {
        private final Context context;
        private ArrayList<? extends Object> Values;
        int which;
        int currentlyExpandedItem = -1;

        @SuppressWarnings("unchecked")
        public FlikArrayAdapter(Context context,
                                ArrayList<? extends Object> Values) {
            super(context, R.layout.view_food_flik, (ArrayList<Object>) Values);
            this.context = context;
            this.Values = Values;

        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            final int position = pos;
            View rowView;

            final MenuItem rowItem = (MenuItem) Values.get(position);

            if (!rowItem.isHeading()) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.view_food_flik, parent,
                        false);
                TextView textView = (TextView) rowView
                        .findViewById(R.id.food_text_view);
                final TextView vd = (TextView) rowView
                        .findViewById(R.id.FlikDesc);
                vd.setTextIsSelectable(false);
                vd.getPaint().setAntiAlias(true);
                vd.setTextSize(8);
                vd.setText(rowItem.getDownvotes() + "");
                final TextView vu = (TextView) rowView
                        .findViewById(R.id.textView2);
                vu.setTextIsSelectable(false);
                vu.getPaint().setAntiAlias(true);
                vu.setTextSize(8);
                vu.setText(rowItem.getUpvotes() + "");
                final ImageButton good = (ImageButton) rowView
                        .findViewById(R.id.good_button);
                final ImageButton bad = (ImageButton) rowView
                        .findViewById(R.id.bad_button);
                textView.setText(rowItem.getItemName());
                textView.setTextSize(12);
                // textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setTextIsSelectable(false);
                textView.getPaint().setAntiAlias(true);
                rowView.getLayoutParams().height = 36;
                if (myvotes.get(rowItem.getNumericalID()) != null) {
                    if (myvotes.get(rowItem.getNumericalID()).getVote() > 0) {
                        good.setImageResource(R.drawable.ic_action_good_green);
                    }
                    if (myvotes.get(rowItem.getNumericalID()).getVote() < 0) {
                        bad.setImageResource(R.drawable.ic_action_bad_red);

                    }
                }

                good.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        // if i havent voted up already
                        if (myvotes.get(rowItem.getNumericalID()) != null) {
                            if (myvotes.get(rowItem.getNumericalID()).getVote() < 0) {
                                // remove old vote

                                VoteObject temp = myvotes.get(rowItem
                                        .getNumericalID());
                                temp.setVote(1);
                                myvotes.remove(rowItem.getNumericalID());
                                myvotes.put(rowItem.getNumericalID(), temp);
                                votesToUpdate.add(new Point(rowItem
                                        .getNumericalID(), 1));
                                good.setImageResource(R.drawable.ic_action_good_green);
                                bad.setImageResource(R.drawable.ic_action_bad);
                                int likes = Integer.parseInt(vu.getText()
                                        .toString());

                                likes++;

                                vu.setText(likes + "");

                                int dislikes = Integer.parseInt(vd.getText()
                                        .toString());
                                dislikes++;
                                vd.setText(dislikes+"");
                                new Vote().execute();

                            }
                        } else {

                            votesToSend.add(new Point(rowItem.getNumericalID(),
                                    1));
                            VoteObject temp = new VoteObject(rowItem.getNumericalID(),email,1,rowItem.getDateString());
                            myvotes.put(rowItem.getNumericalID(), temp);
                            good.setImageResource(R.drawable.ic_action_good_green);
                            bad.setImageResource(R.drawable.ic_action_bad);
                            int likes = Integer.parseInt(vu.getText()
                                    .toString());

                            likes++;

                            vu.setText(likes + "");
                            new Vote().execute();
                        }

                    }
                });
                bad.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        // if i havent voted up already
                        if (myvotes.get(rowItem.getNumericalID()) != null) {
                            if (myvotes.get(rowItem.getNumericalID()).getVote() > 0) {
                                // remove old vote

                                VoteObject temp = myvotes.get(rowItem
                                        .getNumericalID());
                                temp.setVote(-1);
                                myvotes.remove(rowItem.getNumericalID());
                                myvotes.put(rowItem.getNumericalID(), temp);
                                votesToUpdate.add(new Point(rowItem
                                        .getNumericalID(), -1));
                                bad.setImageResource(R.drawable.ic_action_bad_red);
                                good.setImageResource(R.drawable.ic_action_good);
                                int likes = Integer.parseInt(vu.getText()
                                        .toString());

                                likes--;

                                vu.setText(likes + "");

                                int dislikes = Integer.parseInt(vd.getText()
                                        .toString());
                                dislikes--;
                                vd.setText(dislikes+"");
                                new Vote().execute();
                            }
                        } else {

                            votesToSend.add(new Point(rowItem.getNumericalID(),
                                    -1));
                            VoteObject temp = new VoteObject(rowItem.getNumericalID(),email,-1,rowItem.getDateString());
                            myvotes.put(rowItem.getNumericalID(), temp);
                            bad.setImageResource(R.drawable.ic_action_bad_red);
                            good.setImageResource(R.drawable.ic_action_good);
                            int likes = Integer.parseInt(vd.getText()
                                    .toString());

                            likes--;
                            vd.setText(likes + "");
                            new Vote().execute();

                        }
                    }
                });
            } else {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.view_food_flik, parent,
                        false);
                TextView textView = (TextView) rowView
                        .findViewById(R.id.food_text_view);
                textView.setText(rowItem.getItemName());
                textView.setTextSize(18);
                textView.getPaint().setAntiAlias(true);
                TextView vd = (TextView) rowView.findViewById(R.id.FlikDesc);
                ImageButton good = (ImageButton) rowView
                        .findViewById(R.id.good_button);
                ImageButton bad = (ImageButton) rowView
                        .findViewById(R.id.bad_button);
                textView.setTextIsSelectable(false);
                // textView.setPadding(DPConverter.dpConvert(2),0,0,0);
                textView.setTypeface(Typeface.DEFAULT_BOLD);

                good.setVisibility(View.GONE);
                bad.setVisibility(View.GONE);
                vd.setVisibility(View.GONE);
            }

            // Change icon based on name

            return rowView;
        }
    }

}