package jp.naist.ardronezero;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.codeminders.ardrone.ARDrone;

public class ARDroneZeroDrawGame 	extends SurfaceView 
									implements Runnable,SurfaceHolder.Callback
{
	Thread thread;
	private SurfaceHolder holder;
	public int screenWidth, screenHeight;

	static ARDrone drone;
	private static final String TAG = "AR.DroneZero GameDraw";
	private Bitmap mImage;
	// private Bitmap[] mEnemy = new Bitmap[2];
	private ARDroneZeroDynamicObject[] dynamicObj;

	static final int mFPS = 30;
	static final long mFRAMETIME = 1000 / (long) mFPS;
	
	static int numOfObject = 10;

	public ARDroneZeroDrawGame(Context c) {// constructor
		super(c);
		holder = getHolder();
		holder.addCallback(this);
		dynamicObj = new ARDroneZeroDynamicObject[ numOfObject ];
	}
	public boolean loadImage(Context c){
		mImage = BitmapFactory.decodeResource(c.getResources(),
				R.drawable.test_background);

		if( mImage == null){
			Log.e(TAG, "cannot read test_background");
			return false;
		}
		for(int i = 0 ; i < dynamicObj.length ; ++i){
			dynamicObj[i] = new ARDroneZeroDynamicObject(c, 0);//ここでimage readとobject初期化を一緒にしているけどいいの?
		}
		// drone = MainActivity.getARDroneInstance();
		return true;
	}

	@Override
	public void run() {
		Log.e(TAG, "runnable");

		long startTime = 0;
		// 初期描画(生成タイミングで描画する必要があるもの)
		while (thread != null) {// until thread is terminated
			try {
				startTime = System.currentTimeMillis();

				// Prepare drawing
				Paint paint = new Paint();
				// paint.setTextSize(24);
				paint.setColor(Color.WHITE);
				paint.setAntiAlias(true);
				// holder.setFormat(PixelFormat.TRANSLUCENT);
				// setZOrderOnTop(true);

				// Canvasの取得(マルチスレッド環境対応のためLock)
				Canvas canvas = holder.lockCanvas();

				// 描画処理(Lock中なのでなるべく早く)
				// [1]Background
				// +++not scaling draw+++
				// canvas.drawBitmap(mImage, 0, 0, paint);//not scaling
				// +++scaling and draw+++
				canvas.drawBitmap(Bitmap.createScaledBitmap(mImage,
						canvas.getWidth(), canvas.getHeight(), true), 0, 0,
						paint);// scaling and draw

				// [2]Enemy
				for(int i = 0 ;  i < dynamicObj.length ; ++i){
					dynamicObj[i].move();
					canvas.drawBitmap(	dynamicObj[i].getImage(),
										dynamicObj[i].getPosX(),
										dynamicObj[i].getPosY(),
										paint);
				}

				// [3] target Circle
				// 塗りつぶし無しの円を描画
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(5);
				int cx = (int) (canvas.getWidth() / 2);
				int cy = (int) (canvas.getHeight() / 2);
				canvas.drawCircle(cx, cy, 200, paint);

				// LockしたCanvasを解放、ほかの描画処理スレッドがあればそちらに。
				holder.unlockCanvasAndPost(canvas);

				// Sleep
				// System.currentTimeMillis() - startTime -> the time for
				// drawing
				// mFRAMETIME -> 1.0[sec] / fps
				long waitTime = mFRAMETIME - 
						(System.currentTimeMillis() - startTime);
				if (waitTime > 0) {
					Thread.sleep(waitTime);
				}
			} catch (Exception e) {
				;
			}

		}

	}

	// SurfaceView生成時に呼び出される
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");
		thread = new Thread(this);

	}

	// SurfaceView変更時に呼び出される
	//ここで初めて画面サイズが分かるのｄえ、ここから描画処理を開始するのが正しい
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.e(TAG, "surfaceChanged");
		screenWidth = width;
		screenHeight = height;
		for(int i = 0 ; i < dynamicObj.length ; ++i){
			dynamicObj[i].setWindowSize(screenWidth, screenHeight);
		}
		thread.start();
	}

	// SurfaceView破棄時に呼び出される
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread = null;
	}
}
