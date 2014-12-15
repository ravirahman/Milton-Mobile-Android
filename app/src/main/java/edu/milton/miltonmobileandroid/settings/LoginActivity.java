package edu.milton.miltonmobileandroid.settings;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.TargetApi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.io.IOException;

import javax.xml.transform.Result;

import edu.milton.miltonmobileandroid.HomeActivity;
import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.util.Consts;

public class LoginActivity extends AccountAuthenticatorActivity {

    private static final String LOG_TAG = LoginActivity.class.getName();
    private View loginView;
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
        if (getIntent().getBooleanExtra("logout",false)) {
            logout();
            return;
        }
        setContentView(R.layout.fragment_settings_login);

        setupActionBar();

        loginView = findViewById(R.id.signInRootView);
        // Set up the login form.
        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);

        typeRadioGroup = (RadioGroup) findViewById(R.id.typeGroup);
        teacherRadioButton = (RadioButton) findViewById(R.id.teacher);
        studentRadioButton = (RadioButton) findViewById(R.id.student);

        signInButton = (Button) findViewById(R.id.login);
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

        if (isLoggedIn()) {
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

    public boolean isLoggedIn() {
        return manager.getAccountsByType(Consts.MMA_ACCOUNTTYPE).length > 0;
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

        if (isLoggedIn()) {
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
                            finishLogin(username, password, firstName, lastName, classnumber, Consts.MMA_STUDENT);
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        usernameEditText.setError("Sorry, there was an error. Please try again.");
                        usernameEditText.requestFocus();
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
        final Intent answer = new Intent();
        setResult(RESULT_CANCELED, null);
        finish();
    }

    public Account[] getAccounts() {

        return manager.getAccountsByType(Consts.MMA_ACCOUNTTYPE);
    }

    public void logout() {
        if (getAccounts().length > 0) {
            final String username = manager.getUserData(getAccounts()[0],AccountManager.KEY_ACCOUNT_NAME);
            manager.removeAccount(getAccounts()[0], new AccountManagerCallback<Boolean>() {
                @Override
                public void run(AccountManagerFuture<Boolean> future) {
                    try {
                        if (future.getResult().booleanValue()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setCancelable(false);
                            builder.setTitle("Account Removed");
                            builder.setMessage(username + " has successfully logged out");
                            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    LoginActivity.this.setResult(RESULT_OK);
                                    LoginActivity.this.finish();

                                }
                            });
                            builder.create().show();
                        }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setCancelable(false);
                            builder.setTitle("Account Not Removed");
                            builder.setMessage(username + " has could not be logged out");
                            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    LoginActivity.this.setResult(RESULT_CANCELED);
                                    LoginActivity.this.finish();

                                }
                            });
                            builder.create().show();
                        }
                    } catch (OperationCanceledException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("Account Not Removed");
                        builder.setMessage(username + " has could not be logged out");
                        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                LoginActivity.this.setResult(RESULT_CANCELED);
                                LoginActivity.this.finish();

                            }
                        });
                        builder.create().show();
                    } catch (IOException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("Account Not Removed");
                        builder.setMessage(username + " has could not be logged out");
                        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                LoginActivity.this.setResult(RESULT_CANCELED);
                                LoginActivity.this.finish();

                            }
                        });
                        builder.create().show();
                    } catch (AuthenticatorException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("Account Not Removed");
                        builder.setMessage(username + " has could not be logged out");
                        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                LoginActivity.this.setResult(RESULT_CANCELED);
                                LoginActivity.this.finish();

                            }
                        });
                        builder.create().show();
                    }
                }
            }, null);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setCancelable(false);
            builder.setTitle("Account Not Removed");
            builder.setMessage("No accounts were found");
            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    LoginActivity.this.setResult(RESULT_CANCELED);
                    LoginActivity.this.finish();

                }
            });
            builder.create().show();
        }
    }


    private void finishLogin(String username, String password, String firstname, String lastname, int classnumber, String userType) {
        final Account account = new Account(username, Consts.MMA_ACCOUNTTYPE);
        manager.addAccountExplicitly(account,password,null);
        manager.setAuthToken(account, Consts.MMA_ACCOUNTTYPE, password);
        manager.setUserData(account,"firstname",firstname);
        manager.setUserData(account,"lastname",firstname);
        manager.setUserData(account,"classnumber",Integer.toString(classnumber));
        manager.setUserData(account,AccountManager.KEY_ACCOUNT_NAME,username);

        final Intent answer = new Intent();
        answer.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        answer.putExtra("firstname",firstname);
        answer.putExtra("lastname",lastname);
        answer.putExtra("classnumber",classnumber);
        answer.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Consts.MMA_ACCOUNTTYPE);

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
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
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
}