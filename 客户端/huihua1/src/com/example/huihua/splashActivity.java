package com.example.huihua;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

//闪屏界面

public class splashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //装载布局文件
        setContentView(R.layout.activity_splash); 
        //不允许将耗时操作放在主线程中
        //2S后跳转    睡2S
        //线程的开启
        new MyThread().start();
    }
    //子线程的创建
    class MyThread extends Thread{
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		super.run();
    		//在子线程中睡眠
    		SystemClock.sleep(3000);
    		//跳转页面
            Intent intent = new Intent(splashActivity.this,MainActivity.class);
            //发起跳转意图
            startActivity(intent);
            //退出当前界面
            finish();
    	}
    }
    
    
}
