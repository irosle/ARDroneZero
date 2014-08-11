package jp.naist.ardronezero;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.codeminders.ardrone.ARDrone;


public class ARDroneZeroDrawGame extends SurfaceView implements SurfaceHolder.Callback{
	static ARDrone drone;
	private static final String TAG = "AR.DroneZero GameDraw";
	private SurfaceHolder holder;
	private Bitmap mImage;
	public ARDroneZeroDrawGame(Context c){//constructor
		super(c);
		holder = getHolder();
	    holder.addCallback(this);
	    mImage = BitmapFactory.decodeResource(c.getResources(), R.drawable.test_background);
		//drone = MainActivity.getARDroneInstance();
	}

	//SurfaceView生成時に呼び出される
	public void surfaceCreated(SurfaceHolder holder) {
		//初期描画(生成タイミングで描画する必要があるもの)
		
		//Prepare drawing
	    Paint paint = new Paint();
	    //paint.setTextSize(24);
	    paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
	    //holder.setFormat(PixelFormat.TRANSLUCENT);
	    //setZOrderOnTop(true);

	    //Canvasの取得(マルチスレッド環境対応のためLock)
	    Canvas canvas = holder.lockCanvas();

	    //描画処理(Lock中なのでなるべく早く)
	    //+++not scaling draw+++
	    //canvas.drawBitmap(mImage, 0, 0, paint);//not scaling
	    //+++scaling and draw+++
	    final int c_w = canvas.getWidth();
	    final int c_h = canvas.getHeight();
	    Bitmap scaledBmp = Bitmap.createScaledBitmap(mImage, c_w, c_h , true);  
	    canvas.drawBitmap( Bitmap.createScaledBitmap(scaledBmp, c_w, c_h , true), 0, 0, paint);//scaling and draw
	    
        // 塗りつぶし無しの円を描画
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
	    canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, 200, paint);
	 
	    //LockしたCanvasを解放、ほかの描画処理スレッドがあればそちらに。
	    holder.unlockCanvasAndPost(canvas);
	}
	 
	//SurfaceView変更時に呼び出される
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	 
	}
	 
	//SurfaceView破棄時に呼び出される
	public void surfaceDestroyed(SurfaceHolder holder) {
	 
	}
}
