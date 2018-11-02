package org.pw.musicplayer_server;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MusicService extends Service {
	public MediaPlayer mMediaPlayer = null;
	private MyBinder mBinder = null;

	public class MyBinder extends IMyBinder.Stub {
		@Override
		public void play() throws RemoteException {
			if (mMediaPlayer != null) {
				mMediaPlayer.start();
			}
		}
		
		@Override
		public void pause() throws RemoteException {
			if (mMediaPlayer != null) {
				mMediaPlayer.pause();
			}
		}
		
		public int getDuration() {
			return mMediaPlayer.getDuration();
		}
		
		public int getCurPos() {
			return mMediaPlayer.getCurrentPosition();
		}
		
		public boolean isPlaying() {
			return mMediaPlayer.isPlaying();
		}
		
		public void setProgress(int progress) {
			mMediaPlayer.seekTo(progress);
		}
	}
	
	private Thread mThread = new Thread()
	{
		public void run() {
			
		}
	};
	
	@Override
	public void onCreate() {
		Log.d("server", "onCreate");
		super.onCreate();
		mMediaPlayer = MediaPlayer.create(this, R.raw.lilium);
		try {
			mMediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mBinder = new MyBinder();
		
		mThread.start();
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("server", "onStartCommand");
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("server", "onBind");
		return mBinder;
	}

	@Override
	public void onDestroy() {
		Log.d("server", "onDestroy");
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		mThread = null;
		mBinder = null;
		super.onDestroy();
	}
}
