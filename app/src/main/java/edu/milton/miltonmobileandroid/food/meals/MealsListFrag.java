package edu.milton.miltonmobileandroid.food.meals;
import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.settings.account.AccountMethods;

@SuppressLint({ "SimpleDateFormat", "ValidFragment" })
public class MealsListFrag extends ListFragment implements
        LoaderCallbacks<Cursor> {

    private static final String READ_MEALS_URL = "http://flik.ma1geek.org/getMeals.php";
    private static final String READ_VOTES_URL = "http://flik.ma1geek.org/getvotes.php";

    private final String LOG_TAG = this.getClass().toString();

    private ArrayList<MealsMenuItem> Foods = new ArrayList<>();
    private ArrayList<MealsVoteObject> votes;
    private HashMap<Integer, MealsVoteObject> myvotes;     // private ArrayList<Integer> voteState; point -> x stores mealid, y stores vote

    private String date;
    private int dateShift;
    private Context context;
    private String email;

    public MealsListFrag(int position, Context context) {
        dateShift = position;
        this.context = context;
        email = AccountMethods.getFirstName(context) + "_" + AccountMethods.getLastName(context) + "00@milton.edu";
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
        final RequestParams params = new RequestParams();
        params.add("date",date);
        params.add("version","2"); //using the second version of the api
        Log.d("date", date);
        // date = "2013-11-23";
        // use to demonstrate if there are no items for current date
        AsyncHttpClient client = new AsyncHttpClient();
        client.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36");

        client.get(context, READ_MEALS_URL,params,new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(LOG_TAG,"The response is: " + responseString);
            }

            public void onSuccess(int statusCode, Header[] headers, final JSONObject jsonEntrees) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        votes = new ArrayList<>();
                        myvotes = new HashMap<>();
                        // get food
                        try {
                            Iterator<String> itr = jsonEntrees.keys();
                            while(itr.hasNext()) {
                                String mealtimename = itr.next();
                                JSONObject mealTime = jsonEntrees.getJSONObject(mealtimename);
                                Foods.add(new MealsMenuItem(true, mealtimename));
                                Iterator<String> itr2 = mealTime.keys();
                                while(itr2.hasNext()) {
                                    String mealtypename = itr2.next();
                                    Foods.add(new MealsMenuItem(true, mealtypename));
                                    JSONArray meals = mealTime.getJSONArray(mealtypename);
                                    for (int i = 0; i < meals.length(); i++) {
                                        JSONObject c = meals.getJSONObject(i);
                                        Foods.add(new MealsMenuItem(false, c));
                                    }
                                }
                            }
                            Log.v(LOG_TAG, "Foods is this big: " + Foods.size());
                            AsyncHttpClient client2 = new AsyncHttpClient();
                            client2.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36");

                            client2.get(context, READ_VOTES_URL, params, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, final JSONObject jsonVotes) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                //Log.d("test2", jsonVotes.toString());
                                                JSONArray retrievedVotes = jsonVotes.getJSONArray("Votes");
                                                for (int i = 0; i < retrievedVotes.length(); i++) {
                                                    JSONObject j = retrievedVotes.getJSONObject(i);
                                                    MealsVoteObject vobj = new MealsVoteObject(j);
                                                    votes.add(vobj);

                                                    if (vobj.getEmail().equalsIgnoreCase(email)) {
                                                        myvotes.put(vobj.getMealID(), vobj);
                                                    }
                                                    for (MealsMenuItem food : Foods) {
                                                        if (vobj.getMealID() == food.getNumericalID()) {
                                                            food.setVotes(food.getVotes() + vobj.getVote());
                                                            if (vobj.getVote() >= 1) {
                                                                food.setUpvotes(food.getUpvotes() + vobj.getVote());
                                                            }
                                                            if (vobj.getVote() <= -1) {
                                                                food.setDownvotes(food.getDownvotes() + vobj.getVote());
                                                            }

                                                        }

                                                    }

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }
                            });
                            Log.d(LOG_TAG,"setting the adapter");
                            MealsListAdapter adapter = new MealsListAdapter(getActivity(), Foods,email,myvotes,date);
                            setListAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }});
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // do something with the data

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.food_meals_listfrag, null);
        return view;
    }
}