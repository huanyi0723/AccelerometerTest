package com.example.accelerometertest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class ShakeListener implements SensorEventListener {
  
  private Context mContext;
  private Sensor sensor; // 传感器
  private SensorManager sensorManager; // 传感器管理器
  private OnShakeListener onShakeListener;

  // 手机上一个位置时重力感应坐标
  private float lastX;
  private float lastY;
  private float lastZ;
  private double speed;

  private long lastUpdateTime;
  int UPTATE_INTERVAL_TIME = 70;// 两次检测的时间间隔
  int SPEED_SHRESHOLD = 3000;// 速度阈值

  // 双甩
  int count = 0;
  int timeSlice = 0; // 时间片
  
  final int GET_SUCC = 0;
  Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case GET_SUCC:
          count = 0;
          timeSlice = 0;
          break;
      }
    }
  };

  public ShakeListener(Context mContext) {
    super();
    this.mContext = mContext;
    start();
  }
  
  //开始
  public void start() {
    // 获得传感器管理器
    sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
    if (sensorManager != null) {
      // 获得重力传感器
      sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    // 注册
    if (sensor != null) {
      sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

  }
  //停止
  public void stop() {
    sensorManager.unregisterListener(this);
  }

  // 设置重力感应监听器
  public void setOnShakeListener(OnShakeListener listener) {
    onShakeListener = listener;
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    
    long currentUpdateTime = System.currentTimeMillis(); // 现在检测时间
    long timeInterval = currentUpdateTime - lastUpdateTime; // 两次检测的时间间隔
    if (timeInterval < UPTATE_INTERVAL_TIME) // 判断是否达到了检测时间间隔
      return;
    lastUpdateTime = currentUpdateTime; // 现在的时间变成last时间

    float[] values = event.values;
    // 获得x,y,z坐标
    float x = event.values[0];
    float y = event.values[1];
    float z = event.values[2];

    // 获得x,y,z的变化值
    float deltaX = x - lastX;
    float deltaY = y - lastY;
    float deltaZ = z - lastZ;

    // 将现在的坐标变成last坐标
    lastX = x;
    lastY = y;
    lastZ = z;

    speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 10000 / timeInterval;
    Log.i("TAG", "speed" + speed);
    Log.i("TAG", "count---" + count);
    Log.i("TAG", "timeSlice---" + timeSlice);

    //限制两次摇动在1S的时间内
    if (count == 1) {
      timeSlice++;
      if (timeSlice * UPTATE_INTERVAL_TIME > 1000) {
        count = 0;
        timeSlice = 0;
      }
    }

    if (speed > SPEED_SHRESHOLD) {
      Log.i("TAG",  " 摇一摇---------------------------------------------------------------------------------------------------摇一摇");
      
      count++;

      if (count == 2) {
        
        count = 0;
        //如果两次触发在300毫秒之内 只能算一次
        if (timeSlice * UPTATE_INTERVAL_TIME < 300) {
          count = 1;
        } else {
          onShakeListener.onShake();
          
          handler.removeMessages(GET_SUCC);
          handler.sendEmptyMessageDelayed(GET_SUCC, 300);
        }
        
        timeSlice = 0;
      }
    }

  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    
  }
  
  public interface OnShakeListener {
    public void onShake();
  }


  public double getSpeed() {
    return speed;
  }
  
}