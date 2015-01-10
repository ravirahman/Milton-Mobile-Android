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

import com.loopj.android.http.*;

import org.apache.http.Header;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import edu.milton.miltonmobileandroid.R;

public class LoginActivity extends AccountAuthenticatorActivity {

    private static final String LOG_TAG = LoginActivity.class.getName();
    private EditText usernameEditText;
    private EditText passwordEditText;
    private RadioGroup typeRadioGroup;
    private RadioButton teacherRadioButton;
    private RadioButton studentRadioButton;
    private AccountManager manager;
    private Button signInButton;

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

        typeRadioGroup = (RadioGroup) findViewById(R.id.settings_login_fragment_typeGroup);
        teacherRadioButton = (RadioButton) findViewById(R.id.settings_login_fragment_teacher);
        studentRadioButton = (RadioButton) findViewById(R.id.settings_login_fragment_student);

        signInButton = (Button) findViewById(R.id.settings_login_fragment_login);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        if (savedInstanceState != null) {
            usernameEditText.setText(savedInstanceState.getString("username"));
            passwordEditText.setText(savedInstanceState.getString("username"));
            if (!savedInstanceState.getBoolean("student",true)) {
                teacherRadioButton.setChecked(true);
                studentRadioButton.setChecked(false);
            }
        }

        if (AccountMethods.isLoggedIn(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Not yet available");
            builder.setMessage("Sorry, only one account allowed at a time.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
        teacherRadioButton.setError(null);
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

        if (teacherRadioButton.isChecked()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Not yet available");
            builder.setMessage("Sorry, teacher logins are not supported yet. Look for an app update soon!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            teacherRadioButton.setError("Sorry, teacher logins are not supported yet. Look for an app update soon!");
            messageView = teacherRadioButton;
            cancel = true;
        }

        if (AccountMethods.isLoggedIn(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Not yet available");
            builder.setMessage("Sorry, only one account allowed at a time.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
            final ProgressDialog ringProgressDialog = ProgressDialog.show(LoginActivity.this, "Signing in ...", "Signing in...", true);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();

            AsyncHttpClient client = new AsyncHttpClient();
            String url;
            RequestParams params = new RequestParams();
            if (studentRadioButton.isChecked()) {

                url = "https://my.milton.edu/student/index.cfm";
                params.put("UserLogin", username);
                params.put("UserPassword", password);
                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        ringProgressDialog.dismiss();
                        String decoded = new String(bytes);
                        Document doc = Jsoup.parse(decoded);
                        Elements things = doc.getElementsByClass("bluelabel");
                        if (things.isEmpty()) {
                            usernameEditText.setError("Please double-check your username, password, and student/teacher selection.");
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
                            finishLogin(username, password, firstName, lastName, classnumber, Consts.STUDENT);
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        usernameEditText.setError("Sorry, there was an error. Please try again.");
                        usernameEditText.requestFocus();
                        ringProgressDialog.dismiss();
                    }
                });
            } else { //is a teacher
                ringProgressDialog.dismiss();
            }
            /*String u = manager.getUserData(manager.getAccounts()[0],AccountManager.KEY_ACCOUNT_NAME);
            Account account = new Account(u, Consts.MMA_ACCOUNTTYPE);
            String p = manager.getPassword(account);*/
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED, null);
        finish();
    }

    private void finishLogin(String username, String password, String firstname, String lastname, int classnumber, String userType) {
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
            View rootView = inflater.inflate(R.layout.settings_login_fragment, container, false);
            return rootView;

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("username",usernameEditText.getText().toString());
        outState.putString("password",passwordEditText.getText().toString());
        outState.putBoolean("student",studentRadioButton.isChecked());
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