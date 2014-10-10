package jp.co.smirate.smirate;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothFragment extends Fragment {

    public interface BluetoothCallback {
        /**
         * check whether device support Bluetooth.
         *
         * @param isAvailable
         *            true if device support Bluetooth.
         */
        public void onCheckAvailability(boolean isAvailable);

        /**
         * called when bluetooth state be changed.
         *
         * @param enabled
         */
        public void onChangeBluetoothState(boolean enabled);

        /**
         * called when try to connect.
         *
         * @param connected
         */
        public void onConnected(boolean connected);

        /**
         * called when connection established
         */
        public void onOpenConnection();

        public void onDataArrived(int size, byte[] buffer);
    }

    public static final String EXTRA_UUID = "uuid";
    public static final int SPP_MODE = 0;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int REQUEST_ENABLE_BT = 11;

    private BluetoothCallback mCallback;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mIsAvailable;
    private boolean mIsEnable;
    private UUID mUuid;

    private ConnectThread mConnectThread;
    private TransmitThread mTransmitThread;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof BluetoothCallback == false) {
            throw new ClassCastException("Activity have to implement BluetoothCallback");
        }

        mCallback = (BluetoothCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mTransmitThread != null) {
            mTransmitThread.cancel();
            mTransmitThread = null;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        int sppMode = args == null ? 0 : args.getInt(EXTRA_UUID, 0);
        switch (sppMode) {
            case 0:
                mUuid = SPP_UUID;
                break;
            default:
                mUuid = SPP_UUID;
                break;
        }

        // 1. Get the BluetoothAdapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mIsAvailable = mBluetoothAdapter != null;

        mCallback.onCheckAvailability(mIsAvailable);

        // 2. Enable Bluetooth
        if (mBluetoothAdapter.isEnabled()) {
            mIsEnable = true;
            onEnabled();
        } else {
            mIsEnable = false;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        mCallback.onChangeBluetoothState(mIsEnable);
    }

    BroadcastReceiver mStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
            if (state == BluetoothAdapter.STATE_ON) {
                mIsEnable = true;
                onEnabled();
            } else {
                mIsEnable = false;
            }
            mCallback.onChangeBluetoothState(mIsEnable);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mStateReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mStateReceiver);
        if (mBluetoothAdapter.isDiscovering()) {
            stopDiscovery();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                mIsEnable = true;
                onEnabled();
            } else {
                mIsEnable = false;
            }
            mCallback.onChangeBluetoothState(mIsEnable);
        }
    }

    private void onEnabled() {
        selectPairedDevice();
    }

    public void selectPairedDevice() {
        // 3. Querying paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
            for (BluetoothDevice device : pairedDevices) {
                deviceList.add(device);
            }
            final BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(getActivity(), deviceList);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Paired devices");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int position) {
                    BluetoothDevice selectedDevice = adapter.getItem(position);
                    connectToDevice(selectedDevice);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, null);

            Context context = getActivity();

            PackageManager manager = context.getPackageManager();
            int permission = manager.checkPermission(Manifest.permission.BLUETOOTH_ADMIN, context.getPackageName());

            if (permission == PackageManager.PERMISSION_GRANTED) {
                builder.setPositiveButton("Search for devices", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        discoveringDevices();
                    }
                });
            }
            builder.show();
        }
    }

    BluetoothDeviceAdapter mDiscoveringAdapter;

    BroadcastReceiver mDiscoveringReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            mDiscoveringAdapter.add(device);
        }
    };

    private void discoveringDevices() {
        // 4. discovering devices
        ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
        mDiscoveringAdapter = new BluetoothDeviceAdapter(getActivity(), deviceList);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(mDiscoveringAdapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int position) {
                stopDiscovery();

                BluetoothDevice selectedDevice = mDiscoveringAdapter.getItem(position);
                connectToDevice(selectedDevice);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                stopDiscovery();
            }
        });
        builder.show();

        startDiscovery();
    }

    private void startDiscovery() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mDiscoveringReceiver, filter);
        mBluetoothAdapter.startDiscovery();
    }

    private void stopDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
        getActivity().unregisterReceiver(mDiscoveringReceiver);
    }

    private void connectToDevice(BluetoothDevice device) {
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mSocket;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(mUuid);
            } catch (IOException e) {
            }
            mSocket = tmp;
        }

        public void run() {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }

            try {
                mSocket.connect();

                Message msg = mHandler.obtainMessage(MESSAGE_CONNECTED, true);
                mHandler.sendMessage(msg);

            } catch (IOException connectException) {
                try {
                    mSocket.close();
                } catch (IOException closeException) {
                }

                Message msg = mHandler.obtainMessage(MESSAGE_CONNECTED, false);
                mHandler.sendMessage(msg);
                return;
            }

            manageConnectedSocket(mSocket);
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        mTransmitThread = new TransmitThread(socket);
        mTransmitThread.start();
    }

    private static final int MESSAGE_READ = 1;
    private static final int MESSAGE_CONNECTED = 2;
    private static final int MESSAGE_OPEN = 3;

    final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case MESSAGE_READ:
                    int length = msg.arg1;
                    byte[] buffer = (byte[]) msg.obj;
                    mCallback.onDataArrived(length, buffer);
                    break;

                case MESSAGE_CONNECTED:
                    boolean connected = (Boolean) msg.obj;
                    mCallback.onConnected(connected);
                    break;

                case MESSAGE_OPEN:
                    mCallback.onOpenConnection();
                    break;
            }
        }
    };

    private class TransmitThread extends Thread {
        private final BluetoothSocket mSocket;
        private final InputStream mInStream;
        private final OutputStream mOutStream;

        public TransmitThread(BluetoothSocket socket) {
            mSocket = socket;

            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();

                Message msg = mHandler.obtainMessage(MESSAGE_OPEN);
                mHandler.sendMessage(msg);

            } catch (IOException e) {
            }

            mInStream = tmpIn;
            mOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int length;

            while (true) {
                try {
                    length = mInStream.read(buffer);

                    Message msg = mHandler.obtainMessage(MESSAGE_READ, length, -1, buffer);
                    mHandler.sendMessage(msg);

                } catch (IOException e) {
                    break;
                }
            }
        }

        public boolean write(byte[] bytes) {
            if (mOutStream == null) {
                return false;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(bytes[i] + " ");
            }

            try {
                mOutStream.write(bytes);
                return true;
            } catch (IOException e) {
            }

            return false;
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
            }
            try {
                mInStream.close();
            } catch (IOException e) {
            }
            try {
                mOutStream.close();
            } catch (IOException e) {
            }
        }
    }

    public boolean write(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i);
        bytes[1] = (byte) (i >> 8);
        bytes[2] = (byte) (i >> 16);
        bytes[3] = (byte) (i >> 24);

        return write(bytes);
    }

    public boolean write(String s) {
        return write(s.getBytes());
    }

    public boolean write(byte[] bytes) {
        if (mTransmitThread == null) {
            return false;
        }

        return mTransmitThread.write(bytes);
    }

    public static class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

        public BluetoothDeviceAdapter(Context context, List<BluetoothDevice> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            BluetoothDevice device = getItem(position);

            TextView nameView = (TextView) convertView.findViewById(android.R.id.text1);
            nameView.setText(device.getName());

            TextView addressView = (TextView) convertView.findViewById(android.R.id.text2);
            addressView.setText(device.getAddress());

            return convertView;
        }
    }
}