package edu.stanford.riedel_kruse.bioticgamessdk;

/**
 * Created by dchiu on 3/31/15.
 */
public interface BluetoothThreadListener {
    public void onLightOn(BluetoothThread.Direction direction);
    public void onLightOff(BluetoothThread.Direction direction);

    public void onJoystickDown();
    public void onJoystickUp();
}
