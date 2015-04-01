package edu.stanford.riedel_kruse.bioticgamessdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by dchiu on 3/17/15.
 */
public class BluetoothThread extends Thread {
    public enum Direction {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    private static final UUID uuid = UUID.fromString("00001101-000-1000-8000-00805F9B34FB");
    private static final String TAG = "BluetoothThread";

    private static final char DELIMETER = '\n';

    private static final String ADDRESS = "00:06:66:67:E8:99";

    private OutputStream mOutStream;
    private InputStream mInStream;

    private String mReceiveBuffer;
    private int mLastVert;
    private int mLastHorz;
    private boolean mLastSel;

    private BluetoothThreadListener mListener;

    public BluetoothThread(BluetoothThreadListener listener) {
        mListener = listener;

        mLastVert = 0;
        mLastHorz = 0;
        mLastSel = false;

        mReceiveBuffer = "";
    }

    private void connect() throws Exception {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) {
            throw new Exception("Bluetooth adapter not found or not enabled!");
        }

        BluetoothDevice remoteDevice = adapter.getRemoteDevice(ADDRESS);

        BluetoothSocket socket = remoteDevice.createRfcommSocketToServiceRecord(uuid);

        adapter.cancelDiscovery();

        socket.connect();

        mOutStream = socket.getOutputStream();
        mInStream = socket.getInputStream();
    }

    private void write(String s) throws IOException {
        s += DELIMETER;
        mOutStream.write(s.getBytes());
    }

    private String read() throws IOException {
        String s = "";

        int bytesAvailable = mInStream.available();
        while (bytesAvailable > 0) {
            byte[] inBuffer = new byte[bytesAvailable];
            bytesAvailable -= mInStream.read(inBuffer);
            s += new String(inBuffer, "ASCII");
        }

        return s;
    }

    private void parseMessages() {
        int index = mReceiveBuffer.indexOf(DELIMETER);
        while (index != -1) {
            // Get the first message
            String message = mReceiveBuffer.substring(0, index);
            Log.d(TAG, "Received message: " + message);

            // Remove the first message from the buffer.
            mReceiveBuffer = mReceiveBuffer.substring(index + 1);
            String[] values = message.split(",");
            int vert = Integer.parseInt(values[0].trim());
            int horz = Integer.parseInt(values[1].trim());
            boolean sel = Integer.parseInt(values[2].trim()) == 0;

            if (mListener != null) {
                if (mLastVert != vert) {
                    if (mLastVert == 1) {
                        mListener.onLightOff(Direction.TOP);
                    }
                    else if (mLastVert == -1) {
                        mListener.onLightOff(Direction.BOTTOM);
                    }

                    if (vert == 1) {
                        mListener.onLightOn(Direction.TOP);
                    }
                    else if (vert == -1) {
                        mListener.onLightOn(Direction.BOTTOM);
                    }
                }

                if (mLastHorz != horz) {
                    if (mLastHorz == 1) {
                        mListener.onLightOff(Direction.LEFT);
                    }
                    else if (mLastHorz == -1) {
                        mListener.onLightOff(Direction.RIGHT);
                    }

                    if (horz == 1) {
                        mListener.onLightOn(Direction.LEFT);
                    }
                    else if (horz == -1) {
                        mListener.onLightOn(Direction.RIGHT);
                    }
                }

                if (mLastSel != sel && sel) {
                    mListener.onJoystickDown();
                }
                else if (mLastSel != sel) {
                    mListener.onJoystickUp();
                }
            }

            mLastVert = vert;
            mLastHorz = horz;
            mLastSel = sel;

            index = mReceiveBuffer.indexOf(DELIMETER);
        }
    }

    public void run() {
        try {
            connect();
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to connect!", e);
        }

        while (!this.isInterrupted()) {
            if (mInStream == null || mOutStream == null) {
                Log.e(TAG, "Lost bluetooth connection!");
                break;
            }

            try {
                mReceiveBuffer += read();
            }
            catch (IOException e) {
                Log.e(TAG, "Failed to read!", e);
            }

            parseMessages();
        }
    }
}
