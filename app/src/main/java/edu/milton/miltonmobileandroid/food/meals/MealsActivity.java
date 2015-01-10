package edu.milton.miltonmobileandroid.food.meals;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.milton.miltonmobileandroid.R;

public class MealsActivity extends FragmentActivity {
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_meals_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MealsActivity.this);
        if (preferences.getBoolean(Consts.FLIK_WARNINGPREFERENCE,true)) {
            View checkboxview = View.inflate(this,R.layout.food_meals_view_dialog_allergywarning,null);
            final CheckBox box = (CheckBox) checkboxview.findViewById(R.id.food_meals_view_dialog_allergywarning_hidewarningmessage);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Allergy Warning");
            builder.setView(checkboxview);
            builder.setMessage("Before placing your order, please inform your server if a person in your party has a food allergy.");
            builder.setPositiveButton("I understand, Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    preferences.edit().putBoolean(Consts.FLIK_WARNINGPREFERENCE,!box.isChecked()).apply();
                    dialog.dismiss();
                    // Create the adapter that will return a fragment for each of the three
                    // primary sections of the app.
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                    // Set up the ViewPager with the sections adapter.
                    mViewPager = (ViewPager) findViewById(R.id.food_meals_fragment);
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                }
            });
            builder.create().show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flik, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MealsFragment extends Fragment {

        public MealsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.food_meals_fragment, container, false);
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            return new MealsListFrag(position,MealsActivity.this);
            //return null;
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
}
