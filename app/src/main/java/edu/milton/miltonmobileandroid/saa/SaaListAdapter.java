package edu.milton.miltonmobileandroid.saa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.milton.miltonmobileandroid.R;

public class SaaListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Event> events;

    public SaaListAdapter(Activity activity, ArrayList<Event> events) {
        this.activity = activity;
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int location) {
        return events.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.view_compact_event, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.view_compact_event_title_textview);
        TextView location = (TextView) convertView.findViewById(R.id.view_compact_event_location_textview);

        // getting movie data for the row
        final Event m = events.get(position);


        // title
        title.setText(m.getEventTitle());

        // rating
        location.setText("Location: " + String.valueOf(m.getEventLocation()));

        return convertView;
    }



}
