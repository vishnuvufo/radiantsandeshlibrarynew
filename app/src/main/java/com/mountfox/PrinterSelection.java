package com.mountfox;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PrinterSelection extends Activity {

    static BluetoothDevice device;
    static BluetoothSocket btSocket;
    static OutputStream outputStream;
    BluetoothAdapter bluetoothAdapter;
    static boolean isPrinterConnected = false;
    static String address = "0";// "00:1F:B7:05:3C:64"

    static final int PRINTER_2MM = 1;
    static final int PRINTER_3MM = 2;
    static int PRINTER_TYPE = PRINTER_2MM;

    ArrayAdapter<String> btArrayAdapter;
    ArrayList<HashMap<String, String>> list;
    HashMap<String, String> value;

    SimpleAdapter btAdapter;
    ListView printerSelectionList;

    private static final String TAG = "ConnectingPrinter";
    ProgressDialog progressDialog;
    Button searchPrinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.printer_selection);
        initializeComponents();
    }

    public void initializeComponents() {
        printerSelectionList = (ListView) findViewById(R.id.printerSelectionList);
        searchPrinter = (Button) findViewById(R.id.searchPrinter);
        if (PrinterSelection.isPrinterConnected)
            PrinterSelection.unpairDevice();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Searching Printer");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.progressbar));

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        list = new ArrayList<HashMap<String, String>>();
        btAdapter = new SimpleAdapter(this, list,
                R.layout.printer_selection_item, new String[] { "name",
                        "address" }, new int[] { R.id.deviceName,
                        R.id.deviceAddress });
        printerSelectionList.setAdapter(btAdapter);

        printerSelectionList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                TextView deviceAddress = (TextView) view
                        .findViewById(R.id.deviceAddress);
                address = deviceAddress.getText().toString();
                getPrinterSocket();
            }
        });

        searchPrinter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                initializeComponents();
            }
        });

        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),
                    "Bluetooth is not available to connect printer",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        } else if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(),
                    "Please enable your bluetooth to connect printer",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else if (!bluetoothAdapter.isDiscovering()) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(ActionFoundReceiver, filter);
            bluetoothAdapter.startDiscovery();
        }
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                value = new HashMap<String, String>();
                value.put("name", device.getName());
                value.put("address", device.getAddress());
                list.add(value);
                btAdapter.notifyDataSetChanged();
                if (Config.DEBUG)
                {
                    //Log.d(TAG, "Device Found: Name: " + device.getName()
                         //   + "\n Address: " + device.getAddress());
                }
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                progressDialog.show();
                if (Config.DEBUG)
                {
                    //Log.d(TAG, "Discovery Started");
                }
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                if (bluetoothAdapter != null) {
                    if (bluetoothAdapter.isDiscovering())
                        bluetoothAdapter.cancelDiscovery();
                }
                unregisterReceiver(ActionFoundReceiver);
                if (Config.DEBUG)
                {
                    //Log.d(TAG, "Discovery Ended");
                }
            }
        }
    };

    public void getPrinterSocket() {

        if (bluetoothAdapter == null) {

            Toast.makeText(getApplicationContext(),
                    "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(),
                    "Please enable your bluetooth", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            device = bluetoothAdapter.getRemoteDevice(address);
            try {
                if (btSocket != null) {
                    btSocket.close();
                }
                Method m = device.getClass().getMethod("createRfcommSocket",
                        new Class[] { int.class });
                btSocket = (BluetoothSocket) m.invoke(device, 1);
                if (bluetoothAdapter.isDiscovering())
                    bluetoothAdapter.cancelDiscovery();
                btSocket.connect();
                outputStream = btSocket.getOutputStream();
                Toast.makeText(getApplicationContext(),
                        "Connected to " + device.getName(), Toast.LENGTH_SHORT)
                        .show();
                isPrinterConnected = true;
                finish();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Invalid PIN or Device is in offline",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unused")
    public boolean printReceipt(byte[] data) {
        try {
            // Printing Size and Format
            byte[] rf6 = new byte[3];
            rf6[0] = 0x1B;
            rf6[1] = 0x4B;
            rf6[2] = 0x01;

            byte[] rf9 = new byte[3];
            rf9[0] = 0x1B;
            rf9[1] = 0x4B;
            rf9[2] = 0x04;

            byte[] rf14 = new byte[3];
            rf14[0] = 0x1B;
            rf14[1] = 0x4B;
            rf14[2] = 0x09;

            if (PRINTER_TYPE == PRINTER_3MM)
            {
                // Printer: 3mm
                // Rf9 -38
                // Rf6 -28
                // Rf14-58

                String line_under = "\n______________________________________";
                String line_tilde = "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

                // Print Start Line
                outputStream.write(rf9);
                outputStream.flush();
                outputStream.write(line_tilde.getBytes());
                outputStream.flush();

                // Print Title Start
                outputStream.write(rf6);
                outputStream.flush();
                outputStream.write(("\n" + getSpace(10) + "RADIANT").getBytes());
                outputStream.flush();

                outputStream.write(rf9);
                outputStream.flush();
                outputStream
                        .write(("\n" + getSpace(4) + "CASH MANAGEMENTSERVICES (P) LTD")
                                .getBytes());
                outputStream.flush();

                outputStream
                        .write(("\n" + getSpace(7) + "No.28,Vijayaragava Road,")
                                .getBytes());
                outputStream.flush();

                outputStream
                        .write(("\n" + getSpace(7) + "T.Nagar,Chennai - 600017")
                                .getBytes());
                outputStream.flush();

                outputStream
                        .write(("\n" + getSpace(3) + "Tele: 044 28155448 / 6448 / 7448")
                                .getBytes());
                outputStream.flush();

                outputStream
                        .write(("\n" + getSpace(6) + "www.radiantcashservices.com")
                                .getBytes());
                outputStream.flush();
                // Print Title End

                // Line
                outputStream.write(rf9);
                outputStream.flush();
                outputStream.write(line_tilde.getBytes());
                outputStream.flush();

                // Print Content Start
                outputStream.write(rf9);
                outputStream.flush();
                outputStream.write(data);
                outputStream.flush();
                // Print Content End

                // Print End Start
                outputStream.write(rf14);
                outputStream.flush();
                int total_line = 58;
                int sp = (int) ((total_line - "Powered by: www.radiantintegritytech.com"
                        .length()) / 2);
                outputStream
                        .write(("\n" + getSpace(sp) + "Powered by: www.radiantintegritytech.com")
                                .getBytes());
                outputStream.flush();
                // Print End of Statement End

                // Print End Line
                outputStream.write(rf9);
                outputStream.flush();
                outputStream.write(line_tilde.getBytes());
                outputStream.flush();
            }
            else if (PRINTER_TYPE == PRINTER_2MM)
            {
                // Printer: 2mm
                // Rf6 -19
                // Rf9 -25
                // Rf14-38

                String line_under = "\n______________________________________";
                String line_tilde = "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
                int total_line = 38;

                // Print Start Line
                outputStream.write(rf14);
                outputStream.flush();
                outputStream.write(line_tilde.getBytes());
                outputStream.flush();

                // Print Title Start
                outputStream.write(rf9);
                outputStream.flush();
                total_line = 25;
                int sp = (int) ((total_line - "RADIANT".length()) / 2);
                outputStream.write(("\n" + getSpace(sp) + "RADIANT").getBytes());
                outputStream.flush();

                outputStream.write(rf14);
                outputStream.flush();
                total_line = 38;
                sp = (int) ((total_line - "CASH MANAGEMENTSERVICES (P) LTD".length()) / 2);
                outputStream
                        .write(("\n" + getSpace(sp) + "CASH MANAGEMENTSERVICES (P) LTD")
                                .getBytes());
                outputStream.flush();

                sp = (int) ((total_line - "No.28,Vijayaragava Road,".length()) / 2);
                outputStream
                        .write(("\n" + getSpace(sp) + "No.28,Vijayaragava Road,")
                                .getBytes());
                outputStream.flush();

                sp = (int) ((total_line - "T.Nagar,Chennai - 600017".length()) / 2);
                outputStream
                        .write(("\n" + getSpace(sp) + "T.Nagar,Chennai - 600017")
                                .getBytes());
                outputStream.flush();

                sp = (int) ((total_line - "Tele: 044 28155448 / 6448 / 7448".length()) / 2);
                outputStream
                        .write(("\n" + getSpace(sp) + "Tele: 044 28155448 / 6448 / 7448")
                                .getBytes());
                outputStream.flush();

                sp = (int) ((total_line - "www.radiantcashservices.com".length()) / 2);
                outputStream
                        .write(("\n" + getSpace(sp) + "www.radiantcashservices.com")
                                .getBytes());
                outputStream.flush();
                // Print Title End

                // Line
                outputStream.write(line_tilde.getBytes());
                outputStream.flush();

                // Print Content Start
                outputStream.write(data);
                outputStream.flush();
                // Print Content End

                // Print End Start
                outputStream.write(("\nPoweredby:www.radiantintegritytech.com").getBytes());
                outputStream.flush();
                // Print End of Statement End

                // Print End Line
                outputStream.write(line_tilde.getBytes());
                outputStream.flush();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unused")
    public boolean printEod(byte[] data) {
        try {
            byte[] rf6 = new byte[3];
            rf6[0] = 0x1B;
            rf6[1] = 0x4B;
            rf6[2] = 0x01;

            byte[] rf9 = new byte[3];
            rf9[0] = 0x1B;
            rf9[1] = 0x4B;
            rf9[2] = 0x04;

            byte[] rf14 = new byte[3];
            rf14[0] = 0x1B;
            rf14[1] = 0x4B;
            rf14[2] = 0x09;

            if (PRINTER_TYPE == PRINTER_3MM)
            {
                // Printer: 3mm
                // Rf9 -38
                // Rf6 -28
                // Rf14-58

                String line_under = "\n______________________________________";
                String line_tilde = "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

                // Print Start Line
                outputStream.write(rf9);
                outputStream.flush();
                outputStream.write(line_under.getBytes());
                outputStream.flush();

                // Print Title Start
                outputStream.write(rf6);
                outputStream.flush();
                outputStream.write(("\n" + getSpace(10) + "RADIANT").getBytes());
                outputStream.flush();

                outputStream.write(rf9);
                outputStream.flush();
                outputStream
                        .write(("\n" + getSpace(4) + "CASH MANAGEMENTSERVICES (P) LTD")
                                .getBytes());
                outputStream.flush();

                outputStream
                        .write(("\n" + getSpace(7) + "No.28,Vijayaragava Road,")
                                .getBytes());
                outputStream.flush();

                outputStream
                        .write(("\n" + getSpace(7) + "T.Nagar,Chennai - 600017")
                                .getBytes());
                outputStream.flush();

                outputStream
                        .write(("\n" + getSpace(3) + "Tele: 044 28155448 / 6448 / 7448")
                                .getBytes());
                outputStream.flush();

                outputStream
                        .write(("\n" + getSpace(6) + "www.radiantcashservices.com")
                                .getBytes());
                outputStream.flush();
                // Print Title End

                // Line
                outputStream.write(rf9);
                outputStream.flush();
                outputStream.write(line_under.getBytes());
                outputStream.flush();

                // Print Content Start
                outputStream.write(rf9);
                outputStream.flush();
                outputStream.write(data);
                outputStream.flush();
                // Print Content End

                // Print End Start
                outputStream.write(rf14);
                outputStream.flush();
                int total_line = 58;
                int sp = (int) ((total_line - "********** End of Statement **********"
                        .length()) / 2);
                outputStream
                        .write(("\n\n" + getSpace(sp) + "********** End of Statement **********")
                                .getBytes());
                outputStream.flush();
                sp = (int) ((total_line - "Powered by: www.radiantintegritytech.com"
                        .length()) / 2);
                outputStream.write(("\n\n" + getSpace(sp)
                        + "Powered by: www.radiantintegritytech.com" + "\n")
                        .getBytes());
                outputStream.flush();
                // Print End of Statement End

                // Print End Line
                outputStream.write(rf9);
                outputStream.flush();
                outputStream.write(line_under.getBytes());
                outputStream.flush();
            }
            else if (PRINTER_TYPE == PRINTER_2MM)
            {
                // Printer: 2mm
                // Rf6 -19
                // Rf9 -25
                // Rf14-38

                String line_under = "\n______________________________________";
                String line_tilde = "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
                int total_line = 38;

                // Print Start Line
                outputStream.write(rf14);
                outputStream.flush();
                outputStream.write(line_tilde.getBytes());
                outputStream.flush();

                // Print Title Start
                outputStream.write(rf9);
                outputStream.flush();
                total_line = 25;
                int sp = (int) ((total_line - "RADIANT".length()) / 2);
                outputStream.write(("\n" + getSpace(sp) + "RADIANT").getBytes());
                outputStream.flush();

                outputStream.write(rf14);
                outputStream.flush();
                total_line = 38;
                sp = (int) ((total_line - "CASH MANAGEMENTSERVICES (P) LTD".length()) / 2);
                outputStream
                        .write(("\n" + getSpace(sp) + "CASH MANAGEMENTSERVICES (P) LTD")
                                .getBytes());
                outputStream.flush();

                sp = (int) ((total_line - "No.28,Vijayaragava Road,".length()) / 2);
                outputStream
                        .write(("\n" + getSpace(sp) + "No.28,Vijayaragava Road,")
                                .getBytes());
                outputStream.flush();

                sp = (int) ((total_line - "T.Nagar,Chennai - 600017".length()) / 2);
                outputStream
                        .write(("\n" + getSpace(sp) + "T.Nagar,Chennai - 600017")
                                .getBytes());
                outputStream.flush();

                sp = (int) ((total_line - "Tele: 044 28155448 / 6448 / 7448".length()) / 2);
                outputStream
                        .write(("\n" + getSpace(sp) + "Tele: 044 28155448 / 6448 / 7448")
                                .getBytes());
                outputStream.flush();

                sp = (int) ((total_line - "www.radiantcashservices.com".length()) / 2);
                outputStream
                        .write(("\n" + getSpace(sp) + "www.radiantcashservices.com")
                                .getBytes());
                outputStream.flush();
                // Print Title End

                // Line
                outputStream.write(line_tilde.getBytes());
                outputStream.flush();

                // Print Content Start
                outputStream.write(data);
                outputStream.flush();
                // Print Content End

                // Print End Start
                sp = (int) ((total_line - "***** End of Statement *****"
                        .length()) / 2);
                outputStream
                        .write(("\n\n" + getSpace(sp) + "***** End of Statement *****")
                                .getBytes());
                outputStream.flush();
                outputStream.write(("\n\nPoweredby:www.radiantintegritytech.com" + "\n")
                        .getBytes());
                outputStream.flush();
                // Print End of Statement End

                // Print End Line
                outputStream.write(line_under.getBytes());
                outputStream.flush();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getSpace(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static void unpairDevice() {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            if (Config.DEBUG)
            {
                //Log.d(TAG, "Unpaired with " + device.getName());
            }
            isPrinterConnected = false;
        } catch (Exception e) {
            if (Config.DEBUG)
            {
                //Log.e(TAG, e.getMessage());
            }
        }
        try {
            if (btSocket != null) {
                btSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
