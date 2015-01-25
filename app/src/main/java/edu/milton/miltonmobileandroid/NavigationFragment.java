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
    ImageButton flik;
    ImageButton saa;
    ImageButton mailbox;
    OnFragmentInteractionListener mListener;
    Activity parentActivity;

    public static NavigationFragment newInstance() {
        return new NavigationFragment();
    }

    public NavigationFragment() {

        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.navigation_fragment, container, false);


        // get the listview
        ExpandableListView navigationListView = (ExpandableListView) parentActivity.findViewById(R.id.navigation_fragment_listView);

        final ArrayList<NavigationGroup> navigationGroups = new ArrayList<NavigationGroup>();
        final HashMap<NavigationGroup, ArrayList<NavigationItem>> navigationItems = new HashMap<NavigationGroup, ArrayList<NavigationItem>>();

        NavigationGroup campus = new NavigationGroup("Campus",0);
        navigationGroups.add(campus);
        ArrayList<NavigationItem> campusNavigationItems = new ArrayList<>();
        NavigationItem doorLock = new NavigationItem("Door Lock",1, DoorLockActivity.class);
        campusNavigationItems.add(doorLock);

        NavigationGroup events = new NavigationGroup("Events",10);
        navigationGroups.add(events);
        ArrayList<NavigationItem> eventNavigationItems = new ArrayList<>();
        NavigationItem saa = new NavigationItem("SAA",11, SaaActivity.class);
        eventNavigationItems.add(saa);

        NavigationGroup food = new NavigationGroup("Food",20);
        navigationGroups.add(food);
        ArrayList<NavigationItem> foodNavigationItems = new ArrayList<>();
        NavigationItem meals = new NavigationItem("Flik",21, MealsActivity.class);
        foodNavigationItems.add(meals);

        NavigationGroup me = new NavigationGroup("Me",30);
        navigationGroups.add(me);
        ArrayList<NavigationItem> meNavigationItems = new ArrayList<>();
        NavigationItem mailbox = new NavigationItem("MailBox",1, MailboxActivity.class);
        meNavigationItems.add(mailbox);


        NavigationListAdapter listAdapter = new NavigationListAdapter(parentActivity, navigationGroups, navigationItems);

        // setting list adapter
        navigationListView.setAdapter(listAdapter);

        navigationListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                NavigationGroup group = navigationGroups.get(groupPosition);
                NavigationItem item = navigationItems.get(group).get(childPosition);
                Intent i = new Intent(parentActivity.getApplicationContext(),
                        item.aClass);
                startActivity(i);
                return true;
            }
        });
        return view;
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


    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
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
