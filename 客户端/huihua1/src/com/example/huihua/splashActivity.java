package com.example.huihua;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

//��������

public class splashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //װ�ز����ļ�
        setContentView(R.layout.activity_splash); 
        //��������ʱ�����������߳���
        //2S����ת    ˯2S
        //�̵߳Ŀ���
        new MyThread().start();
    }
    //���̵߳Ĵ���
    class MyThread extends Thread{
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		super.run();
    		//�����߳���˯��
    		SystemClock.sleep(3000);
    		//��תҳ��
            Intent intent = new Intent(splashActivity.this,MainActivity.class);
            //������ת��ͼ
            startActivity(intent);
            //�˳���ǰ����
            finish();
    	}
    }
    
    
}
