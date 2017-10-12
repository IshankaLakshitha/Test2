package sliitassisme.test2;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.UUID;

import sliitassisme.test2.database.Devices;

//import sliitassisme.test2.FirstTimeDevicesActivity;


public class BluetoothLEService extends Service {

    public static final int NO_ALERT = 0x00;
    public static final int MEDIUM_ALERT = 0x01;
    public static final int HIGH_ALERT = 0x02;//start alert

    public static final String IMMEDIATE_ALERT_AVAILABLE = "IMMEDIATE_ALERT_AVAILABLE";
    public static final String BATTERY_LEVEL = "BATTERY_LEVEL";
    public static final String GATT_CONNECTED = "GATT_CONNECTED";
    public static final String SERVICES_DISCOVERED = "SERVICES_DISCOVERED";
    public static final String RSSI_RECEIVED = "RSSI_RECEIVED";

    public static final UUID IMMEDIATE_ALERT_SERVICE = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
    public static final UUID FIND_ME_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public static final UUID LINK_LOSS_SERVICE = UUID.fromString("00001803-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID ALERT_LEVEL_CHARACTERISTIC = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");
    public static final UUID FIND_ME_CHARACTERISTIC = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

    public static final String TAG = BluetoothLEService.class.toString();
    public static final String ACTION_PREFIX = "net.sylvek.itracing2.action.";
    public static final long TRACK_REMOTE_RSSI_DELAY_MILLIS = 5000L;
    public static final int FOREGROUND_ID = 1664;
    public static final String BROADCAST_INTENT_ACTION = "BROADCAST_INTENT";

    private BluetoothDevice mDevice;

    private HashMap<String, BluetoothGatt> bluetoothGatt = new HashMap<>();

    private BluetoothGattService immediateAlertService;

    private BluetoothGattService linkLossService;

    //private BluetoothGattCharacteristic batteryCharacteristic;

    private BluetoothGattCharacteristic buttonCharacteristic;

    private long lastChange;

    private UUID lastUuid;

    private String lastAddress;

    private Runnable r;

    private Handler handler = new Handler();

    private Runnable trackRemoteRssi = null;

    private class CustomBluetoothGattCallback extends BluetoothGattCallback {

        private final String address;

        CustomBluetoothGattCallback(final String address) {
            this.address = address;
        }

        //Public API for the Bluetooth GATT Profile.
        //This class provides Bluetooth GATT functionality to enable communication with Bluetooth Smart or Smart Ready devices.

        //BluetoothGattCallback abstract class is used to implement BluetoothGatt callbacks.
        //Callback indicating when GATT client has connected/disconnected to/from a remote GATT server.
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "onConnectionStateChange() address: " + address + " status => " + status);
            if (BluetoothGatt.GATT_SUCCESS == status) {
                Log.d(TAG, "onConnectionStateChange() address: " + address + " newState => " + newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    broadcaster.sendBroadcast(new Intent(GATT_CONNECTED));
                    gatt.discoverServices();

                }

                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    gatt.close();
                }
            }

            //final boolean actionOnPowerOff = Preferences.isActionOnPowerOff(BluetoothLEService.this, this.address);
            /*if (actionOnPowerOff || status == 8) {
                Log.d(TAG, "onConnectionStateChange() address: " + address + " newState => " + newState);
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    for (String action : Preferences.getActionOutOfBand(getApplicationContext(), this.address)) {
                        sendAction(Preferences.Source.out_of_range, action);
                    }
                    enablePeerDeviceNotifyMe(gatt, false);
                }
            }*/
        }

        //read rssi value
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            final Intent rssiIntent = new Intent(RSSI_RECEIVED);
            rssiIntent.putExtra(RSSI_RECEIVED, rssi);
            broadcaster.sendBroadcast(rssiIntent);
        }

        /*start service when tag clicked*/
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered()");

            launchTrackingRemoteRssi(gatt);
            //status 0
            broadcaster.sendBroadcast(new Intent(SERVICES_DISCOVERED));
            if (BluetoothGatt.GATT_SUCCESS == status) {

                for (BluetoothGattService service : gatt.getServices()) {

                    Log.d(TAG, "service discovered: " + service.getUuid());

                    if (IMMEDIATE_ALERT_SERVICE.equals(service.getUuid())) {
                        immediateAlertService = service;
                        broadcaster.sendBroadcast(new Intent(IMMEDIATE_ALERT_AVAILABLE));
                        gatt.readCharacteristic(getCharacteristic(gatt, IMMEDIATE_ALERT_SERVICE, ALERT_LEVEL_CHARACTERISTIC));
                        setCharacteristicNotification(gatt, immediateAlertService.getCharacteristics().get(0), true);
                    }

                    /*if (BATTERY_SERVICE.equals(service.getUuid())) {
                        batteryCharacteristic = service.getCharacteristics().get(0);
                        gatt.readCharacteristic(batteryCharacteristic);
                    }*/

                    if (FIND_ME_SERVICE.equals(service.getUuid())) {
                        if (!service.getCharacteristics().isEmpty()) {
                            buttonCharacteristic = service.getCharacteristics().get(0);
                            setCharacteristicNotification(gatt, buttonCharacteristic, true);
                            Log.v("aa","gattIsh");
                        }
                    }

                    if (LINK_LOSS_SERVICE.equals(service.getUuid())) {
                        linkLossService = service;
                    }
                }
                enablePeerDeviceNotifyMe(gatt, true);
            }
        }


        private void launchTrackingRemoteRssi(final BluetoothGatt gatt) {
            if (trackRemoteRssi != null) {
                handler.removeCallbacks(trackRemoteRssi);
            }

            trackRemoteRssi = new Runnable() {
                @Override
                public void run() {
                    gatt.readRemoteRssi();
                    handler.postDelayed(this, TRACK_REMOTE_RSSI_DELAY_MILLIS);
                }
            };
            handler.post(trackRemoteRssi);
        }



       //eporting the result of a characteristic ble Btry level not used yet
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead()");
            if (characteristic.getValue() != null && characteristic.getValue().length > 0) {
                final Intent batteryLevel = new Intent(BATTERY_LEVEL);
                final byte level = characteristic.getValue()[0];
                batteryLevel.putExtra(BATTERY_LEVEL, Integer.valueOf(level));
                broadcaster.sendBroadcast(batteryLevel);
            }
        }
    }

    private void setCharacteristicNotification(BluetoothGatt bluetoothgatt, BluetoothGattCharacteristic bluetoothgattcharacteristic, boolean flag) {
        bluetoothgatt.setCharacteristicNotification(bluetoothgattcharacteristic, flag);
        BluetoothGattDescriptor descriptor = bluetoothgattcharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothgatt.writeDescriptor(descriptor);
        }
    }

    //conected device notifivation
    public void enablePeerDeviceNotifyMe(BluetoothGatt bluetoothgatt, boolean flag) {
        BluetoothGattCharacteristic bluetoothgattcharacteristic = getCharacteristic(bluetoothgatt, FIND_ME_SERVICE, FIND_ME_CHARACTERISTIC);
        if (bluetoothgattcharacteristic != null && (bluetoothgattcharacteristic.getProperties() | 0x10) > 0) {
            setCharacteristicNotification(bluetoothgatt, bluetoothgattcharacteristic, flag);
        }
    }

    private BluetoothGattCharacteristic getCharacteristic(BluetoothGatt bluetoothgatt, UUID serviceUuid, UUID characteristicUuid) {
        if (bluetoothgatt != null) {
            BluetoothGattService service = bluetoothgatt.getService(serviceUuid);
            if (service != null) {
                return service.getCharacteristic(characteristicUuid);
            }
        }

        return null;
    }

    public class BackgroundBluetoothLEBinder extends Binder {
        public BluetoothLEService service() {
            return BluetoothLEService.this;
        }
    }

    //start bleclass service and return bleclass
    private BackgroundBluetoothLEBinder myBinder = new BackgroundBluetoothLEBinder();

    private LocalBroadcastManager broadcaster;


    //binf with bleclass
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    //run bleclss background. not used yet
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setForegroundEnabled(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("action_foreground", true));
        connect();
        return START_STICKY;
    }

    //set notifivation
    public void setForegroundEnabled(boolean enabled) {
        if (enabled) {

        } else {
            stopForeground(true);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        broadcaster = LocalBroadcastManager.getInstance(this);
    }


    //disconect with divice
    @Override
    public void onDestroy() {
        if (trackRemoteRssi != null) {
            handler.removeCallbacks(trackRemoteRssi);
        }

        disconnect();

        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    //disconect one by one
    public synchronized void disconnect() {
        final Cursor cursor = Devices.findDevices(this);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                final String address = cursor.getString(0);
                if (Devices.isEnabled(this, address)) {
                    Log.d(TAG, "disconnect() - to device " + address);
                    if (bluetoothGatt.get(address) != null) {
                        bluetoothGatt.get(address).disconnect();
                    }
                    bluetoothGatt.remove(address);
                }
            } while (cursor.moveToNext());
        }
    }

    public void setLinkLossNotificationLevel(String address, int alertType) {
        Log.d(TAG, "setLinkLossNotificationLevel() - the device " + address);
        if (bluetoothGatt.get(address) == null || linkLossService == null || linkLossService.getCharacteristics() == null || linkLossService.getCharacteristics().size() == 0) {
            somethingGoesWrong();
            return;
        }
        final BluetoothGattCharacteristic characteristic = linkLossService.getCharacteristics().get(0);
        characteristic.setValue(alertType, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        bluetoothGatt.get(address).writeCharacteristic(characteristic);
    }

    //imidiate alert
    public void immediateAlert(String address, int alertType) {
        //Log.d(TAG, "immediateAlert() - the device " + address);
        /*if (bluetoothGatt.get(address) == null || immediateAlertService == null || immediateAlertService.getCharacteristics() == null || immediateAlertService.getCharacteristics().size() == 0) {
            somethingGoesWrong();
            return;
        }*/
        final BluetoothGattCharacteristic characteristic = immediateAlertService.getCharacteristics().get(0);
        characteristic.setValue(alertType, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        bluetoothGatt.get(address).writeCharacteristic(characteristic);
        Log.v("dd","ringer");

    }

    private synchronized void somethingGoesWrong() {
        Toast.makeText(this, R.string.something_goes_wrong, Toast.LENGTH_LONG).show();
    }

    //conect devices
    public synchronized void connect() {
        final Cursor cursor = Devices.findDevices(this);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                final String address = cursor.getString(0);
                if (Devices.isEnabled(this, address)) {
                    this.connect(address);
                }
            } while (cursor.moveToNext());
        }
    }

    public synchronized void connect(final String address) {
        if (!bluetoothGatt.containsKey(address) || bluetoothGatt.get(address) == null) {
            Log.d(TAG, "connect() - (new link) to device " + address);
            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
            bluetoothGatt.put(address, mDevice.connectGatt(this, true, new CustomBluetoothGattCallback(address)));
        } else {
            Log.d(TAG, "connect() - discovering services for " + address);
            bluetoothGatt.get(address).discoverServices();
        }
    }

    public synchronized void disconnect(final String address) {
        if (bluetoothGatt.containsKey(address)) {
            Log.d(TAG, "disconnect() - to device " + address);
            if (!Devices.isEnabled(this, address)) {
                Log.d(TAG, "disconnect() - no background linked for " + address);
                if (bluetoothGatt.get(address) != null) {
                    bluetoothGatt.get(address).disconnect();
                }
                bluetoothGatt.remove(address);
            }
        }
    }

    //disconect with divice
    public synchronized void remove(final String address) {
        if (bluetoothGatt.containsKey(address)) {
            Log.d(TAG, "remove() - to device " + address);
            if (bluetoothGatt.get(address) != null) {
                bluetoothGatt.get(address).disconnect();
            }
            bluetoothGatt.remove(address);
        }
    }
}
