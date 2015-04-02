package edu.stanford.riedel_kruse.bioticgamessdk;

/**
 * Created by dchiu on 3/31/15.
 */
public interface JoystickListener {
    public void onJoystickDirectionStarted(JoystickThread.Direction direction);
    public void onJoystickDirectionFinished(JoystickThread.Direction direction);

    public void onJoystickDown();
    public void onJoystickUp();
}
