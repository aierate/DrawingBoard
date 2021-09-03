package com.example.huihua;
import com.example.huihua.ColorPickerDialog;
import com.example.huihua.ColorPickerDialog.OnColorChangedListener;
import com.example.huihua.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

public class MainActivity extends Activity implements OnClickListener {
	private Canvas canvas;
	private Paint mPaint;
	private MotionEvent event;
	private PaintView paintView;
	private ColorPickerDialog colorPickerDialog;
	private Button line;
	private Button penBt;
	private Button triangle;
	private Button colorBt;
	private Button rectangular;
	private Button curve;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("任你涂鸦");
        
        colorPickerDialog = new ColorPickerDialog(this, "点击圆心选择颜色",
    			new OnColorChangedListener() {

    				public void colorChanged(int color) {
    					// TODO Auto-generated method stub
    					Log.i("color---", color+"");
    					paintView.setPenColor(color);
    				}
    			});

        
        line = (Button) findViewById(R.id.line);
		penBt = (Button) findViewById(R.id.pen_bt);
		triangle = (Button) findViewById(R.id.triangle);
		colorBt = (Button) findViewById(R.id.color_bt);
		rectangular = (Button) findViewById(R.id.rectangular);
		curve = (Button) findViewById(R.id.curve);
	    paintView = (PaintView)findViewById(R.id.drawView1);
		line.setOnClickListener(this);
		curve.setOnClickListener(this);
		penBt.setOnClickListener(this);
		triangle.setOnClickListener(this);
		colorBt.setOnClickListener(this);
		rectangular.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	 // 获取当前的菜单
        // Inflate the menu; this adds items to the action bar if it is present.
    	MenuInflater inflater = new MenuInflater(this);//实例化一个MenuInflater对象
        inflater.inflate(R.menu.main, menu);//解析菜单文件
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("WrongCall")
	@Override
	/**
     * 对菜单点击事件处理
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        
        paintView = (PaintView)findViewById(R.id.drawView1);
    	paintView.mPaint.setXfermode(null);//图片相交模式
    	paintView.setPenWidth(paintView.penWidth);//设置空心线宽
    	paintView.setPenColor(paintView.penColor);
    	
    	 switch(item.getItemId()){
	    	 case R.id.red:
	    		 paintView.mPaint.setColor(Color.RED);
	    		 paintView.color="#ff0000";
	     		item.setCheckable(true);
	     		break;
	     	case R.id.green:
	     		paintView.mPaint.setColor(Color.GREEN);
	     		paintView.color="#00ff00";
	     		item.setCheckable(true);
	     		break;
	     	case R.id.blue:
	     		paintView.mPaint.setColor(Color.BLUE);
	     		paintView.color="#0000ff";
	     		item.setCheckable(true);
	     		break;
	     	case R.id.width:
	     		dialog();
	     		break;
	     	case R.id.clear:
	     		
	     		paintView.clear();
	     		dialog1();
	     		break;
	    	case R.id.chexiao:
	    		 paintView.undo();
	     		break;
	     	case R.id.huifu:
	     		paintView.redo();
	     		break;
	     	case R.id.qingping:
	     		paintView.removeAllPaint();
	     		RequestParams params = new RequestParams();
	             HttpUtils http = new HttpUtils();
	             http.send(HttpMethod.POST,
	                     "http://114.55.100.109/home/Indexa/clear_canvas",
	                     params,
	                     new RequestCallBack<String>() {
	                         @Override
	                         public void onSuccess(ResponseInfo<String> responseInfo) {
	                             Log.i("-------",responseInfo.result);

	                         }

	                         @Override
	                         public void onFailure(HttpException e, String s) {

	                         }
	                     });
	     		break;
	     	
	     	case R.id.save:
	     		paintView.saveBitmap();
	     		 Toast.makeText(MainActivity.this, "保存成功", 0).show();
	     		break;	
	     }
	     return true;
	    }
    //画笔宽度对话框
    public  void dialog() {
    	final PaintView paintView = (PaintView)findViewById(R.id.drawView1);
    	
  		 AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
  		final EditText name=new EditText(this);
  		 builder.setView(name);
  		 builder.setTitle("画笔宽度");
         builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int which) {
         String string=name.getText().toString().trim();
         float str=Float.parseFloat(string);
         if(str>0&&str<=100){
        	 paintView.mPaint.setStrokeWidth(str);
    		   paintView.penWidth=str;
     		   Toast.makeText(MainActivity.this, string, 0).show();
    		  dialog.dismiss();//消除对话框
    	 }
         if(str<=0||str>100){
     		   Toast.makeText(MainActivity.this, "请重新输入1-100!", 0).show();
    		  dialog.dismiss();//消除对话框
    	 }
          

		  //MainActivity.this.finish();

  		  }

		
  		  });

  		  builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

         public void onClick(DialogInterface dialog, int which) {

  		  dialog.dismiss();

  		  }

  		  });

  		  builder.create().show();

  		  }
    //颜色宽度对话框
    public  void dialog1() {
    	final PaintView paintView = (PaintView)findViewById(R.id.drawView1);
    	
  		 AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
  		final EditText name=new EditText(this);
  		 builder.setView(name);
  		 builder.setTitle("橡皮擦大小");
         builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int which) {
         String string=name.getText().toString().trim();
         float str=Float.parseFloat(string);
         if(str>0&&str<=100){
        	 paintView.mPaint.setStrokeWidth(str);
    		   paintView.penWidth=str;
     		   Toast.makeText(MainActivity.this, string, 0).show();
    		  dialog.dismiss();//消除对话框
    	 }
         if(str<=0||str>100){
     		   Toast.makeText(MainActivity.this, "请重新输入1-100", 0).show();
    		  dialog.dismiss();//消除对话框
    	 }
		  //MainActivity.this.finish();

  		  }

		
  		  });

  		  builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

         public void onClick(DialogInterface dialog, int which) {

  		  dialog.dismiss();

  		  }

  		  });

  		  builder.create().show();

  		  }
    
	@SuppressLint("WrongCall")
	
	/**
	 * 
	 * 对按钮点击事件的处理 (画笔宽度、取色器、曲线、直线、三角形、矩形)
	 * */
	public void onClick(View v) {
		// TODO Auto-generated method stub
		paintView.mPaint.setXfermode(null);//图片相交模式
		paintView.setPenWidth(paintView.penWidth);//设置空心线宽
		paintView.setPenColor(paintView.penColor);
		switch (v.getId()) {
		case R.id.pen_bt:
		dialog();
			break;
		case R.id.triangle:
			paintView.mShape=3;
			break;
		case R.id.color_bt:
		colorPickerDialog.setmInitialColor(paintView.getPenColor());
		colorPickerDialog.show();
			break;
		case R.id.curve:
			paintView.mShape=1;
				break;
		case R.id.line:
			paintView.mShape=2;
				break;
		case R.id.rectangular:
			paintView.mShape=4;
			break;
		}
	}
}



