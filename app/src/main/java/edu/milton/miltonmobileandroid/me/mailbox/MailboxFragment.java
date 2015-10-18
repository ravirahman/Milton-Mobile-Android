package edu.milton.miltonmobileandroid.me.mailbox;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.settings.account.AccountMethods;
import edu.milton.miltonmobileandroid.util.JsonHttp;

public class MailboxFragment extends Fragment {

    // Progress Dialog
    private ProgressDialog pDialog;
    private String mailbox, combo;
    private TextView mailboxText, comboText;
    private static String LOG_TAG = edu.milton.miltonmobileandroid.me.mailbox.MailboxFragment.class.getName();

    private static final String LOGIN_URL = "http://backend.ma1geek.org/me/mailbox/get";

    private static final String TAG_MAILBOX = "mailbox";
    private static final String TAG_COMBO = "combo";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.me_mailbox_fragment, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!AccountMethods.isLoggedIn(getActivity())) {
            AccountMethods.login(getActivity(), new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    if (!AccountMethods.isLoggedIn(getActivity())) {
                        return;
                    }
                    mailboxText = (TextView) getActivity().findViewById(R.id.me_mailbox_fragment_mailbox);
                    comboText = (TextView) getActivity().findViewById(R.id.me_mailbox_fragment_combo);
                    retrieveCombination();

                }
            });
            return;
        }

        mailboxText = (TextView) getActivity().findViewById(R.id.me_mailbox_fragment_mailbox);
        comboText = (TextView) getActivity().findViewById(R.id.me_mailbox_fragment_combo);
        retrieveCombination();
    }
    private void retrieveCombination() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String mbox = preferences.getString("mailbox",null);
        String combination = preferences.getString("combination",null);
        if (combination != null && mbox != null) {
            combo = combination;
            mailbox = mbox;
            updateText();
            return;
        }
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.string_Please_Wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        RequestParams params = new RequestParams();
        params.add("username",AccountMethods.getUsername(getActivity()));

        params.add("password",AccountMethods.getPassword(getActivity()));
        JsonHttp.request(LOGIN_URL,"GET",params,null,null,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                pDialog.dismiss();
                try {
                    mailbox = response.getString(TAG_MAILBOX);
                    combo = response.getString(TAG_COMBO);
                    preferences.edit().putString("combination",combo).putString("mailbox",mailbox).apply();
                    updateText();
                } catch (JSONException e) {
                    Log.v(LOG_TAG,"Error with JSON parsing");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.string_Check_Your_Network_Connection));
                builder.setMessage(getString(R.string.string_Please_try_again));
                builder.setPositiveButton(R.string.string_OK,new DialogInterface.OnClickListener() {
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

        mailboxText.setText(getString(R.string.string_Mailbox) + ": "+ mailbox);
        comboText.setText(getString(R.string.string_Combo) + ": " + combo);
    }

}