package sliitassisme.test2.FirstTime;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import sliitassisme.test2.BluetoothLEService;
import sliitassisme.test2.CommonActivity;
import sliitassisme.test2.R;
import sliitassisme.test2.database.Devices;

//import com.Sliit.assistme.dashboard.DashboardActivity;


public class FirstTimeDevicesActivity extends CommonActivity implements FirstTimeDevicesFragment.OnDevicesListener, FirstTimeDeviceAlertDialogFragment.OnConfirmAlertDialogListener  {

    public static final String TAG = FirstTimeDevicesActivity.class.toString();

    private BluetoothLEService service; //for servicedicoverd in bleclass

    private final static int REQUEST_ENABLE_BT = 1;

    private static final long SCAN_PERIOD = 10000; // 10 seconds

    //create ble class instance
    //Interface for monitoring the state of an application service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            if (iBinder instanceof BluetoothLEService.BackgroundBluetoothLEBinder) {
                service = ((BluetoothLEService.BackgroundBluetoothLEBinder) iBinder).service();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            Log.d(BluetoothLEService.TAG, "onServiceDisconnected()");
        }
    };

    private final Random random = new Random();

    private final FirstTimeDevicesFragment devicesFragment = FirstTimeDevicesFragment.instance();

    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private Runnable stopScan;

    private final Map<String, String> devices = new HashMap<>();//to hold scand devices while scaning

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        //RSSI Used as an optional short extra field in ACTION_FOUND intents

        //Callback reporting an LE device found during a device scan initiated by the
        // startLeScan(BluetoothAdapter.LeScanCallback) function.
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
        {
            final String address = device.getAddress();
            final String name = device.getName();

            Log.d(TAG, "device " + name + " with address " + address + " found");
            if (!Devices.containsDevice(FirstTimeDevicesActivity.this, address)) {//check tag store in database
               devices.put((name == null) ? address : name, address);
            }
        }
    };

    //insert data after selecting divice
    private void selectDevice(String name, String address)
    {
        Devices.insert(this, name, address);
        Devices.setEnabled(this, address, true);
        service.connect(address);
        devicesFragment.refresh();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mHandler = new Handler();

        showDevices();

        //survice not running strt again
        if (!isMyServiceRunning(BluetoothLEService.class)) {
            startService(new Intent(this, BluetoothLEService.class));
        }
    }

    //show device in new fragment
    private void showDevices()
    {
        getFragmentManager().beginTransaction().replace(R.id.container, devicesFragment).commit();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // detect Bluetooth enabled
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    //expext result as enable blouetooth
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_ENABLE_BT) {
            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                finish();
            }
        }
    }



    @Override
    public void onScanStart()
    {
        devices.clear();
        stopScan = new Runnable() {
            @Override
            public void run()
            {
                mHandler.removeCallbacks(stopScan);
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                setRefreshing(false);

                if (!devices.isEmpty()) {
                    displayListScannedDevices();
                } else {
                    devicesFragment.snack(getString(R.string.beacon_not_found));//if not found set snac bar
                }
            }
        };
        mHandler.postDelayed(stopScan, SCAN_PERIOD);//scan period 10 s
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        setRefreshing(true);
    }


    //when app start bind with bleclss
    @Override
    public void onDevicesStarted()
    {
        bindService(new Intent(this, BluetoothLEService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    //stop con to all BLE's
    @Override
    public void onDevicesStopped()
    {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mHandler.removeCallbacks(stopScan);

        unbindService(serviceConnection);
    }


    //select Device
    @Override
    public void onDevice(String name, String address) {
        final Intent intent = new Intent(this, FirstTimeDevicesActivity.class);// need to edit
        intent.putExtra(Devices.ADDRESS, address);
        intent.putExtra(Devices.NAME, name);
        startActivity(intent);
    }


    @Override
    public void onChangeDeviceName(String name, String address, boolean checked)
    {
        FirstTimeDeviceAlertDialogFragment.instance(name, address, checked).show(getFragmentManager(), "dialog");
    }



//disable or enable checkbox
    @Override
    public void onDeviceStateChanged(String address, boolean enabled)
    {
        if (service != null) {
            Devices.setEnabled(this, address, enabled);

            if (enabled) {
                service.connect(address);
            } else {
                //AlertDialogFragment.instance(R.string.app_name, R.string.link_loss_disabled).show(getFragmentManager(), null);
                service.disconnect(address);
            }
        }
    }

    private void displayListScannedDevices()
    {
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.mipmap.ic_launcher);
        builderSingle.setTitle(R.string.select_device);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(devices.keySet());
        builderSingle.setSingleChoiceItems(arrayAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int witch)
            {
                final String device = arrayAdapter.getItem(witch);
                selectDevice(device, devices.get(device));//selectdivicemehod
                dialog.dismiss();
            }
        });
        builderSingle.setNegativeButton(android.R.string.cancel,//cansl btn in popup
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
        builderSingle.show();
    }

    //update divice when name change and refresh fragment
    @Override
    public void doPositiveClick(String address, String name)
    {
        Devices.updateDevice(this, address, name);
        devicesFragment.refresh();
    }

    @Override
    public void doNegativeClick()
    {
    }

    //imidiate alert when click dialog box
    @Override
    public void doAlertClick(String address, int alertType)
    {
        this.service.immediateAlert(address, alertType);
    }

    //delete tag
    @Override
    public void doDeleteClick(String address)
    {
        service.remove(address);
        Devices.removeDevice(this, address);
        devicesFragment.refresh();
    }


    //pass class name and check that class is runing
    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void clickDone(View view) {
        Intent intent1=new Intent(getApplicationContext(),FirstTimeenterDailyScedule.class);
        startActivity(intent1);
    }
}
