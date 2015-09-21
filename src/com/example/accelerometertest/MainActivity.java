package com.example.accelerometertest;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.EditText;

public class MainActivity extends Activity{

  EditText etTxt1;
  private ShakeListener mShakeListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    etTxt1 = (EditText) findViewById(R.id.txt1);
  }

  @Override
  protected void onResume() {
    super.onResume();
    //注册
    mShakeListener = new ShakeListener(this);
    mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
      
      @Override
      public void onShake() {
        Log.i("TAG", "震动震动震动震动震动震动震动震动震动震动震动震动震动震动震动震动震动震动震动震动");
        Vibrator vVi = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        vVi.vibrate(400);
        
        StringBuilder sb = new StringBuilder();
        sb.append(mShakeListener.getSpeed());
        etTxt1.setText(sb.toString());
      }
    });

  }

  @Override
  protected void onStop() {
    super.onStop();
    // 取消注册
    mShakeListener.stop();
  }


}
