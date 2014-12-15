package edu.milton.miltonmobileandroid.flik;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.milton.miltonmobileandroid.R;



@SuppressLint("SimpleDateFormat")
public class FlikMenuActivity extends FragmentActivity implements AllergenWarning.NoticeDialogListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link android.support.v4.view.ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

    ListView mListView;

    static boolean updateReady = false;

    static ArrayList<MenuItem> food = new ArrayList<MenuItem>();

    View vp1;
	
	//the string email used for voting
	String email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flik_food_view);


		Intent i = getIntent();
		if (i.getExtras() != null) {
			email = i.getStringExtra("email");
		}
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

        Log.e("hello", "MSections: " + mSectionsPagerAdapter);
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);

        mListView = (ListView) findViewById(R.id.listView3);

        Log.e("something******************************", "Mview: "+mViewPager);

		mViewPager.setAdapter(mSectionsPagerAdapter);

        Log.e("something*****************************2", "hi");


        long startTime = System.currentTimeMillis();

        while(!updateReady) {
            //forgive me
        }

        Log.e("UR", "Bool: " + updateReady);

            buildDisplayString();



        Log.e("something*****************************3", "hi");


		//Log.d("this is the email", email);
		showNoticeDialog();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		getMenuInflater().inflate(R.menu.menu_flik, menu);
		return true;


	}

    public void buildDisplayString(){
        Log.e("something*****************************4", "hi");


        ArrayList<String> strings = new ArrayList<String>();

        Log.e("something*****************************5", "hi");


        food.add(new MenuItem(true, "FredTest"));


        for(MenuItem m : food){
            String name = m.getItemName();
            String type = m.getItemClass();
            String time = m.getItemTime();

            strings.add(name + "  -  " + type + "  -  " + time);
            strings.add("Fred");


        }

        Log.e("something*****************************6", "hi");

        for(String s : strings){

            Log.e("Strings", s);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,strings );

        mListView.setAdapter(arrayAdapter);


    }

	/**
	 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			return new FlikListFrag(position, email);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			Calendar c = Calendar.getInstance(l);
		  	SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d");
		  	String formattedDate[] = new String [getCount()];
		  	formattedDate[0] = df.format(c.getTime());
		  	c.add(Calendar.DATE, 1);
		  	formattedDate[1] = df.format(c.getTime());
		  	c.add(Calendar.DATE, 1);
		  	formattedDate[2] = df.format(c.getTime());
		  	c.add(Calendar.DATE, 1);
		  	formattedDate[3] = df.format(c.getTime());
		  	c.add(Calendar.DATE, 1);
		  	formattedDate[4] = df.format(c.getTime());
		  	
			switch (position) {
			case 0:
				return formattedDate[0];
			case 1:
				return formattedDate[1];
			case 2:
				return formattedDate[2];
			case 3:
				return formattedDate[3];			
			case 4:
				return formattedDate[4];				
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		public static final String ARG_SECTION_TEXT = "This is Section N";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tabs_test_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			TextView secTextView = (TextView) rootView.findViewById(R.id.seclabel2);
			secTextView.setText(ARG_SECTION_TEXT);
			return rootView;
		}
	}
	
	public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AllergenWarning();
        dialog.show(getSupportFragmentManager(), "Allergen Warning");
    }
	
	public void onDialogPositiveClick(DialogFragment dialog) {
        dialog.dismiss();
    }

}
