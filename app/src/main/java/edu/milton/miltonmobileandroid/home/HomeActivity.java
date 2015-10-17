package edu.milton.miltonmobileandroid.home;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.campus.doorlock.DoorLockActivity;
import edu.milton.miltonmobileandroid.events.activities.ActivitiesFragment;
import edu.milton.miltonmobileandroid.food.meals.MealsFragment;
import edu.milton.miltonmobileandroid.me.mailbox.MailboxFragment;
import edu.milton.miltonmobileandroid.settings.account.AccountMethods;
import edu.milton.miltonmobileandroid.settings.account.Consts;
import edu.milton.miltonmobileandroid.util.Callback;


public class HomeActivity extends Activity {

    private static final String LOG_TAG = HomeActivity.class.getName();
    private static final String EVENTS_SAA_TAB_TAG = "EVENTS_SAA";
    private static final String FOOD_MEALS_TAB_TAG = "FOOD_MEALS";
    private static final String ME_MAILBOX_TAB_TAG = "ME_MAILBOX";

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

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        actionBar.addTab(actionBar.newTab()
                .setText("Meals")
                .setIcon(R.drawable.food)
                .setTabListener(new TabListener<MealsFragment>(this, "meals",MealsFragment.class)));

        actionBar.addTab(actionBar.newTab()
                .setText("Activities")
                .setIcon(R.drawable.theatre_mask)
                .setTabListener(new TabListener<ActivitiesFragment>(this, "activities",ActivitiesFragment.class)));

        actionBar.addTab(actionBar.newTab()
                .setText("Mailbox")
                .setIcon(R.drawable.message)
                .setTabListener(new TabListener<MailboxFragment>(this,"mailbox",MailboxFragment.class)));

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
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
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_login) {
            AccountMethods.login(this,new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    invalidateOptionsMenu();
                }
            });
        }
        if (id == R.id.action_logout) {
            AccountMethods.logout(this, new Callback() {
                @Override
                public void run(Bundle info) {
                    if (info.getBoolean(Consts.KEY_SUCCESS,false)) {
                        invalidateOptionsMenu();
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
        if (id == R.id.action_about) {
            String strVersion = "Version: ";

            PackageInfo packageInfo;
            try {
                packageInfo = getApplicationContext()
                        .getPackageManager()
                        .getPackageInfo(
                                getApplicationContext().getPackageName(),
                                0
                        );
                strVersion += packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                strVersion += "Unknown";
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setCancelable(false);
            builder.setTitle("About Milton Mobile Android");
            builder.setMessage(strVersion);
            builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

        if (id == R.id.action_door_lock) {
            Intent intent = new Intent(this, DoorLockActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;

        /** Constructor used each time a new tab is created.
         * @param activity  The host Activity, used to instantiate the fragment
         * @param tag  The identifier tag for the fragment
         * @param clz  The fragment's Class, used to instantiate the fragment
         */
        public TabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

    /* The following are each of the ActionBar.TabListener callbacks */

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                // Detach the fragment, because another one is being attached
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            // User selected the already selected tab. Usually do nothing.
        }
    }
}
