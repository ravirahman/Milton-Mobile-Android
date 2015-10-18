package edu.milton.miltonmobileandroid.settings.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;

import android.accounts.AccountManager;
import android.annotation.TargetApi;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import edu.milton.miltonmobileandroid.util.JsonHttp;
import org.apache.http.Header;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import edu.milton.miltonmobileandroid.R;

public class LoginActivity extends AccountAuthenticatorActivity {

    private static final String LOG_TAG = LoginActivity.class.getName();
    private EditText usernameEditText;
    private EditText passwordEditText;
    private AccountManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = AccountManager.get(this);
        setContentView(R.layout.settings_login_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setupActionBar();

        // Set up the login form.
        usernameEditText = (EditText) findViewById(R.id.settings_login_fragment_username);
        passwordEditText = (EditText) findViewById(R.id.settings_login_fragment_password);


        Button signInButton = (Button) findViewById(R.id.settings_login_fragment_login);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        if (savedInstanceState != null) {
            usernameEditText.setText(savedInstanceState.getString("username"));
            passwordEditText.setText(savedInstanceState.getString("username"));
        }

        if (AccountMethods.isLoggedIn(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(getString(R.string.string_Error));
            builder.setMessage(getString(R.string.settings_login_already_logged_in));
            builder.setPositiveButton(getString(R.string.string_OK), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.create().show();
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        usernameEditText.setError(null);
        passwordEditText.setError(null);
        // Store values at the time of the login attempt.
        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        boolean cancel = false;
        View messageView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.settings_login_errorInvalidPassword));
            messageView = passwordEditText;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.settings_login_errorInvalidUsername));
            messageView = usernameEditText;
            cancel = true;
        }

        if (AccountMethods.isLoggedIn(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(getString(R.string.string_Error));
            builder.setMessage(getString(R.string.settings_login_already_logged_in));
            builder.setPositiveButton(getString(R.string.string_OK), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.create().show();
            return;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            messageView.requestFocus();
        } else {
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(usernameEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            final ProgressDialog ringProgressDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.string_Sign_In), getString(R.string.string_Please_Wait), true);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();

            AsyncHttpClient client = new AsyncHttpClient();
            String url;
            RequestParams params = new RequestParams();

            url = "http://my.milton.edu/student/index.cfm";
            params.put("UserLogin", username);
            params.put("UserPassword", password);
            client.setTimeout(100000);
            client.setUserAgent(JsonHttp.USER_AGENT);

            client.post(url, params, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, String decoded) {
                    ringProgressDialog.dismiss();
                    Document doc = Jsoup.parse(decoded);
                    Elements things = doc.getElementsByClass("bluelabel");
                    if (things.isEmpty()) {
                        usernameEditText.setError(getString(R.string.string_Please_check_your_credentials));
                        usernameEditText.requestFocus();
                    }
                    else {
                        String toParse = things.first().toString();
                        String lastName = toParse.substring(toParse.indexOf(">")+9,toParse.indexOf(","));
                        String firstName = toParse.substring(toParse.indexOf(",")+2,toParse.indexOf("[")-1);
                        String classRoman = toParse.substring(toParse.indexOf("[")+8,toParse.indexOf("::")-1);
                        int classnumber = 0;
                        if (classRoman.equals("IV")) {
                            classnumber = 4;

                        } else if (classRoman.equals("III")) {
                            classnumber = 3;

                        } else if (classRoman.equals("II")) {
                            classnumber = 2;

                        } else if (classRoman.equals("I")) {
                            classnumber = 1;
                        }
                        finishLogin(username, password, firstName, lastName, classnumber);
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, String response, Throwable throwable) {
                    usernameEditText.setError(getString(R.string.string_Please_try_again));
                    usernameEditText.requestFocus();
                    ringProgressDialog.dismiss();
                }
            });
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED, null);
        finish();
    }

    private void finishLogin(String username, String password, String firstname, String lastname, int classnumber) {
        final Account account = new Account(username, Consts.ACCOUNT_TYPE);
        manager.addAccountExplicitly(account,password,null);
        manager.setAuthToken(account, Consts.ACCOUNT_TYPE, password);
        manager.setUserData(account,Consts.KEY_FIRSTNAME,firstname);
        manager.setUserData(account,Consts.KEY_LASTNAME,lastname);
        manager.setUserData(account,Consts.KEY_CLASSNUMBER,Integer.toString(classnumber));
        manager.setUserData(account,AccountManager.KEY_ACCOUNT_NAME,username);

        final Intent answer = new Intent();
        answer.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        answer.putExtra(Consts.KEY_FIRSTNAME,firstname);
        answer.putExtra(Consts.KEY_LASTNAME,lastname);
        answer.putExtra(Consts.KEY_CLASSNUMBER,classnumber);
        answer.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Consts.ACCOUNT_TYPE);

        setAccountAuthenticatorResult(answer.getExtras());
        setResult(RESULT_OK, answer);
        finish();
    }

    public static class LoginFragment extends Fragment {

        public LoginFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.settings_login_fragment, container, false);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("username",usernameEditText.getText().toString());
        outState.putString("password",passwordEditText.getText().toString());
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