package edu.milton.miltonmobileandroid.food.meals;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.util.JsonHttp;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;

public class MealsListAdapter extends ArrayAdapter<Object> {
    private final Context context;
    private ArrayList<?> Values;
    private static final String SUBMIT_VOTE_URL = "http://flik.ma1geek.org/vote.php";
    private static final String UPDATE_VOTE_URL = "http://flik.ma1geek.org/update.php";
    private ArrayList<Point> votesToSend = new ArrayList<>();
    private ArrayList<Point> votesToUpdate = new ArrayList<>();
    private String email;
    private final String LOG_TAG = this.getClass().toString();
    private HashMap<Integer, MealsVoteObject> myvotes;
    private String date;

    public void updateVotes() {
        RequestParams send = new RequestParams();
        RequestParams update = new RequestParams();
        for (Point p : votesToSend) {
            send.add("email", email);
            send.add("mealid", "" + p.x);
            send.add("vote", "" + p.y);
            send.add("date", date);
        }
        for (Point p : votesToUpdate) {
            update.add("email", email);
            update.add("mealid", "" + p.x);
            update.add("vote", "" + p.y);
            update.add("date", date);
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setUserAgent(JsonHttp.USER_AGENT);

        client.post(context,SUBMIT_VOTE_URL,send,new TextHttpResponseHandler(){

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });
        client.post(context,UPDATE_VOTE_URL,update,new TextHttpResponseHandler(){

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });
        votesToSend.clear();
        votesToUpdate.clear();
    }

    public MealsListAdapter(Context context, ArrayList<?> Values,String email, HashMap<Integer, MealsVoteObject> myvotes, String date) {
        super(context, R.layout.food_meals_foodview, (ArrayList<Object>) Values);
        this.context = context;
        this.Values = Values;
        this.email = email;
        this.myvotes = myvotes;
        this.date = date;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        Log.d(LOG_TAG, "getting the view for position " + pos);
        View rowView;

        final MealsMenuItem rowItem = (MealsMenuItem) Values.get(pos);

        if (!rowItem.isHeading()) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.food_meals_foodview, parent,
                    false);
            TextView textView = (TextView) rowView
                    .findViewById(R.id.food_text_view);
            final TextView vd = (TextView) rowView
                    .findViewById(R.id.ApacheTextView);
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

            if (rowItem.getItemName().equalsIgnoreCase("None Entered")) {
                //disable the good and bad button
                good.setEnabled(false);
                bad.setEnabled(false);

            }
            else {
                good.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        // if i havent voted up already
                        if (myvotes.get(rowItem.getNumericalID()) != null) {
                            if (myvotes.get(rowItem.getNumericalID()).getVote() < 0) {
                                // remove old vote

                                MealsVoteObject temp = myvotes.get(rowItem
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
                                vd.setText(dislikes + "");
                                updateVotes();
                            }
                        } else {

                            votesToSend.add(new Point(rowItem.getNumericalID(),
                                    1));
                            MealsVoteObject temp = new MealsVoteObject(rowItem.getNumericalID(), email, 1, rowItem.getDateString());
                            myvotes.put(rowItem.getNumericalID(), temp);
                            good.setImageResource(R.drawable.ic_action_good_green);
                            bad.setImageResource(R.drawable.ic_action_bad);
                            int likes = Integer.parseInt(vu.getText()
                                    .toString());

                            likes++;

                            vu.setText(likes + "");
                            updateVotes();
                        }

                    }
                });
                bad.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        // if i havent voted up already
                        if (myvotes.get(rowItem.getNumericalID()) != null) {
                            if (myvotes.get(rowItem.getNumericalID()).getVote() > 0) {
                                // remove old vote

                                MealsVoteObject temp = myvotes.get(rowItem
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
                                vd.setText(dislikes + "");
                                updateVotes();
                            }
                        } else {

                            votesToSend.add(new Point(rowItem.getNumericalID(),
                                    -1));
                            MealsVoteObject temp = new MealsVoteObject(rowItem.getNumericalID(), email, -1, rowItem.getDateString());
                            myvotes.put(rowItem.getNumericalID(), temp);
                            bad.setImageResource(R.drawable.ic_action_bad_red);
                            good.setImageResource(R.drawable.ic_action_good);
                            int likes = Integer.parseInt(vd.getText()
                                    .toString());

                            likes--;
                            vd.setText(likes + "");
                            updateVotes();

                        }
                    }
                });
            }
        } else {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.food_meals_foodview, parent,
                    false);
            TextView textView = (TextView) rowView
                    .findViewById(R.id.food_text_view);
            textView.setText(rowItem.getItemName());
            textView.setTextSize(18);
            textView.getPaint().setAntiAlias(true);
            TextView vd = (TextView) rowView.findViewById(R.id.ApacheTextView);
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