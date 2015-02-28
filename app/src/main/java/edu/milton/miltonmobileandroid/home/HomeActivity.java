package edu.milton.miltonmobileandroid.home;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Field;

import edu.milton.miltonmobileandroid.NavigationFragment;
import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.events.saa.SaaActivity;
import edu.milton.miltonmobileandroid.food.meals.MealsActivity;
import edu.milton.miltonmobileandroid.me.mailbox.MailboxActivity;
import edu.milton.miltonmobileandroid.settings.account.AccountMethods;
import edu.milton.miltonmobileandroid.settings.account.Consts;
import edu.milton.miltonmobileandroid.util.Callback;


public class HomeActivity extends Activity implements NavigationFragment.OnFragmentInteractionListener {

    private static final String LOG_TAG = HomeActivity.class.getName();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private FrameLayout mDrawerFrame;
    private String mDrawerTitle;
    private String mTitle;

    //TEMP
    ImageButton flik;
    ImageButton saa;
    ImageButton mailbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        }
        catch (Exception e) {
            // presumably, not relevant
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        TextView welcomeLabel = (TextView) findViewById(R.id.home_fragment_welcomeLabel);
        if (AccountMethods.isLoggedIn(this)) {
            welcomeLabel.setText("Welcome " + AccountMethods.getFirstName(this) + " " + AccountMethods.getLastName(this));
        }
        else {
            welcomeLabel.setText("Welcome to Milton Academy");
        }
        mTitle = mDrawerTitle = getTitle().toString();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_activity);
        mDrawerFrame = (FrameLayout) findViewById(R.id.home_activity_nav_frame);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.dummy_content, R.string.dummy_content) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        //TEMP
        flik = (ImageButton) findViewById(R.id.home_fragment_meals_button);
        saa = (ImageButton) findViewById(R.id.home_fragment_saa_button);
        mailbox = (ImageButton) findViewById(R.id.home_fragment_mailbox_button);
        flik.setOnClickListener(new View.OnClickListener() {

        @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        MealsActivity.class);
                startActivity(i);

            }
        });


        saa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        SaaActivity.class);
                startActivity(i);
            }
        });

        mailbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        MailboxActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerFrame);
        if(AccountMethods.isLoggedIn(this)){
            menu.removeItem(R.id.action_login);
        }
        else{
            menu.removeItem(R.id.action_logout);
        }

        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public void onRestart() {
        super.onRestart();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (id == R.id.action_login) {
            AccountMethods.login(this,new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    invalidateOptionsMenu();
                    TextView welcomeLabel = (TextView) findViewById(R.id.home_fragment_welcomeLabel);
                    welcomeLabel.setText("Welcome " + AccountMethods.getFirstName(HomeActivity.this) + " " + AccountMethods.getLastName(HomeActivity.this));
                }
            });
        }
        if (id == R.id.action_logout) {
            AccountMethods.logout(this, new Callback() {
                @Override
                public void run(Bundle info) {
                    if (info.getBoolean(Consts.KEY_SUCCESS,false)) {
                        invalidateOptionsMenu();
                        TextView welcomeLabel = (TextView) findViewById(R.id.home_fragment_welcomeLabel);
                        welcomeLabel.setText("Welcome to Milton Academy");
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Account Not Removed");
                    builder.setMessage(info.getString(Consts.KEY_MESSAGE,"You have NOT been logged out because of an error."));
                    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    public static class HomeFragment extends Fragment {

        public HomeFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.home_fragment, container, false);
            return rootView;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
