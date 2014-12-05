package edu.milton.miltonmobileandroid;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.milton.miltonmobileandroid.flik.old_FlikActivity;
import edu.milton.miltonmobileandroid.mailbox.MailboxActivity;
import edu.milton.miltonmobileandroid.saa.SaaActivity;
import edu.milton.miltonmobileandroid.settings.LoginActivity;
import edu.milton.miltonmobileandroid.util.Consts;


public class HomeActivity extends Activity {
    ImageButton flik;
    ImageButton saa;
    ImageButton mailbox;

    private static int LOGIN_REQUEST_CODE = 1;
    private static int LOGOUT_REQUEST_CODE = 2;
    AccountManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        flik = (ImageButton) findViewById(R.id.flik_button);
        saa = (ImageButton) findViewById(R.id.saa_button);
        mailbox = (ImageButton) findViewById(R.id.mailbox_button);

        flik.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        old_FlikActivity.class);
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
        manager = AccountManager.get(this);
        if (isLoggedIn()) {
            TextView welcomeLabel = ((TextView) findViewById(R.id.welcomeLabel));
//            welcomeLabel.setText("Welcome " + manager.getUserData(manager.getAccounts()[0],"firstname") + " " + manager.getUserData(manager.getAccounts()[0],"lastname"));
 //           ViewGroup.LayoutParams params = welcomeLabel.getLayoutParams();
 //           params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
 //           welcomeLabel.setLayoutParams(params);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

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
                        TextView welcomeLabel = ((TextView) findViewById(R.id.welcomeLabel));
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
    public static class HomeFragment extends Fragment {

        public HomeFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;
        }
    }
}
