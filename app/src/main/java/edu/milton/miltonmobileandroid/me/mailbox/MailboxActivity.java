package edu.milton.miltonmobileandroid.me.mailbox;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.settings.account.AccountMethods;
import edu.milton.miltonmobileandroid.util.JsonHttp;

public class MailboxActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;
    private String mailbox, combo;
    private TextView mailboxText, comboText;
    private static String LOG_TAG = MailboxActivity.class.getName();

    private static final String LOGIN_URL = "http://ma1geek.org/mailbox2.php";

    private static final String TAG_MAILBOX = "mailbox";
    private static final String TAG_COMBO = "combo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (!AccountMethods.isLoggedIn(this)) {
            Log.v(LOG_TAG,"Not logged in");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please Log-In");
            builder.setMessage("In order to retreive your mailbox combination, please login first");
            builder.setPositiveButton("Login",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AccountMethods.login(MailboxActivity.this,new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            if (!AccountMethods.isLoggedIn(MailboxActivity.this)) {
                                finish();
                                return;
                            }
                            setContentView(R.layout.me_mailbox_activity);
                            mailboxText = (TextView) findViewById(R.id.me_mailbox_fragment_mailbox);
                            comboText = (TextView) findViewById(R.id.me_mailbox_fragment_combo);
                            retrieveCombination();

                        }
                    });
                }
            });
            builder.setNegativeButton("No Thanks, Go Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MailboxActivity.this.finish();
                }
            });
            builder.create().show();
            //display an alert saying you must log in
            //give two options, one to go back, and one to go to the login page
            return;
        }
        setContentView(R.layout.me_mailbox_activity);
        mailboxText = (TextView) findViewById(R.id.me_mailbox_fragment_mailbox);
        comboText = (TextView) findViewById(R.id.me_mailbox_fragment_combo);
        retrieveCombination();


    }
    private void retrieveCombination() {
        pDialog = new ProgressDialog(MailboxActivity.this);
        pDialog.setMessage("Please wait while we retrieve your mailbox combination");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        RequestParams params = new RequestParams();
        params.add("username",AccountMethods.getUsername(this));

        params.add("password",AccountMethods.getPassword(this));
        JsonHttp.request(LOGIN_URL,"POST",params,null,null,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject response) {
                pDialog.dismiss();
                try {
                    mailbox = response.getString(TAG_MAILBOX);
                    combo = response.getString(TAG_COMBO);
                    updateText();
                } catch (JSONException e) {
                    Log.v(LOG_TAG,"Error with JSON parsing");
                }
            }
            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                pDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(MailboxActivity.this);
                builder.setTitle("Error retrieving combination");
                builder.setMessage("There was an error retrieving your Mailbox combination. Please check your network connection.");
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    protected void updateText() {

        //parse combo text
        while(combo.endsWith("<")) {
            combo = combo.substring(0, combo.length()-1);
        }

        mailboxText.setText("Mailbox: "+ mailbox);
        comboText.setText("Combo: " + combo);
    }

    public static class MailboxFragment extends Fragment {
        public MailboxFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.me_mailbox_fragment, container, false);
            return rootView;

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}