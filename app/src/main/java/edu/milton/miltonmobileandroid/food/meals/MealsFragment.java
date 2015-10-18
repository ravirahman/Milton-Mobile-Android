package edu.milton.miltonmobileandroid.food.meals;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.milton.miltonmobileandroid.R;

public class MealsFragment extends Fragment {
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    boolean[] isLoaded = {false,false,false,false,false};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.food_meals_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferences.getBoolean(MealsConsts.FLIK_WARNINGPREFERENCE,true)) {
            View checkboxview = View.inflate(getActivity(),R.layout.food_meals_view_dialog_allergywarning,null);
            final CheckBox box = (CheckBox) checkboxview.findViewById(R.id.food_meals_view_dialog_allergywarning_hidewarningmessage);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setTitle(getString(R.string.string_Allergy_Warning));
            builder.setView(checkboxview);
            builder.setMessage(getString(R.string.food_meals_allergy_warning));
            builder.setPositiveButton(R.string.string_OK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    preferences.edit().putBoolean(MealsConsts.FLIK_WARNINGPREFERENCE,!box.isChecked()).apply();
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        mViewPager = (ViewPager) getActivity().findViewById(R.id.food_meals_fragment);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
                return new MealsListFrag(position, getActivity());
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
