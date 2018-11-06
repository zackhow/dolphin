package org.dolphinemu.dolphinemu.overlay;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import org.dolphinemu.dolphinemu.NativeLibrary;

import java.util.Arrays;

import static android.content.Context.SENSOR_SERVICE;

public class InputOverlayGyro implements SensorEventListener
{
  private SensorManager sensorManager;
  private float[] data = {0, 0, 0}; // x, y, z
  private float[] previousData = {0, 0, 0}; // x, y, z
  private long lastUpdate = 0;
  private Shake shake;
  private Tilt tilt;

  private Handler handler;

  public InputOverlayGyro(Context context)
  {
    try
    {
      sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
      sensorManager.registerListener(this,
              sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
              SensorManager.SENSOR_DELAY_GAME);
    }
    catch (Exception e)
    {
      // Failed to get sensor
      return;
    }

    handler = new Handler();
    shake = new Shake(NativeLibrary.ButtonType.WIIMOTE_SHAKE_X);
    tilt = new Tilt();
  }

  public void unRegister()
  {
    sensorManager.unregisterListener(this);
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy)
  {

  }

  @Override
  public void onSensorChanged(SensorEvent event)
  {
    long time = System.currentTimeMillis();

    // limit polling to 50ms
    if ((time - lastUpdate) > 50)
    {
      lastUpdate = time;
      data = event.values;
      check();
      previousData = Arrays.copyOf(data, data.length);
    }
  }

  public void check()
  {
    shake.check();
    tilt.check();
  }

  class Shake
  {
    private static final float SHAKE_THRESHOLD = 10;
    int shakeBase;

    public Shake(int shakeBase)
    {
      this.shakeBase = shakeBase;
    }

    public void check()
    {
      for (int i = 0; i < 3; i++)
      {
        final int shakeAxis = shakeBase + i;

        if (data[i] + (previousData[i] * -1f) > SHAKE_THRESHOLD)
        {
          NativeLibrary.onGamePadEvent(NativeLibrary.TouchScreenDevice, shakeAxis,
                  NativeLibrary.ButtonState.PRESSED);
          handler.postDelayed(() -> NativeLibrary.onGamePadEvent(NativeLibrary.TouchScreenDevice,
                  shakeAxis,
                  NativeLibrary.ButtonState.RELEASED), 300);
        }
      }
    }
  }

  class Tilt
  {
    private static final float TILT_THRESHOLD = 2;
    private static final float TILT_MODIFIER = 15f;
    public Tilt()
    {
    }

    public void check()
    {
      checkX();
      checkY();
    }

    /**
     * Check for TILT_RIGHT and TILT_LEFT
     */
    private void checkY()
    {
      if (data[1] < (TILT_THRESHOLD * -1))
      {
        NativeLibrary.onGamePadMoveEvent(NativeLibrary.TouchScreenDevice,
                NativeLibrary.ButtonType.WIIMOTE_TILT_LEFT,
                data[1] / TILT_MODIFIER);
      }
      else if (data[1] > TILT_THRESHOLD)
      {
        NativeLibrary.onGamePadMoveEvent(NativeLibrary.TouchScreenDevice,
                NativeLibrary.ButtonType.WIIMOTE_TILT_RIGHT,
                data[1] / TILT_MODIFIER);

      }
      else
      {
        NativeLibrary.onGamePadEvent(NativeLibrary.TouchScreenDevice,
                NativeLibrary.ButtonType.WIIMOTE_TILT_LEFT,
                0);
        NativeLibrary.onGamePadEvent(NativeLibrary.TouchScreenDevice,
                NativeLibrary.ButtonType.WIIMOTE_TILT_RIGHT,
                0);
      }
    }

    /**
     * Check for TILT_FORWARD and TILT_BACKWARD
     */
    private void checkX()
    {
      if (data[0] < (TILT_THRESHOLD * -1))
      {
        NativeLibrary.onGamePadMoveEvent(NativeLibrary.TouchScreenDevice,
                NativeLibrary.ButtonType.WIIMOTE_TILT_FORWARD,
                data[0] / TILT_MODIFIER);
      }
      else if (data[0] > TILT_THRESHOLD)
      {
        NativeLibrary.onGamePadMoveEvent(NativeLibrary.TouchScreenDevice,
                NativeLibrary.ButtonType.WIIMOTE_TILT_BACKWARD,
                data[0] / TILT_MODIFIER);
      }
      else
      {
        NativeLibrary.onGamePadEvent(NativeLibrary.TouchScreenDevice,
                NativeLibrary.ButtonType.WIIMOTE_TILT_FORWARD,
                0);
        NativeLibrary.onGamePadEvent(NativeLibrary.TouchScreenDevice,
                NativeLibrary.ButtonType.WIIMOTE_TILT_BACKWARD,
                0);
      }
    }
  }
}
