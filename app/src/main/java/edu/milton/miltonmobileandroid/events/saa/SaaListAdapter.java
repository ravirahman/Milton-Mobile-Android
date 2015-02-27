package edu.milton.miltonmobileandroid.events.saa;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.milton.miltonmobileandroid.R;

public class SaaListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<SaaEvent> saaEvents;

    public SaaListAdapter(Activity activity, ArrayList<SaaEvent> saaEvents) {
        this.activity = activity;
        this.saaEvents = saaEvents;
    }

    @Override
    public int getCount() {
        return saaEvents.size();
    }

    @Override
    public Object getItem(int location) {
        return saaEvents.get(location);
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
            convertView = inflater.inflate(R.layout.events_saa_view_compact_event, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.events_saa_view_compact_event_title_textview);
        TextView location = (TextView) convertView.findViewById(R.id.events_saa_view_compact_event_location_textview);
        TextView time = (TextView) convertView.findViewById(R.id.events_saa_view_compact_event_time_textview);

        // getting movie data for the row
        final SaaEvent m = saaEvents.get(position);


        // title
        title.setText(m.getEventTitle());

        // rating
        location.setText("Location: " + String.valueOf(m.getEventLocation()));

        //STRING DATE ADJUSTMENT
        String from = (m.getEventBeginTime().getHours() % 12) + ":";
        if (m.getEventBeginTime().getMinutes()<10) {
            from += "0";
        }
        from += m.getEventBeginTime().getMinutes();
        if (m.getEventBeginTime().getHours()>12) {
            from += " PM";
        }
        if (m.getEventBeginTime().getHours()<=12) {
            from += " AM";
        }

        //STRING DATE ADJUSTMENT
        String to = (m.getEventEndTime().getHours() % 12) + ":";
        if (m.getEventEndTime().getMinutes()<10) {
            to += "0";
        }
        to += m.getEventEndTime().getMinutes();
        if (m.getEventEndTime().getHours()>12) {
            to += " PM";
        }
        if (m.getEventEndTime().getHours()<=12) {
            to += " AM";
        }





        time.setText("Time: " + from + " - " + to);
        return convertView;
    }



}
