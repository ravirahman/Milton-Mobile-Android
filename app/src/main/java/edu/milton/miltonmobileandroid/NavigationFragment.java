package edu.milton.miltonmobileandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;

import edu.milton.miltonmobileandroid.campus.doorlock.DoorLockActivity;
import edu.milton.miltonmobileandroid.events.saa.SaaActivity;
import edu.milton.miltonmobileandroid.food.meals.MealsActivity;
import edu.milton.miltonmobileandroid.me.mailbox.MailboxActivity;


public class NavigationFragment extends Fragment {
    //OnFragmentInteractionListener mListener;
    Activity parentActivity;
    public static NavigationFragment newInstance() {
        return new NavigationFragment();
    }

    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.navigation_fragment, container, false);

        return view;
    }
    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ExpandableListView navigationListView = (ExpandableListView) parentActivity.findViewById(R.id.navigation_fragment_listView);

        final HashMap<NavigationGroup, ArrayList<NavigationItem>> navigationItems = new HashMap<NavigationGroup, ArrayList<NavigationItem>>();

        NavigationGroup campus = new NavigationGroup("Campus",0);
        ArrayList<NavigationItem> campusNavigationItems = new ArrayList<NavigationItem>();
        NavigationItem doorLock = new NavigationItem("Door Lock",1, DoorLockActivity.class);
        campusNavigationItems.add(doorLock);
        navigationItems.put(campus,campusNavigationItems);

        NavigationGroup events = new NavigationGroup("Events",10);
        ArrayList<NavigationItem> eventsNavigationItems = new ArrayList<NavigationItem>();
        NavigationItem saa = new NavigationItem("SAA",11, SaaActivity.class);
        eventsNavigationItems.add(saa);
        navigationItems.put(events,eventsNavigationItems);

        NavigationGroup food = new NavigationGroup("Food",20);
        ArrayList<NavigationItem> foodNavigationItems = new ArrayList<NavigationItem>();
        NavigationItem meals = new NavigationItem("Flik",21, MealsActivity.class);
        foodNavigationItems.add(meals);
        navigationItems.put(food,foodNavigationItems);

        NavigationGroup me = new NavigationGroup("Me",30);
        ArrayList<NavigationItem> meNavigationItems = new ArrayList<NavigationItem>();
        NavigationItem mailbox = new NavigationItem("MailBox",1, MailboxActivity.class);
        meNavigationItems.add(mailbox);
        navigationItems.put(me,meNavigationItems);

        NavigationListAdapter listAdapter = new NavigationListAdapter(parentActivity, navigationItems);

        // setting list adapter
        navigationListView.setAdapter(listAdapter);

        navigationListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ArrayList<NavigationGroup> groups = new ArrayList<NavigationGroup>(navigationItems.keySet());
                ArrayList<NavigationItem> items = navigationItems.get(groups.get(groupPosition));
                NavigationItem item = items.get(childPosition);
                Intent i = new Intent(parentActivity.getApplicationContext(), item.aClass);
                startActivity(i);
                return true;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        parentActivity = activity;




        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

    }

}
