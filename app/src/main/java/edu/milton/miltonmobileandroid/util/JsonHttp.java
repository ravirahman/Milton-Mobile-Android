package edu.milton.miltonmobileandroid.util;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.client.CookieStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JsonHttp {
    public static String LOG_TAG = JsonHttp.class.getName();
    //full client
    public static void request(
            final String url,
            final String method,
            final RequestParams params,
            final CookieStore cookies,
            final JSONArray headers,
            final JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        if (cookies != null) {
            client.setCookieStore(cookies);
        }
        //client.setEnableRedirects(true);
        if (headers != null) {
            for (int i = 0; i < headers.length(); i++) {
                try {
                    JSONArray hArray = headers.getJSONArray(i);
                    client.addHeader((String) hArray.get(0),(String) hArray.get(1));

                } catch (JSONException e) {
                    try {
                        JSONObject hObject = headers.getJSONObject(i);
                        for (Iterator<String> iter = hObject.keys(); hObject.keys().hasNext(); ) {
                            String key = iter.next();
                            client.addHeader(key, (String) headers.getJSONObject(i).get(key));
                        }
                    }
                    catch (JSONException x) {
                        Log.v(LOG_TAG,"Bad JSON Headers");
                    }
                }
            }
        }

        if (method.equalsIgnoreCase("POST")) {
            client.post(url,handler);
            return;
        }
        if (method.equalsIgnoreCase("PUT")) {
            client.put(url,handler);
            return;
        }
        if (method.equalsIgnoreCase("DELETE")) {
            client.delete(url,handler);
            return;
        }
        //if not any of the above, do a get request
        client.get(url,handler);

    }

    //basic client
    public static void request(final String url, final JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        request(url, "GET", null, null, null, handler);
    }
}
