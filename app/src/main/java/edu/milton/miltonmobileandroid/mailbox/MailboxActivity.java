package edu.milton.miltonmobileandroid.mailbox;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
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

public class MailboxActivity extends Activity{

    // Progress Dialog
    private ProgressDialog pDialog;
    private String mailbox, combo;
    private TextView mailboxText, comboText;
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

        mailboxText = (TextView) findViewById(R.id.mailbox);
        comboText = (TextView) findViewById(R.id.combo);

        manager = AccountManager.get(this);
        account = manager.getAccountsByType(Consts.MMA_ACCOUNTTYPE)[0];
        new AttemptLogin().execute();
    }

    protected void updateText() {
        mailboxText.setText(mailbox);
        //TODO: format combo string
        comboText.setText(combo);
    }

    protected String getUser() {
        String username = "";
        return username;
    }

    protected String getPassword() {
        String password = "";
        return password;
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
            String username = getUser();
            String password = getPassword();
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

                // json success tag
                if (json.toString() != "") {
                    Log.d("Login Successful!", json.toString());
                    // save user data
                    try {
                        mailbox = json.getString(TAG_MAILBOX);
                        combo = json.getString(TAG_COMBO);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    updateText();
                    return json.getString(TAG_MESSAGE);
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