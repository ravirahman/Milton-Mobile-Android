package edu.milton.miltonmobileandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
        flik = (ImageButton) view.findViewById(R.id.navigation_fragment_flik_button);
        saa = (ImageButton) view.findViewById(R.id.navigation_fragment_saa_button);
        mailbox = (ImageButton) view.findViewById(R.id.navigation_fragment_mailbox_button);

        flik.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(parentActivity.getApplicationContext(),
                        MealsActivity.class);
                parentActivity.startActivity(i);
            }
        });

        saa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(parentActivity.getApplicationContext(),
                        SaaActivity.class);
                parentActivity.startActivity(i);
            }
        });

        mailbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(parentActivity.getApplicationContext(),
                        MailboxActivity.class);
                parentActivity.startActivity(i);
            }
        });
        // get the listview
        //ExpandableListView expListView = (ExpandableListView) view.findViewById(R.id.lvExp);

        // preparing list data
        //prepareListData();

        //NavigationListAdapter listAdapter = new NavigationListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        //expListView.setAdapter(listAdapter);
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
