package com.example.huihua;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.R.array;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PaintView extends View  {
     
        private Canvas  mCanvas;//画布
        private Path    mPath;//路径
        private Paint   mBitmapPaint = null;
        private Bitmap  mBitmap;//图像
        private JSONArray array;
        Paint mPaint = null;//画笔
        String color="#000000";//默认颜色
         float penWidth = 10f;//默认画笔宽度
        static String pwidth="10";
        private ArrayList<DrawPath> savePath;
        private ArrayList<DrawPath> deletePath;
        private DrawPath dp;//绘制图形
         
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;//接触误差
         
        private int bitmapWidth;//位图的宽
        private int bitmapHeight;//位图的高
        static int penColor = Color.BLACK;
		 int mShape=1;//初始化形状

		 
		 private float sX,sY;  //记录三角形的起点
         
        public PaintView(Context c) {
            super(c);
            //得到屏幕的分辨率
            /*为了获取DisplayMetrics 成员，首先初始化一个对象*/
            DisplayMetrics dm = new DisplayMetrics();
            //窗口机制.得到默认的屏幕.得到分辨率
            ((MainActivity) c).getWindowManager().getDefaultDisplay().getMetrics(dm);
             
            bitmapWidth = dm.widthPixels;//宽的维度
            bitmapHeight = dm.heightPixels - 2 * 45;
             
            initCanvas();//调用初始化画布函数以清空画布
            savePath = new ArrayList<DrawPath>();
            deletePath = new ArrayList<DrawPath>();
             
        }
        public PaintView(Context c,AttributeSet attrs) {
            super(c,attrs);
            //得到屏幕的分辨率
            /*为了获取DisplayMetrics 成员，首先初始化一个对象*/
            DisplayMetrics dm = new DisplayMetrics();
            //窗口机制.得到默认的屏幕.得到分辨率
            ((MainActivity) c).getWindowManager().getDefaultDisplay().getMetrics(dm);
             
            bitmapWidth = dm.widthPixels;//宽的维度
            bitmapHeight = dm.heightPixels - 2 * 45;
             
            initCanvas();//调用初始化画布函数以清空画布
            savePath = new ArrayList<DrawPath>();
            deletePath = new ArrayList<DrawPath>();
             
        }
   
        //初始化画布
        public void initCanvas(){
             
            mPaint = makepaint(penColor,penWidth);
             
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);  //抗抖动画笔    
            array = new JSONArray();
            //画布大小 
            mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, 
                Bitmap.Config.ARGB_8888);//存储色彩的位数  决定  图片质量
            mCanvas = new Canvas(mBitmap);  //所有mCanvas画的东西都被保存在了mBitmap中
             
            mCanvas.drawColor(Color.WHITE);//画布的颜色
            mPath = new Path();
            mBitmapPaint = new Paint();     
        }
		private Paint makepaint(int color,float penWidth2) {
			Paint p = new Paint();
			p = new Paint(Paint.DITHER_FLAG);//抗抖动
            p.setAntiAlias(true);//防锯齿
            p.setDither(true);//防抖动
            p.setColor(color);//默认黑色
            p.setStyle(Paint.Style.STROKE);//画笔风格空心
            p.setStrokeJoin(Paint.Join.ROUND);//画的线圆滑
            p.setStrokeCap(Paint.Cap.ROUND);//画笔笔刷类型圆滑
            p.setStrokeWidth(penWidth2);   //设置空心线宽 
            
            return p;
		}
                
        @Override
        public void onDraw(Canvas canvas) {   
             canvas.drawColor(0xFFFFFFFF);//画布的颜色
             
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);     //显示旧的画布       
            if (mPath != null) {
                // 实时的显示
                canvas.drawPath(mPath, mPaint);//沿着路径绘制图形
            }
            canvas.save(Canvas.ALL_SAVE_FLAG);//保存画布的状态
            canvas.restore();//取出保存的状态
        }
        //路径对象 
        class DrawPath{
            Path path;
            Paint paint;
        }
        //橡皮擦
        void clear(){
        	mPaint.setColor(Color.WHITE);
//        	mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        	color="#ffffff";
        	penColor = Color.WHITE;
        	JSONObject object = new JSONObject();
        	try {
				object.put("color", "#ffffff");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	array.put(object);
        	
//        	mPaint.setStrokeWidth(50);	//设置空心线宽 
//        	pwidth="50";
        }
        
        /**
         * 撤销的核心思想就是将画布清空，
         * 将保存下来的Path路径最后一个移除掉，
         * 重新将路径画在画布上面。
         */
        public void undo(){       
            System.out.println(savePath.size()+"--------------");
            if(savePath != null && savePath.size() > 0){
                //调用初始化画布函数以清空画布
                initCanvas();
                 
                //将路径保存列表中的最后一个元素删除 ,并将其保存在路径删除列表中
                DrawPath drawPath = savePath.get(savePath.size() - 1);//从一个数组中的到相应下标的元素，数组下标是从0开始的
                deletePath.add(drawPath);
                savePath.remove(savePath.size() - 1);
                 
                //将路径保存列表中的路径重绘在画布上  数组的遍历
                Iterator<DrawPath> iter = savePath.iterator(); //重复保存
                while (iter.hasNext()) {
                    DrawPath dp = iter.next();
                    mCanvas.drawPath(dp.path, dp.paint);
                     
                }
                invalidate();// 刷新
            }
        }
        /**
         * 恢复的核心思想就是将撤销的路径保存到另外一个列表里面(栈)，
         * 然后从redo的列表里面取出最顶端对象，
         * 画在画布上面即可
         */
        public void redo(){
            if(deletePath.size() > 0){
                //将删除的路径列表中的最后一个，也就是最顶端路径取出（栈）,并加入路径保存列表中
                DrawPath dp = deletePath.get(deletePath.size() - 1);
                savePath.add(dp);
                //将取出的路径重绘在画布上
                mCanvas.drawPath(dp.path, dp.paint);
                //将该路径从删除的路径列表中去除
                deletePath.remove(deletePath.size() - 1);
                invalidate();
            }
        }
        /*
         * 清空的主要思想就是初始化画布
         * 将保存路径的两个List清空
         * */
        public void removeAllPaint(){
            //调用初始化画布函数以清空画布
            initCanvas();
            invalidate();//刷新
            savePath.clear();
            deletePath.clear();
        }
    
       /* 
        * 保存所绘图形 
        * 返回绘图文件的存储路径
        * */
        @SuppressLint({ "SimpleDateFormat", "SdCardPath" }) public String saveBitmap(){
            //获得系统当前时间，并以该时间作为文件名
            SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");  
            Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间 
            String   str   =   formatter.format(curDate);  
            String paintPath = "";
            str = str + "paint.png";
            File dir = new File("/sdcard/notes/");
            File file = new File("/sdcard/notes/",str);
            if (!dir.exists()) { 
                dir.mkdir(); 
            } 
            else{
                if(file.exists()){
                    file.delete();
                }
            }
             
            try {
                FileOutputStream out = new FileOutputStream(file);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); 
                out.flush(); 
                out.close(); 
                //保存绘图文件路径
                paintPath = "/sdcard/notes/" + str;  
         
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
             
            return paintPath;
        }
         
         
        private void touch_start(float x, float y) {
            mPath.reset();//清空path
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
            Log.i("---mshape", mShape+"-----");
            if(mShape==2){
            	Log.i("star---", mX+"----"+mY);
            	JSONObject o = new JSONObject();
            	try {
					o.put("y", mY);
					o.put("x", mX);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	array.put(o);
            }else if(mShape==3||mShape==4){
				sX = x;
				sY = y;
			}
        }
        private void touch_move(float x, float y) {
        	float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
           
			switch(mShape)  
            {  
            case 1:  
               
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {  
                    mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);  
                    mX = x;  
                    mY = y;  
                    
                    JSONObject object = new JSONObject();
                    try {
                        object.put("y",mY);
                        object.put("x",mX);

                        array.put(object);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }break;  
            case 2:  
                mPath.reset();  
                mPath.moveTo(mX, mY);  
                mPath.lineTo(x, y); 
                

             
                break;  
            case 3:  
            	mPath.reset();  
            	try {
					array = new JSONArray("[]");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	
                mPath.moveTo(mX, mY);  
                mPath.lineTo(mX, y);  
                mPath.lineTo(x, mY); 
                mPath.lineTo(mX, mY); 
                
               
                
                break;  
            case 4:  
                mPath.reset();  
                RectF mRect1 = new RectF();  
                mRect1.set(mX, mY, x, y);  
                mPath.addRect(mRect1, Path.Direction.CW);  
                break;  
            }  
                JSONObject object = new JSONObject();
                try {
                    object.put("y",mY);
                    object.put("x",mX);

                    array.put(object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            
        
        private void touch_up(float x, float y) {
            mPath.lineTo(mX, mY);
            mCanvas.drawPath(mPath, mPaint);
            savePath.add(dp);
            mPath = null;
            if(mShape==2){
            	Log.i("end---", x+"----"+y);
            JSONObject object2 = new JSONObject();
            try {
				object2.put("y", y);
				object2.put("x", x);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			array.put(object2);
            }else if (mShape==3) {
            	 JSONObject o1 = new JSONObject();
                 try {
                 o1.put("y", sY);
                 o1.put("x", sX);
                 JSONObject o2 = new JSONObject();
                 o2.put("y", y);
                 o2.put("x", sX);
                 JSONObject o3 = new JSONObject();
                 o3.put("y", sY);
                 o3.put("x", x);
                 
                 
                 array.put(o1);
                 array.put(o2);
                 array.put(o3);
                 array.put(o1);
                 } catch (JSONException e1) {
 					// TODO Auto-generated catch block
 					e1.printStackTrace();
 				}
			}else if (mShape==4) {
				 JSONObject o1 = new JSONObject();
                 try {
                 o1.put("y", sY);
                 o1.put("x", sX);
                 JSONObject o2 = new JSONObject();
                 o2.put("y", y);
                 o2.put("x", sX);
                 JSONObject o3 = new JSONObject();
                 o3.put("y", y);
                 o3.put("x", x);
                 JSONObject o4 = new JSONObject();
                 o4.put("y", sY);
                 o4.put("x", x);
                 
                 array.put(o1);
                 array.put(o2);
                 array.put(o3);
                 array.put(o4);
                 array.put(o1);
                 } catch (JSONException e1) {
 					// TODO Auto-generated catch block
 					e1.printStackTrace();
 				}
			}
           
            if(array.length()>0){
            	Log.i("arrlength", array.length()+"");
                send();

                try {
                    array = new JSONArray("[]");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                }
        }
         
       @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
             
            switch (event.getAction()) {
            //按下
                case MotionEvent.ACTION_DOWN:
                     
                    mPath = new Path();
                    dp = new DrawPath();
                    
                    dp.path = mPath;
                    
                    
                    dp.paint = makepaint(penColor,penWidth);
                     
                    touch_start(x, y);
                    invalidate(); //更新
                    break;
                    //记录了触摸的x y坐标了
                case MotionEvent.ACTION_MOVE:
                	
                    touch_move(x, y);
                    invalidate();//更新
                    break;
                    //抬起
                case MotionEvent.ACTION_UP:
                	
                    touch_up(x,y);
                    invalidate();//清屏
                    break;
            }
            return true;
        }
	
      //发送网络请求
        public void send(){
        	pwidth=String.valueOf(penWidth);
            RequestParams params = new RequestParams();
            params.addBodyParameter("line_sites",array.toString());
            params.addBodyParameter("line_color",color);
            params.addBodyParameter("line_width",pwidth);
            Log.i("-----", "line_sites：" + array.toString());
            Log.i("-----","line_color："+color);
            Log.i("-----","line_width："+pwidth);
            HttpUtils http = new HttpUtils();
            http.send(HttpMethod.POST,
                    "http://114.55.100.109/home/Indexa/upload_xy",
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
        }
         
        	public int getPenColor() {
        		return penColor;
        		
        	}

        	/**
        	 * 设置画笔颜色
        	 */
        	public void setPenColor(int penColor) {
        		this.penColor = penColor;
        		mPaint.setColor(penColor);
        	}
        	
        	public void setPenWidth(float penWidth2) {
        		this.penWidth = penWidth2;
        		mPaint.setStrokeWidth(penWidth2);
        	}
        	

}
        	







