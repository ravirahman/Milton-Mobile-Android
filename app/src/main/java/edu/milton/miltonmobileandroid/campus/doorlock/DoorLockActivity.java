package edu.milton.miltonmobileandroid.campus.doorlock;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import edu.milton.miltonmobileandroid.R;
import edu.milton.miltonmobileandroid.settings.account.AccountMethods;
import edu.milton.miltonmobileandroid.util.Hex;
import edu.milton.miltonmobileandroid.util.JsonHttp;

public class DoorLockActivity extends Activity {
    private boolean hasBle = false;
    private boolean hasMinOs = false;
    private boolean bleEnabled = false;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private DoorLockListAdapter adapter;
    private BluetoothGatt connectedGatt;

    private final String LOG_TAG = this.getClass().toString();
    private final HashMap<String,String> lockMacName = new HashMap<String,String>();

    private boolean servicesDiscovered = false;
    private void setServicesDiscovered() {
        servicesDiscovered = true;
    }
    private boolean getServicesDiscovered() {
        return !servicesDiscovered;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campus_doorlock_activity);
        adapter = new DoorLockListAdapter(this);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ListView doorLockListView = (ListView) findViewById(R.id.campus_doorlock_fragment_listview);

        doorLockListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            ProgressDialog progressDialog;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (connectedGatt != null) {
                    Log.v(LOG_TAG,"trying to run when gatt is not null");
                    return;
                }
                mBluetoothAdapter.stopLeScan(callback);
                progressDialog = new ProgressDialog(DoorLockActivity.this,ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage(getResources().getString(R.string.string_Please_Wait));
                progressDialog.setCancelable(false);
                progressDialog.show();
                final DoorLock lock = adapter.getItem(position);
                final BluetoothDevice device = lock.device;


                connectedGatt = device.connectGatt(
                        DoorLockActivity.this,
                        true,
                        new BluetoothGattCallback() {
                            ArrayList<BluetoothGattDescriptor> descriptors = new ArrayList<BluetoothGattDescriptor>();
                            BluetoothGattCharacteristic writeChar = null;
                            BluetoothGattCharacteristic notifyChar = null;


                            private int descriptorsWritten = 0;

                            @Override
                            public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                                super.onConnectionStateChange(gatt, status, newState);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (newState == BluetoothGatt.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS && getServicesDiscovered()) {
                                            //gatt.connect();
                                            gatt.discoverServices();
                                            //gatt.getServices();
                                            setServicesDiscovered();
                                            return;
                                        }
                                        if (newState == BluetoothGatt.STATE_DISCONNECTED && connectedGatt != null) {
                                            connectedGatt = null;
                                            servicesDiscovered = false;
                                        }
                                        if (status == BluetoothGatt.GATT_FAILURE) { //unknown reason, so let's restart the app
                                            Intent mStartActivity = new Intent(DoorLockActivity.this, DoorLockActivity.class);
                                            int mPendingIntentId = 123456;
                                            PendingIntent mPendingIntent = PendingIntent.getActivity(DoorLockActivity.this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                            AlarmManager mgr = (AlarmManager) DoorLockActivity.this.getSystemService(Context.ALARM_SERVICE);
                                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                            System.exit(0);
                                            return;
                                        }
                                        progressDialog.dismiss(); //gatt was disconnected
                                    }
                                });
                            }

                            @Override
                            public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
                                super.onServicesDiscovered(gatt, status);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayList<BluetoothGattService> services = (ArrayList<BluetoothGattService>) gatt.getServices();
                                        outerloop:
                                        for (BluetoothGattService service : services) {
                                            String uuid = service.getUuid().toString();
                                            Log.v(LOG_TAG,"The UUID is " + uuid);
                                            if (uuid.equalsIgnoreCase("713d0000-503e-4c75-ba94-3148f18d941e")) { //this is the service we want
                                                ArrayList<BluetoothGattCharacteristic> characteristics = (ArrayList<BluetoothGattCharacteristic>) service.getCharacteristics();
                                                for (BluetoothGattCharacteristic characteristic : characteristics) {
                                                    if (characteristic.getUuid().toString().equalsIgnoreCase("713d0003-503e-4c75-ba94-3148f18d941e")) { //this is the char we use to write
                                                        writeChar = characteristic;
                                                    }
                                                    if (characteristic.getUuid().toString().equalsIgnoreCase("713d0002-503e-4c75-ba94-3148f18d941e")) { //this is the char we are notified on
                                                        notifyChar = characteristic;
                                                        ArrayList<BluetoothGattDescriptor> tdescriptors = (ArrayList<BluetoothGattDescriptor>) notifyChar.getDescriptors();
                                                        for (BluetoothGattDescriptor descriptor : tdescriptors) {
                                                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                                            descriptors.add(descriptor);
                                                        }
                                                    }
                                                    if (writeChar != null && notifyChar != null) {
                                                        break outerloop;
                                                    }
                                                }
                                            }

                                        }
                                        if (writeChar == null || notifyChar == null) {
                                            progressDialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(DoorLockActivity.this);
                                            builder.setMessage(R.string.campus_doorlock_not_a_doorlock);
                                            builder.setTitle(getString(R.string.string_Error));
                                            builder.setCancelable(true);
                                            builder.setNegativeButton(getString(R.string.string_Cancel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.create().show();
                                            gatt.disconnect();
                                            connectedGatt = null;
                                            servicesDiscovered = false;
                                            return;
                                        }
                                        for (BluetoothGattDescriptor descriptor : descriptors) {
                                            gatt.writeDescriptor(descriptor);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                                super.onCharacteristicChanged(gatt, characteristic);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        byte[] values = characteristic.getValue();
                                        if (values[0] == 0x52 && values[1] == 0x45) { //we are getting back the request code
                                            //bytes 3 to 10 inclusive are the nonce. Send that to the server for the signature
                                            byte[] nonceB = new byte[4];
                                            System.arraycopy(values, 2, nonceB, 0, 4);

                                            long nonce  = Hex.rebase(Hex.bytesToHex(nonceB), 16);

                                            /**
                                             * Ok, now do the http request
                                             * We have the
                                             *      code
                                             *      username (AccountMethods.getUsername();)
                                             *      password
                                             *      lock mac
                                             */
                                            String code = Long.toString(nonce);
                                            String mac = lock.mac;
                                            String username = AccountMethods.getUsername(DoorLockActivity.this);
                                            String password = AccountMethods.getPassword(DoorLockActivity.this);
                                            RequestParams params = new RequestParams();
                                            params.add("code",code);
                                            params.add("mac",mac);
                                            params.add("username",username);
                                            params.add("password", password);
                                            AsyncHttpClient client = new AsyncHttpClient();
                                            client.setUserAgent(JsonHttp.USER_AGENT);

                                            client.post(DoorLockActivity.this,"http://backend.ma1geek.org/campus/doorlock/unlock",params,new JsonHttpResponseHandler(){
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                                                    DoorLockActivity.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                boolean success = response.getBoolean("success");
                                                                if (!success) {
                                                                    final String message = response.getString("message");
                                                                    progressDialog.dismiss();
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(DoorLockActivity.this);
                                                                    builder.setTitle(getString(R.string.string_Error));
                                                                    builder.setMessage(message);
                                                                    builder.setNeutralButton(getString(R.string.string_OK), new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    });
                                                                    builder.create().show();
                                                                    gatt.setCharacteristicNotification(notifyChar, false);
                                                                    gatt.disconnect();
                                                                    connectedGatt = null;
                                                                    servicesDiscovered = false;
                                                                    return;
                                                                }
                                                                String token = response.getString("token");
                                                                byte[] tokenAsBytes = Hex.HexStringToByteArray(token);

                                                                byte[] answer = new byte[20];
                                                                answer[0] = 0x53;
                                                                answer[1] = 0x55;
                                                                //bytes 2-11 (inclusive) will be for the token
                                                                System.arraycopy(tokenAsBytes, 0, answer, 2, 18);
                                                                writeChar.setValue(answer);
                                                                gatt.writeCharacteristic(writeChar);
                                                            } catch (JSONException e) {
                                                                Log.v(LOG_TAG, e.toString());
                                                            }
                                                        }
                                                    });
                                                }

                                            });
                                            return;
                                        }
                                        if (values[0] == 0x53 && values[1] == 0x55) { //the door is open
                                            progressDialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(DoorLockActivity.this);
                                            builder.setTitle(getString(R.string.campus_doorlock_unlocked));
                                            builder.setNeutralButton(getString(R.string.string_OK), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.create().show();
                                            gatt.setCharacteristicNotification(notifyChar, false);
                                            gatt.disconnect();
                                            connectedGatt = null;
                                            servicesDiscovered = false;
                                            return;
                                        }
                                        if (values[0] == 0x45 && values[1] == 0x52) {
                                            progressDialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(DoorLockActivity.this);
                                            builder.setTitle(getString(R.string.string_Error));
                                            builder.setMessage(getString(R.string.string_Please_try_again));
                                            builder.setNeutralButton(getString(R.string.string_OK), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.create().show();
                                            gatt.setCharacteristicNotification(notifyChar, false);
                                            gatt.disconnect();
                                            connectedGatt = null;
                                            servicesDiscovered = false;
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onDescriptorWrite(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
                                super.onDescriptorWrite(gatt, descriptor, status);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        descriptorsWritten++;
                                        if (descriptors.size() == descriptorsWritten) { //no more descriptors to write
                                            //IMPORTANT STEP 1 Request Code
                                            gatt.setCharacteristicNotification(notifyChar,true);
                                            //ok, now write the three bytes to the read. These spell REQ
                                            //gatt.writeCharacteristic(notifyChar);
                                            byte[] answer = new byte[2];
                                            answer[0] = 0x52; //R
                                            answer[1] = 0x45; //E
                                            writeChar.setValue(answer);
                                            gatt.writeCharacteristic(writeChar);
                                        }
                                    }
                                });
                            }
                        }
                );

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (connectedGatt == null) {
                                    servicesDiscovered = false;
                                    return;
                                }
                                //if the progress dialog is still showing after 10 seconds, dismiss it and show that there was an error
                                progressDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(DoorLockActivity.this);
                                builder.setTitle(getString(R.string.string_Error));
                                builder.setMessage(getString(R.string.string_Please_try_again));
                                builder.setNeutralButton(getString(R.string.string_OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                connectedGatt.disconnect();
                                servicesDiscovered = false;
                                connectedGatt = null;
                            }
                        });
                    }
                }, 60000);
            }
        });
        doorLockListView.setAdapter(adapter);
        //  COPYRIGHT NOTICE
        // ALL CODE In the "DoorLock" package (package edu.milton.miltonmobileandroid.campus.doorlock) and referenced layouts/other assets are (c)
        // Copyright Ravi Rahman, 2015 (except code written by others and cited), as an individual, and are not owned by Milton Academy, nor may these be used for any other purpose or reproduced
        // (in whole, part, or even an idea or method) outside of this application.
        // without Ravi Rahman's explicit approval and permission.
        // Nor may any individual, or the school, profit from these ideas, without Ravi Rahman's explicit approval and permission

        //TODO check the savedInstanceState to see if the doorlocks are already there - so it doesn't have to refresh on screen rotate

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            hasMinOs = true;
        }
        if (!hasMinOs) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.string_Incompatible_Device));
            builder.setMessage(getString(R.string.campus_doorlock_no_ble_api));
            builder.setNeutralButton(getString(R.string.string_OK),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            return;
        }

        //from https://developer.android.com/guide/topics/connectivity/bluetooth-le.html
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            hasBle = true;
        }
        if (!hasBle) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.string_Incompatible_Device));
            builder.setMessage(getString(R.string.campus_doorlock_no_ble_hardware));
            builder.setNeutralButton(getString(R.string.string_OK),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            return;
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            bleEnabled = true;
        }
        if (!bleEnabled) {
            enableBle();
            return;
        }
        if (!AccountMethods.isLoggedIn(this)) {
            Log.v(LOG_TAG,"Not logged in");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AccountMethods.login(DoorLockActivity.this, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                if (!AccountMethods.isLoggedIn(DoorLockActivity.this)) {
                    finish();
                    return;
                }
                findDoorLocks();
                }
            });
            return;
        }

        findDoorLocks();
    }

    private void enableBle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.string_Please_Enable_BLE));
        builder.setMessage(getString(R.string.campus_doorlock_no_ble_enabled));
        builder.setNegativeButton(getString(R.string.string_Back), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton(R.string.string_OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        });
    }

    BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            if (lockMacName.containsKey(device.getAddress().replace(":",""))) { //we only want to show actual door locks, not other ble devices that might be floating around
                runOnUiThread(new Runnable() { //not sure why it has to be runOnUiThread
                    @Override
                    public void run() {
                        adapter.addLockToList(new DoorLock(device));
                    }
                });
            }
        }
    };

    private void findDoorLocks() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.string_Please_Wait));
        dialog.show();
        dialog.setCancelable(false);
        JsonHttp.request("http://backend.ma1geek.org/campus/doorlock/list",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                lockMacName.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject lockO = response.getJSONObject(i);
                        String mac = lockO.getString("mac");
                        String name = lockO.getString("name");
                        lockMacName.put(mac, name);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.removeAll();
                dialog.hide();
                if (connectedGatt != null) {
                    connectedGatt.disconnect();
                    servicesDiscovered = false;
                    connectedGatt = null;
                }
                mBluetoothAdapter.cancelDiscovery();
                mBluetoothAdapter.stopLeScan(callback);
                mBluetoothAdapter.startLeScan(callback);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.String responseString, java.lang.Throwable throwable) {
                dialog.hide();
                AlertDialog.Builder builder = new AlertDialog.Builder(DoorLockActivity.this);
                builder.setTitle(getString(R.string.string_Check_Your_Network_Connection));
                builder.setMessage(getString(R.string.string_Please_try_again));
                builder.setNeutralButton(R.string.string_OK,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        },DoorLockActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                bleEnabled = true;
                findDoorLocks();
            }
            if (!bleEnabled) {
                enableBle();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.campus_doorlock_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                if (connectedGatt != null) {
                    connectedGatt.disconnect();
                    servicesDiscovered = false;
                    connectedGatt = null;
                }
                finish();
                break;
            case R.id.campus_doorlock_activity_refresh:
                findDoorLocks();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public static class DoorLockFragment extends Fragment {

        public DoorLockFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.campus_doorlock_fragment, container, false);
        }
    }

}