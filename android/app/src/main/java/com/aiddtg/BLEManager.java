package com.aiddtg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.polidea.rxandroidble2.scan.ScanSettings;
import com.skyautonet.api.dtglite.DtgApi;
import com.skyautonet.api.dtglite.common.State;
import com.skyautonet.api.dtglite.communication.Communicator;
import com.skyautonet.api.dtglite.communication.model.CarRegistrationInfo;
import com.skyautonet.api.dtglite.communication.model.DtgRealData;
import com.skyautonet.api.dtglite.manager.ConfigManager;
import com.skyautonet.api.dtglite.manager.ConnectableDevice;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class BLEManager extends ReactContextBaseJavaModule implements LifecycleEventListener {
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_FINE_LOCATION = 2;

    private static ReactApplicationContext reactContext;
    private Activity activity;

    private Handler handler;

    private BluetoothManager manager;
    private BluetoothAdapter adapter;
    private BluetoothLeScanner scanner;
    private BluetoothGatt gatt;

    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic rwCharacteristic;

    private BluetoothGattDescriptor rwDescriptor;

    public static UUID SERVICE_UUID = UUID.fromString("4880c12c-fdcb-4077-8920-a450d7f9b907");
    public static UUID WRITE_CHARACTERISTIC_UUID = UUID.fromString("fec26ec4-6d71-4442-9f81-55bc21d658d6");
    public static UUID READ_CHARACTERISTIC_UUID = WRITE_CHARACTERISTIC_UUID;

    private Map<String, BluetoothDevice> scanResults;
    private List<ConnectableDevice> deviceList = new ArrayList<>();
    private ScanCallback callback;

    private CompositeDisposable onStopDisposables = new CompositeDisposable();
    private CompositeDisposable onDestroyDisposables = new CompositeDisposable();
    private Disposable scanDisposable = Disposables.empty();

    private boolean isDebug = false;

    BLEManager(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "BluetoothManager";
    }

    @SuppressLint("MissingPermission")
    @ReactMethod
    public void init() {
        DtgApi.init(reactContext);

        activity = reactContext.getCurrentActivity();
        reactContext.addLifecycleEventListener(this);
        handler = new Handler();

        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d("BLEManager", "BLE Not supported");
            return;
        }
        manager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        adapter = manager.getAdapter();

        if (adapter == null || !adapter.isEnabled()) {
            Intent bleEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            activity.startActivityForResult(bleEnableIntent, REQUEST_ENABLE_BT);
            Log.d("BLEManager", "Turn on BT");
        }

        scanner = adapter.getBluetoothLeScanner();

        Disposable disposable = DtgApi.getConnectionManager().getCommunicator().onReceiveRealtimeData()
                .observeOn(AndroidSchedulers.mainThread())
                .map(DtgRealData::parse)
                .subscribe(realData -> {
                    Log.d("BLEManager", "Real data received. data=" + realData);
                    WritableMap data = Arguments.createMap();
                    data.putInt("speed", realData.speed);
                    data.putDouble("gpsX", realData.gpsX);
                    data.putDouble("gpsY", realData.gpsY);
                    data.putBoolean("isGpsOk", realData.isGpsOk);
                    sendEvent("onRealtimeDataReceived", data);
                });
        onStopDisposables.add(disposable);
    }

    @Override
    public void onHostResume() {
        Log.d("BLEManager", "onResume");
    }

    @Override
    public void onHostDestroy() {
        Log.d("BLEManager", "onHostDestroy");
        onDestroyDisposables.clear();
    }

    @Override
    public void onHostPause() {
        Log.d("BLEManager", "onHostPause");
        onStopDisposables.clear();
        stopRealtimeData();
    }

    @SuppressLint("MissingPermission")
    @ReactMethod
    public void startScan() {
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d("BLEManager", "BLE Not supported");
            return;
        }

        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return;
        }
        stopScan();
        disconnectGattServer();
        deviceList.clear();

        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

        scanDisposable = DtgApi.getScanManager().startScan(0, settings)
                .subscribe(connectableDevice -> {
                    if (deviceList.stream().noneMatch(device -> device.macAddress.equals(connectableDevice.macAddress))) {
                        Log.d("BLEManager", "Device Found. device: " + connectableDevice);
                        deviceList.add(connectableDevice);

                        WritableArray scannedDevices = Arguments.createArray();
                        for (ConnectableDevice device : deviceList) {
                            WritableMap scannedDevice = Arguments.createMap();
                            scannedDevice.putString("name", device.name);
                            scannedDevice.putString("address", device.macAddress);
                            scannedDevices.pushMap(scannedDevice);
                        }
                        WritableMap params = Arguments.createMap();
                        params.putArray("devices", scannedDevices);
                        sendEvent("onDeviceDiscovered", params);
                    }
                });
    }

    @SuppressLint("MissingPermission")
    @ReactMethod
    public void stopScan() {
        Log.d("BLEManager", "Stop Scan");
        scanDisposable.dispose();
    }

    @SuppressLint("MissingPermission")
    @ReactMethod
    public void connectDevice(String addr) {
        Log.d("BLEManager", "Start connect to " + addr);

        DtgApi.getConnectionManager().connect(addr)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d("BLEManager", "Success");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("BLEManager", "Error occured. error=" + throwable.getLocalizedMessage());
                    }
                });

        Disposable disposable = DtgApi.getConnectionManager().onStateChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(state -> {
                    Log.d("BLEManager", "State Changed. state=" + state.state);
                    WritableMap targetDevice = Arguments.createMap();
                    targetDevice.putString("name", state.deviceName);
                    targetDevice.putString("address", state.deviceId);
                    if (state.state == State.CONNECTED) {
                        targetDevice.putString("state", "CONNECTED");
                    }
                    else if (state.state == State.READY) {
                        targetDevice.putString("state", "READY");
                    }
                    else if (state.state == State.DISCONNECTED) {
                        targetDevice.putString("state", "DISCONNECTED");
                    }
                    else {
                        targetDevice.putString("state", "UNDEFINED");
                    }
                    sendEvent("onStateChanged", targetDevice);
                });
        onStopDisposables.add(disposable);

        if (isDebug) {
            BluetoothDevice device = adapter.getRemoteDevice(addr);

            GattClientCallback callback = new GattClientCallback();
            if (device == null) {
                Log.d("BLEManager", "Device not found. addr=" + addr);
            }

            gatt = device.connectGatt(DtgApi.getConnectionManager().getContext(), false, callback);
            gatt.connect();
        }
    }

    @ReactMethod
    public void startRealtimeData() {
        if (DtgApi.getConnectionManager().getState() == State.READY) {
            Communicator communicator = DtgApi.getConnectionManager().getCommunicator();
            Disposable disposable = Completable.timer(500, TimeUnit.MILLISECONDS)
                    .andThen(communicator.requestStartRealTimeDtgData())
                    .subscribe();
            onDestroyDisposables.add(disposable);
        }
        else {
            Log.d("BLEManager", "Connection Manager is not ready.");
        }
    }

    @ReactMethod
    public void stopRealtimeData() {
        if (DtgApi.getConnectionManager().getState() == State.READY) {
            Communicator communicator = DtgApi.getConnectionManager().getCommunicator();
            Disposable disposable = Completable.timer(500, TimeUnit.MILLISECONDS)
                    .andThen(communicator.requestStopRealTimeDtgData())
                    .onErrorComplete()
                    .subscribe();
            onDestroyDisposables.add(disposable);
        }
    }

    @ReactMethod
    public void setDriverCode(String driverCode) {
        if (DtgApi.getConnectionManager().getState() == State.READY) {
            Communicator communicator = DtgApi.getConnectionManager().getCommunicator();
            Log.d("BLEManager", "Set driver code to " + driverCode);
            communicator.requestSetDriverCode(driverCode)
                    .subscribe();
        }
    }

    @ReactMethod
    public void setCarInfo(String carType, String carPlateNumber, String carRegistrationNumber, String driverBusinessNumber) {
        if (DtgApi.getConnectionManager().getState() == State.READY) {
            Communicator communicator = DtgApi.getConnectionManager().getCommunicator();
            Log.d("BLEManager", "Set car info");
            try {
                String unicodeStr = new String(carPlateNumber.getBytes(Charset.forName("euc-kr")), "euc-kr");
                CarRegistrationInfo info = new CarRegistrationInfo(carType,
                        unicodeStr,
                        carRegistrationNumber,
                        driverBusinessNumber);
                communicator.requestSetCarInfo(info)
                        .doOnError(throwable -> {
                            WritableMap response = Arguments.createMap();
                            response.putString("response", throwable.getLocalizedMessage());
                            response.putInt("errorCode", -1);
                            sendEvent("didUpdateValue", response);
                        })
                        .subscribe();
            }
            catch (Exception e) {
                Log.d("BLEManager", "Text encoding error. error=" + e.getLocalizedMessage());
            }
        }
    }

    @ReactMethod
    public void requestFileList(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        Log.d("BLEManager", "Request File List");
        if (DtgApi.getConnectionManager().getState() == State.READY) {
            Communicator communicator = DtgApi.getDownloadManager().getCommunicator();;
            LocalDate localStartDate = LocalDate.of(startYear, startMonth, startDay);
            LocalDate localEndDate = LocalDate.of(endYear, endMonth, endDay);
            Date startDate = Date.from(localStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(localEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Log.d("BLEManager", "Start: " + startDate);
            Log.d("BLEManager", "end: " + endDate);

            communicator.requestFileList(startDate, endDate)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(fileItems -> {
                        Log.d("BLEManager", "File List Received. list=" + fileItems);
                    });
        }
        else {
            Log.d("BLEManager", "Connection is not ready");
        }
    }

    @ReactMethod
    public void requestFileDownload(int startYear, int startMonth, int startDay, int endYear,
                                    int endMonth, int endDay, int startNumber, int endNumber) {
        Log.d("BLEManager", "Request File Download");
        if (DtgApi.getConnectionManager().getState() == State.READY) {
            Communicator communicator = DtgApi.getDownloadManager().getCommunicator();
            LocalDate localStartDate = LocalDate.of(startYear, startMonth, startDay);
            LocalDate localEndDate = LocalDate.of(endYear, endMonth, endDay);
            Date startDate = Date.from(localStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(localEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Log.d("BLEManager", "Start: " + startDate);
            Log.d("BLEManager", "end: " + endDate);

            communicator.requestDownloadDtgFile(startDate, endDate, startNumber, endNumber)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(throwable -> {
                        WritableMap response = Arguments.createMap();
                        response.putString("response", throwable.getLocalizedMessage());
                        response.putInt("errorCode", -1);
                        sendEvent("didUpdateValue", response);
                    })
                    .subscribe(downloadProgress -> {
                        Log.d("BLEManager", "File Download Test. progress=" + downloadProgress);
                        String responseStr = "File #" + downloadProgress.getFileNumber() + "\n";
                        responseStr += downloadProgress.getDownloadedSize() + " / " + downloadProgress.getTotalSize();
                        WritableMap response = Arguments.createMap();
                        response.putInt("errorCode", 0);
                        response.putString("response", responseStr);
                        sendEvent("didUpdateValue", response);
                    });
        }
        else {
            Log.d("BLEManager", "Connection is not ready");
        }
    }

    @SuppressLint("MissingPermission")
    private void disconnectGattServer() {
        Log.d("BLEManager", "Closing Gatt Connection");

        if (isDebug) {
            if (gatt != null) {
                gatt.disconnect();
                gatt.close();
            }
        }
    }

    private void sendEvent(String eventName, WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @SuppressLint("MissingPermission")
    private class GattClientCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_FAILURE) {
                disconnectGattServer();
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                disconnectGattServer();
                return;
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BLEManager", "Connected to device");
//                gatt.requestMtu(517);
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d("BLEManager", "onServiceDiscovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (BluetoothGattService service : gatt.getServices()) {
                    if (service.getUuid().equals(SERVICE_UUID)) {
                        Log.d("BLEManager", "Service found. uuid=" + service.getUuid());
                        for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                            Log.d("BLEManager", "Characteristic. uuid=" + characteristic);
                            if (characteristic.getUuid().equals(WRITE_CHARACTERISTIC_UUID)) {
                                Log.d("BLEManager", "Write characteristic found.");
                                writeCharacteristic = characteristic;
                                readCharacteristic = characteristic;
                                rwCharacteristic = characteristic;
                                gatt.setCharacteristicNotification(rwCharacteristic, true);
                            } else if (characteristic.getUuid().equals(READ_CHARACTERISTIC_UUID)) {
                                Log.d("BLEManager", "Read characteristic found.");
                                readCharacteristic = characteristic;
                            }
                        }
                    } else {
                        Log.d("BLEManager", "Service not found. uuid=" + service.getUuid());
                    }
                }
            }
        }

        @Override
        public void onServiceChanged(@NonNull BluetoothGatt gatt) {
            super.onServiceChanged(gatt);
            Log.d("BLEManager", "onServiceChanged.");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] value = characteristic.getValue();
            try {
                String responseStr = new String(value, "euc-kr");
                Log.d("BLEManager", "onCharacteristicChanged. response=" + responseStr);
            } catch (Exception e) {
                Log.d("BLEManager", "onCharacteristicChanged. Failed to decode response. error=" + e.getLocalizedMessage());
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            byte[] value = characteristic.getValue();
            String responseStr = new String(value);
            Log.d("BLEManager", "onCharacteristicRead. response=" + responseStr);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d("BLEManager", "onCharacteristicWrite");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (readCharacteristic != null) {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    String requestStr = new String(characteristic.getValue());
                    Log.d("BLEManager", "onCharacteristicWrite. value=" + requestStr);
                    gatt.readCharacteristic(rwCharacteristic);
                }
                else {
                    Log.d("BLEManager", "Characteristic not found.");
                }
            }
            else {
                Log.d("BLEManager", "onCharacteristicWrite Failed. error=" + status);
            }
        }
    }
}
