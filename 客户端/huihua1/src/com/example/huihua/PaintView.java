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
     
        private Canvas  mCanvas;//����
        private Path    mPath;//·��
        private Paint   mBitmapPaint = null;
        private Bitmap  mBitmap;//ͼ��
        private JSONArray array;
        Paint mPaint = null;//����
        String color="#000000";//Ĭ����ɫ
         float penWidth = 10f;//Ĭ�ϻ��ʿ��
        static String pwidth="10";
        private ArrayList<DrawPath> savePath;
        private ArrayList<DrawPath> deletePath;
        private DrawPath dp;//����ͼ��
         
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;//�Ӵ����
         
        private int bitmapWidth;//λͼ�Ŀ�
        private int bitmapHeight;//λͼ�ĸ�
        static int penColor = Color.BLACK;
		 int mShape=1;//��ʼ����״

		 
		 private float sX,sY;  //��¼�����ε����
         
        public PaintView(Context c) {
            super(c);
            //�õ���Ļ�ķֱ���
            /*Ϊ�˻�ȡDisplayMetrics ��Ա�����ȳ�ʼ��һ������*/
            DisplayMetrics dm = new DisplayMetrics();
            //���ڻ���.�õ�Ĭ�ϵ���Ļ.�õ��ֱ���
            ((MainActivity) c).getWindowManager().getDefaultDisplay().getMetrics(dm);
             
            bitmapWidth = dm.widthPixels;//���ά��
            bitmapHeight = dm.heightPixels - 2 * 45;
             
            initCanvas();//���ó�ʼ��������������ջ���
            savePath = new ArrayList<DrawPath>();
            deletePath = new ArrayList<DrawPath>();
             
        }
        public PaintView(Context c,AttributeSet attrs) {
            super(c,attrs);
            //�õ���Ļ�ķֱ���
            /*Ϊ�˻�ȡDisplayMetrics ��Ա�����ȳ�ʼ��һ������*/
            DisplayMetrics dm = new DisplayMetrics();
            //���ڻ���.�õ�Ĭ�ϵ���Ļ.�õ��ֱ���
            ((MainActivity) c).getWindowManager().getDefaultDisplay().getMetrics(dm);
             
            bitmapWidth = dm.widthPixels;//���ά��
            bitmapHeight = dm.heightPixels - 2 * 45;
             
            initCanvas();//���ó�ʼ��������������ջ���
            savePath = new ArrayList<DrawPath>();
            deletePath = new ArrayList<DrawPath>();
             
        }
   
        //��ʼ������
        public void initCanvas(){
             
            mPaint = makepaint(penColor,penWidth);
             
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);  //����������    
            array = new JSONArray();
            //������С 
            mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, 
                Bitmap.Config.ARGB_8888);//�洢ɫ�ʵ�λ��  ����  ͼƬ����
            mCanvas = new Canvas(mBitmap);  //����mCanvas���Ķ���������������mBitmap��
             
            mCanvas.drawColor(Color.WHITE);//��������ɫ
            mPath = new Path();
            mBitmapPaint = new Paint();     
        }
		private Paint makepaint(int color,float penWidth2) {
			Paint p = new Paint();
			p = new Paint(Paint.DITHER_FLAG);//������
            p.setAntiAlias(true);//�����
            p.setDither(true);//������
            p.setColor(color);//Ĭ�Ϻ�ɫ
            p.setStyle(Paint.Style.STROKE);//���ʷ�����
            p.setStrokeJoin(Paint.Join.ROUND);//������Բ��
            p.setStrokeCap(Paint.Cap.ROUND);//���ʱ�ˢ����Բ��
            p.setStrokeWidth(penWidth2);   //���ÿ����߿� 
            
            return p;
		}
                
        @Override
        public void onDraw(Canvas canvas) {   
             canvas.drawColor(0xFFFFFFFF);//��������ɫ
             
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);     //��ʾ�ɵĻ���       
            if (mPath != null) {
                // ʵʱ����ʾ
                canvas.drawPath(mPath, mPaint);//����·������ͼ��
            }
            canvas.save(Canvas.ALL_SAVE_FLAG);//���滭����״̬
            canvas.restore();//ȡ�������״̬
        }
        //·������ 
        class DrawPath{
            Path path;
            Paint paint;
        }
        //��Ƥ��
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
        	
//        	mPaint.setStrokeWidth(50);	//���ÿ����߿� 
//        	pwidth="50";
        }
        
        /**
         * �����ĺ���˼����ǽ�������գ�
         * ������������Path·�����һ���Ƴ�����
         * ���½�·�����ڻ������档
         */
        public void undo(){       
            System.out.println(savePath.size()+"--------------");
            if(savePath != null && savePath.size() > 0){
                //���ó�ʼ��������������ջ���
                initCanvas();
                 
                //��·�������б��е����һ��Ԫ��ɾ�� ,�����䱣����·��ɾ���б���
                DrawPath drawPath = savePath.get(savePath.size() - 1);//��һ�������еĵ���Ӧ�±��Ԫ�أ������±��Ǵ�0��ʼ��
                deletePath.add(drawPath);
                savePath.remove(savePath.size() - 1);
                 
                //��·�������б��е�·���ػ��ڻ�����  ����ı���
                Iterator<DrawPath> iter = savePath.iterator(); //�ظ�����
                while (iter.hasNext()) {
                    DrawPath dp = iter.next();
                    mCanvas.drawPath(dp.path, dp.paint);
                     
                }
                invalidate();// ˢ��
            }
        }
        /**
         * �ָ��ĺ���˼����ǽ�������·�����浽����һ���б�����(ջ)��
         * Ȼ���redo���б�����ȡ����˶���
         * ���ڻ������漴��
         */
        public void redo(){
            if(deletePath.size() > 0){
                //��ɾ����·���б��е����һ����Ҳ�������·��ȡ����ջ��,������·�������б���
                DrawPath dp = deletePath.get(deletePath.size() - 1);
                savePath.add(dp);
                //��ȡ����·���ػ��ڻ�����
                mCanvas.drawPath(dp.path, dp.paint);
                //����·����ɾ����·���б���ȥ��
                deletePath.remove(deletePath.size() - 1);
                invalidate();
            }
        }
        /*
         * ��յ���Ҫ˼����ǳ�ʼ������
         * ������·��������List���
         * */
        public void removeAllPaint(){
            //���ó�ʼ��������������ջ���
            initCanvas();
            invalidate();//ˢ��
            savePath.clear();
            deletePath.clear();
        }
    
       /* 
        * ��������ͼ�� 
        * ���ػ�ͼ�ļ��Ĵ洢·��
        * */
        @SuppressLint({ "SimpleDateFormat", "SdCardPath" }) public String saveBitmap(){
            //���ϵͳ��ǰʱ�䣬���Ը�ʱ����Ϊ�ļ���
            SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");  
            Date   curDate   =   new   Date(System.currentTimeMillis());//��ȡ��ǰʱ�� 
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
                //�����ͼ�ļ�·��
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
            mPath.reset();//���path
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
            //����
                case MotionEvent.ACTION_DOWN:
                     
                    mPath = new Path();
                    dp = new DrawPath();
                    
                    dp.path = mPath;
                    
                    
                    dp.paint = makepaint(penColor,penWidth);
                     
                    touch_start(x, y);
                    invalidate(); //����
                    break;
                    //��¼�˴�����x y������
                case MotionEvent.ACTION_MOVE:
                	
                    touch_move(x, y);
                    invalidate();//����
                    break;
                    //̧��
                case MotionEvent.ACTION_UP:
                	
                    touch_up(x,y);
                    invalidate();//����
                    break;
            }
            return true;
        }
	
      //������������
        public void send(){
        	pwidth=String.valueOf(penWidth);
            RequestParams params = new RequestParams();
            params.addBodyParameter("line_sites",array.toString());
            params.addBodyParameter("line_color",color);
            params.addBodyParameter("line_width",pwidth);
            Log.i("-----", "line_sites��" + array.toString());
            Log.i("-----","line_color��"+color);
            Log.i("-----","line_width��"+pwidth);
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
        	 * ���û�����ɫ
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
        	







