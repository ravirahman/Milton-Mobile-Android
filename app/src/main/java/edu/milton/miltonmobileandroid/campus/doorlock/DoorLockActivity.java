package edu.milton.miltonmobileandroid.campus.doorlock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
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
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
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
    private ArrayList<BluetoothGatt> gatts = new ArrayList<>();

    private final String LOG_TAG = this.getClass().toString();
    private final HashMap<String,String> lockMacName = new HashMap<>();

    private boolean isRunning() {
        return running;
    }
    private void setRunning(boolean running) {
        this.running = running;
    }
    private boolean running = false;


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

                mBluetoothAdapter.stopLeScan(callback);
                progressDialog = new ProgressDialog(DoorLockActivity.this,ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(true);
                final DoorLock lock = adapter.getItem(position);
                final BluetoothDevice device = lock.device;
                setRunning(true);

                final BluetoothGatt gatt = device.connectGatt(
                        DoorLockActivity.this,
                        true,
                        new BluetoothGattCallback() {
                            ArrayList<BluetoothGattDescriptor> descriptors = new ArrayList<>();
                            BluetoothGattCharacteristic writeChar = null;
                            BluetoothGattCharacteristic notifyChar = null;
                            private boolean servicesDiscovered = false;
                            private void setServicesDiscovered() {
                                servicesDiscovered = true;
                            }
                            private boolean getServicesDiscovered() {
                                return !servicesDiscovered;
                            }

                            private int descriptorsWritten = 0;

                            @Override
                            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                                super.onConnectionStateChange(gatt, status, newState);
                                if (newState == BluetoothGatt.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS && getServicesDiscovered() && isRunning()) {
                                    gatt.connect();
                                    gatt.discoverServices();
                                    gatt.getServices();
                                    setServicesDiscovered();
                                    return;
                                }
                                progressDialog.dismiss();
                                //setRunning(false);
                            }

                            @Override
                            public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
                                super.onServicesDiscovered(gatt, status);
                                if (!isRunning()) {
                                    return;
                                }
                                ArrayList<BluetoothGattService> services = (ArrayList<BluetoothGattService>) gatt.getServices();
                                outerloop:
                                for (BluetoothGattService service : services) {
                                    String uuid = service.getUuid().toString();
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

                                    if (!isRunning()) {
                                        return;
                                    }
                                    setRunning(false);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(DoorLockActivity.this);
                                    builder.setMessage("This is not a doorlock");
                                    builder.setTitle("Error");
                                    builder.setCancelable(true);
                                    builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();
                                        }
                                    });
                                    builder.create().show();
                                    gatt.disconnect();
                                    return;
                                }
                                for (BluetoothGattDescriptor descriptor : descriptors) {
                                    gatt.writeDescriptor(descriptor);
                                }
                            }

                            @Override
                            public void onCharacteristicChanged(final BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                                super.onCharacteristicChanged(gatt, characteristic);
                                if (!isRunning()) {
                                    return;
                                }
                                byte[] values = characteristic.getValue();
                                if (values[0] == 0x52 && values[1] == 0x45) { //we are getting back the request code
                                    //STEP 2
                                    //the first 10 bytes are teh sha1 hash
                                    //the last 8 bytes are the timestamp
                                    byte[] sha1 = new byte[10];
                                    System.arraycopy(values, 2, sha1, 0, sha1.length);
                                    byte[] timestamp = new byte[8];
                                    System.arraycopy(values, 12, timestamp, 0, 8);
                                    long timeLong = Hex.rebase(Hex.bytesToHex(timestamp), 16);
                                    /**
                                     * Ok, now do the http request
                                     * We have the
                                     *      code
                                     *      time
                                     *      username (AccountMethods.getUsername();)
                                     *      password
                                     *      lock mac
                                     */
                                    String code = Hex.bytesToHex(sha1);
                                    String time = Long.toString(timeLong);
                                    String mac = lock.mac;
                                    String username = AccountMethods.getUsername(DoorLockActivity.this);
                                    String password = AccountMethods.getPassword(DoorLockActivity.this);
                                    RequestParams params = new RequestParams();
                                    params.add("code",code);
                                    params.add("time",time);
                                    params.add("mac",mac);
                                    params.add("username",username);
                                    params.add("password",password);
                                    AsyncHttpClient client = new AsyncHttpClient();

                                    client.post(DoorLockActivity.this,"http://backend.ma1geek.org/campus/doorlock/unlock",params,new TextHttpResponseHandler(){
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                            if (!isRunning()) {
                                                return;
                                            }
                                            try{
                                                final JSONObject response = new JSONObject(responseString);
                                                boolean success = response.getBoolean("success");
                                                if (!success) {
                                                    final String message = response.getString("message");
                                                    DoorLockActivity.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressDialog.dismiss();
                                                            if (!isRunning())  {
                                                                return;
                                                            }
                                                            setRunning(false);
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(DoorLockActivity.this);
                                                            builder.setTitle("There was an error");
                                                            builder.setMessage(message);
                                                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                            builder.create().show();
                                                        }
                                                    });
                                                    gatt.setCharacteristicNotification(notifyChar, false);
                                                    gatt.disconnect();
                                                    return;
                                                }
                                                String token = response.getString("token");
                                                byte[] tokenAsBytes = Hex.HexStringToByteArray(token);
                                                long time = response.getLong("time");
                                                byte[] timeBytes = ByteBuffer.allocate(8).putLong(time).array();
                                                //now bundle these for ble
                                                byte[] answer = new byte[20];
                                                answer[0] = 0x53;
                                                answer[1] = 0x55;
                                                //bytes 2-11 (inclusive) will be for the token
                                                System.arraycopy(tokenAsBytes, 0, answer, 2, 10);
                                                //bytes 12-19 (inclusive) will be for the time
                                                System.arraycopy(timeBytes, 0, answer, 12, 8);
                                                writeChar.setValue(answer);
                                                gatt.writeCharacteristic(writeChar);
                                            }
                                            catch(JSONException e) {
                                                Log.v(LOG_TAG,e.toString());
                                            }

                                        }

                                    });
                                    return;
                                }
                                if (values[0] == 0x53 && values[1] == 0x55) { //the door is open
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            if (!isRunning()) {
                                                return;
                                            }
                                            setRunning(false);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(DoorLockActivity.this);
                                            builder.setTitle("The door is open!");
                                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.create().show();
                                        }
                                    });
                                    gatt.setCharacteristicNotification(notifyChar,false);
                                    gatt.disconnect();
                                    return;
                                }
                                if (values[0] == 0x45 && values[1] == 0x52) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            if (!isRunning()) {
                                                return;
                                            }
                                            setRunning(false);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(DoorLockActivity.this);
                                            builder.setTitle("There was an error");
                                            builder.setMessage("Please try again");
                                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.create().show();
                                        }
                                    });
                                    gatt.setCharacteristicNotification(notifyChar,false);
                                    gatt.disconnect();
                                    return;
                                }
                            }

                            @Override
                            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                                super.onDescriptorWrite(gatt, descriptor, status);
                                if (!isRunning()) {
                                    return;
                                }
                                descriptorsWritten++;
                                if (descriptors.size() == descriptorsWritten) { //no more descriptors to write
                                    //IMPORTANT STEP 1 Request Code

                                    gatt.setCharacteristicNotification(notifyChar,true);
                                    //ok, now write the three bytes to the read. These spell REQ
                                    //gatt.writeCharacteristic(notifyChar);
                                    long tsLong = System.currentTimeMillis()/1000;
                                    byte[] timeBytes = ByteBuffer.allocate(8).putLong(tsLong).array(); //length should be 8
                                    byte[] answer = new byte[10];
                                    answer[0] = 0x52; //R
                                    answer[1] = 0x45; //E
                                    System.arraycopy(timeBytes, 0, answer, 2, timeBytes.length);
                                    writeChar.setValue(answer);
                                    gatt.writeCharacteristic(writeChar);
                                }
                            }
                        }
                );
                gatts.add(gatt);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        gatt.disconnect();
                        dialog.dismiss();
                    }
                });
                progressDialog.show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //if the progress dialog is still showing after 10 seconds, dismiss it and show that there was an error
                        if (isRunning()) {
                            setRunning(false);
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(DoorLockActivity.this);
                            builder.setTitle("There was an error");
                            builder.setMessage("Please try again");
                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                        gatt.disconnect();
                    }
                }, 10000);
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
            builder.setTitle("Feature Not Available");
            builder.setMessage("To use this feature, you must be running Android 4.3 or Higher");
            builder.setNeutralButton("Go Back",new DialogInterface.OnClickListener() {
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
            builder.setTitle("Feature Not Available");
            builder.setMessage("To use this feature, your device must have Bluetooth LE 4.0 or Higher");
            builder.setNeutralButton("Go Back",new DialogInterface.OnClickListener() {
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
        findDoorLocks();
    }

    private void enableBle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Enable BLE");
        builder.setMessage("To use this feature, please enable Bluetooth Low Energy");
        builder.setNegativeButton("No Thanks, Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Enable BLE (follow the prompts)",new DialogInterface.OnClickListener() {
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
                mBluetoothAdapter.stopLeScan(callback);
                mBluetoothAdapter.startLeScan(callback);
            }

            @Override
            public void onFailure(int status, Header[] headers, byte[] bytes, Throwable throwable) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DoorLockActivity.this);
                builder.setTitle("Network Connection Required");
                builder.setMessage("To use this feature, please check your network connection.");
                builder.setNeutralButton("OK",new DialogInterface.OnClickListener() {
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
                setRunning(false);
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
            View rootView = inflater.inflate(R.layout.campus_doorlock_fragment, container, false);
            return rootView;
        }
    }

}