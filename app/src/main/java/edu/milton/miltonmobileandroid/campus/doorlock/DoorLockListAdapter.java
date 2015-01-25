package edu.milton.miltonmobileandroid.campus.doorlock;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import edu.milton.miltonmobileandroid.R;


public class DoorLockListAdapter extends BaseAdapter {

    private ArrayList<DoorLock> locks = new ArrayList<>();
    private LayoutInflater inflater;
    private Activity activity;

    public DoorLockListAdapter(Activity activity) {
        this.activity = activity;
    }

    public void addLockToList(DoorLock lock) {
        for (DoorLock doorLock : locks) {
            if (doorLock.mac.equals(lock.mac)) {
                return;
            }
        }
        locks.add(lock);
        this.notifyDataSetChanged();
    }

    public void removeLockFromList(DoorLock lock) {
        locks.remove(lock);
        this.notifyDataSetChanged();
    }
    public void removeAll() {
        locks.clear();
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return locks.size();
    }

    @Override
    public DoorLock getItem(int position) {
        return locks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return locks.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.campus_doorlock_listview_item, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.campus_doorlock_listview_item_title);

        // getting movie data for the row
        final DoorLock m = locks.get(position);


        // title
        title.setText(m.name);
        return convertView;
    }
}
