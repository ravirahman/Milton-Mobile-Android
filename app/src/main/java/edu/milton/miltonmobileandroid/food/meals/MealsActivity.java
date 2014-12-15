package edu.milton.miltonmobileandroid.food.meals;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import edu.milton.miltonmobileandroid.R;

public class MealsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_meals_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MealsActivity.this);
        if (preferences.getBoolean(edu.milton.miltonmobileandroid.util.Consts.FLIK_WARNINGPREFERENCE,true)) {
            View checkboxview = View.inflate(this,R.layout.food_meals_view_dialog_allergywarning,null);
            final CheckBox box = (CheckBox) checkboxview.findViewById(R.id.food_meals_view_dialog_allergywarning_hidewarningmessage);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Allergy Warning");
            builder.setView(checkboxview);
            builder.setMessage("Before placing your order, please inform your server if a person in your party has a food allergy.");
            builder.setPositiveButton("I understand, Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    preferences.edit().putBoolean(edu.milton.miltonmobileandroid.util.Consts.FLIK_WARNINGPREFERENCE,!box.isChecked()).apply();
                    dialog.dismiss();
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
}
