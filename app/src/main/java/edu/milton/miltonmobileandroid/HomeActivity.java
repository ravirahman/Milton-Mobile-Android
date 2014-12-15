package edu.milton.miltonmobileandroid;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
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

import edu.milton.miltonmobileandroid.events.saa.SaaActivity;
import edu.milton.miltonmobileandroid.food.meals.MealsActivity;
import edu.milton.miltonmobileandroid.me.mailbox.MailboxActivity;
import edu.milton.miltonmobileandroid.settings.account.LoginActivity;
import edu.milton.miltonmobileandroid.util.Consts;


public class HomeActivity extends AccountAuthenticatorActivity implements NavigationFragment.OnFragmentInteractionListener {

    private static int LOGIN_REQUEST_CODE = 1;
    private static int LOGOUT_REQUEST_CODE = 2;
    AccountManager manager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private FrameLayout mDrawerFrame;
    private String mDrawerTitle;
    private String mTitle;

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
        manager = AccountManager.get(this);
        setContentView(R.layout.home_activity);

        manager = AccountManager.get(this);
        if (isLoggedIn()) {
            TextView welcomeLabel = ((TextView) findViewById(R.id.home_fragment_welcomeLabel));
//            welcomeLabel.setText("Welcome " + manager.getUserData(manager.getAccounts()[0],"firstname") + " " + manager.getUserData(manager.getAccounts()[0],"lastname"));
  //          ViewGroup.LayoutParams params = welcomeLabel.getLayoutParams();
    //        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
      //      welcomeLabel.setLayoutParams(params);
        }
        mTitle = mDrawerTitle = getTitle().toString();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_activity);
        mDrawerFrame = (FrameLayout) findViewById(R.id.home_activity_nav_frame);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.dummy_content, R.string.dummy_content) {

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
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerFrame);
        if(isLoggedIn()){
            menu.removeItem(R.id.action_login);
        }
        else{
            menu.removeItem(R.id.action_logout);
        }

        return super.onPrepareOptionsMenu(menu);
    }


    public boolean isLoggedIn() {
        return manager.getAccountsByType(Consts.MMA_ACCOUNTTYPE).length > 0;
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
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
        }
        if (id == R.id.action_logout) {
            Intent intent = new Intent(this,LoginActivity.class);
            intent.putExtra("logout",true);
            startActivityForResult(intent, LOGOUT_REQUEST_CODE);
        }

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int classnumber = data.getIntExtra("classnumber", 0);
                String firstname = data.getStringExtra("firstname");
                String lastname = data.getStringExtra("lastname");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("Welcome");
                builder.setMessage(firstname + " " + lastname + " successfully signed in");
                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        HomeActivity.this.invalidateOptionsMenu();
                        TextView welcomeLabel = ((TextView) findViewById(R.id.home_fragment_welcomeLabel));
//                        welcomeLabel.setText("Welcome " + manager.getUserData(manager.getAccounts()[0],"firstname") + " " + manager.getUserData(manager.getAccounts()[0],"lastname"));
  //                      ViewGroup.LayoutParams params = welcomeLabel.getLayoutParams();
    //                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
      //                  welcomeLabel.setLayoutParams(params);
                    }
                });
                builder.create().show();
            }
        }
        if (requestCode == LOGOUT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MainFragment extends Fragment {

        public MainFragment() {
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
