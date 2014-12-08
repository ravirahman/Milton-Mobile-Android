package edu.milton.miltonmobileandroid.mailbox;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.accounts.AccountManager;

import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.util.Consts;

public class MailboxActivity extends AccountAuthenticatorActivity {

    // Progress Dialog
    private ProgressDialog pDialog;
    private String mailbox, combo;
    private TextView mailboxText, comboText;
    private boolean updateReady = false;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    private static final String LOGIN_URL = "http://ma1geek.org/mailbox2.php";

    private static final String TAG_MAILBOX = "mailbox";
    private static final String TAG_COMBO = "combo";
    private static final String TAG_MESSAGE = "message";

    private AccountManager manager;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailbox);

        mailboxText = (TextView) findViewById(R.id.mailbox);
        comboText = (TextView) findViewById(R.id.combo);

        manager = AccountManager.get(this);
        account = manager.getAccountsByType(Consts.MMA_ACCOUNTTYPE)[0];
        new AttemptLogin().execute();
        updateText();
    }

    protected void updateText() {
        long startTime = System.currentTimeMillis();
        while(!updateReady && System.currentTimeMillis()-startTime < 5*1000) {
            //forgive me
        }

        //parse combo text
        while(combo.endsWith("<")) {
            combo = combo.substring(0, combo.length()-1);
        }

        mailboxText.setText("Mailbox: "+ mailbox);
        comboText.setText("Combo: " + combo);
    }

    protected String[] getAccount() {
        String[] user = new String[2];

        AccountManager newManager = AccountManager.get(this);
        Account[] accounts = newManager.getAccountsByType(Consts.MMA_ACCOUNTTYPE);
        final String UserName = newManager.getUserData(accounts[0],AccountManager.KEY_ACCOUNT_NAME);
        Account newAccount = new Account(UserName, Consts.MMA_ACCOUNTTYPE);
        final String Password = newManager.getPassword(newAccount);
        user[0] = UserName;
        user[1] = Password;

        return user;
    }


    class AttemptLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MailboxActivity.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            String[] user = new String[2];
            user = getAccount();
            String username = user[0];
            String password = user[1];
            System.out.println("1");
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
                        params);
                System.out.println(json.toString());
                // check your log for json response
                Log.d("Mailbox Request", json.toString());
                System.out.println(2);
                // json success tag
                if (!json.toString().equals("")) {
                    System.out.println(3);
                    Log.d("Login Successful!", json.toString());
                    // save user data
                    try {
                        mailbox = json.getString(TAG_MAILBOX);
                        combo = json.getString(TAG_COMBO);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    updateReady = true;
                    //updateText();
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Mailbox Invalid Login", json.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(MailboxActivity.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

    }

}