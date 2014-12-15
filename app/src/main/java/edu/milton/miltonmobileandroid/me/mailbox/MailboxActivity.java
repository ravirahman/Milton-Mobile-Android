package edu.milton.miltonmobileandroid.me.mailbox;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.accounts.AccountManager;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.settings.account.LoginActivity;
import edu.milton.miltonmobileandroid.util.Consts;
import edu.milton.miltonmobileandroid.util.JsonHttp;

public class MailboxActivity extends AccountAuthenticatorActivity {

    // Progress Dialog
    private ProgressDialog pDialog;
    private String mailbox, combo;
    private TextView mailboxText, comboText;
    private boolean updateReady = false;
    private static String LOG_TAG = MailboxActivity.class.getName();
    // JSON parser class
    JsonHttp jsonParser = new JsonHttp();

    private static final String LOGIN_URL = "http://ma1geek.org/mailbox2.php";

    private static final String TAG_MAILBOX = "mailbox";
    private static final String TAG_COMBO = "combo";
    private static final String TAG_MESSAGE = "message";

    private static final int LOGIN_REQUESTCODE = 1;

    private AccountManager manager;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        manager = AccountManager.get(this);
        if (manager.getAccountsByType(Consts.MMA_ACCOUNTTYPE).length < 1) {
            Log.v(LOG_TAG,"Not logged in");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please Log-In");
            builder.setMessage("In order to retreive your mailbox combination, please login first");
            builder.setPositiveButton("Login",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MailboxActivity.this, LoginActivity.class);
                    startActivityForResult(intent, LOGIN_REQUESTCODE);
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
        else {
            setContentView(R.layout.me_mailbox_activity);

            mailboxText = (TextView) findViewById(R.id.me_mailbox_fragment_mailbox);
            comboText = (TextView) findViewById(R.id.me_mailbox_fragment_combo);
            Log.v(LOG_TAG,"Logged in");
            retrieveCombination();
        }

    }
    private void retrieveCombination() {
        account = manager.getAccountsByType(Consts.MMA_ACCOUNTTYPE)[0];

        pDialog = new ProgressDialog(MailboxActivity.this);
        pDialog.setMessage("Please wait while we retrieve your mailbox combination");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        String[] user = getAccount();
        String username = user[0];
        String password = user[1];

        RequestParams params = new RequestParams();
        params.add("username",username);
        params.add("password",password);
        JsonHttp.request(LOGIN_URL,"POST",params,null,null,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject response) {
                pDialog.dismiss();
                try {
                    mailbox = response.getString(TAG_MAILBOX);
                    combo = response.getString(TAG_COMBO);
                    updateText();
                } catch (JSONException e) {

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                retrieveCombination();
            }
            else {
                finish();
            }
        }
    }

    protected void updateText() {

        //parse combo text
        while(combo.endsWith("<")) {
            combo = combo.substring(0, combo.length()-1);
        }

        mailboxText.setText("Mailbox: "+ mailbox);
        comboText.setText("Combo: " + combo);
    }

    protected String[] getAccount() {
        String[] user = new String[2];

        Account[] accounts = manager.getAccountsByType(Consts.MMA_ACCOUNTTYPE);
        final String UserName = manager.getUserData(accounts[0],AccountManager.KEY_ACCOUNT_NAME);
        Account newAccount = new Account(UserName, Consts.MMA_ACCOUNTTYPE);
        final String Password = manager.getPassword(newAccount);
        user[0] = UserName;
        user[1] = Password;

        return user;
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

}