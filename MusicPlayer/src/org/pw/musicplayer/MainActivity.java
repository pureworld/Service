package org.pw.musicplayer;

import org.pw.musicplayer_server.IMyBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private IMyBinder mBinder = null;
	ServiceConnection mConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			//���ӳɹ�
			Log.d("client", "onServiceConnected");
			mBinder = IMyBinder.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBinder = null;
		}
	};

	SeekBar mBar = null;
	Thread mThread = null;
	boolean mIsSel = false;
	ImageView mPlayImageView = null;
	ImageView mModeImageView = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final Intent intent = new Intent();
        intent.setAction("51asm");
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        
        mBar = (SeekBar) findViewById(R.id.seekBar1);
        mPlayImageView = (ImageView) findViewById(R.id.img_play);
        mPlayImageView.setOnClickListener(this);
       
        mModeImageView = (ImageView) findViewById(R.id.img_mode);
        mModeImageView.setOnClickListener(this);
        
        findViewById(R.id.img_lastsong).setOnClickListener(this);
        findViewById(R.id.img_nextsong).setOnClickListener(this);
        findViewById(R.id.img_download).setOnClickListener(this);
        
		mThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while (mBinder.isPlaying() && !mIsSel) {
						mBar.setProgress(mBinder.getCurPos());
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
			}
		});
        
        mBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
        	private int mProgress = 0;
        	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {	
				try {
					mBinder.setProgress(mProgress);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mIsSel = false;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mIsSel = true;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				seekBar.setProgress(progress);
				
				if (fromUser) {
					mProgress = progress;
					
				}
			}
		});
    }
    
    
    
    @Override
    protected void onDestroy() {

    	super.onDestroy();
    }

    boolean mFirstTime = true;
    boolean mIsPlaying = false;
    int [] mModeImage = {
    		R.drawable.play_single,
    		R.drawable.play_mul,
    		R.drawable.play_random,
    };
    String [] mModeStrings = {
    	"����ѭ��",
    	"�б�ѭ��",
    	"�������",
    };
    int mCurMode = 0;
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_play:
			try {
				if (!mIsPlaying) {			
					mBinder.play();
					mIsPlaying = true;
					mPlayImageView.setImageResource(R.drawable.pause);
					if (mFirstTime) {
						mBar.setMax(mBinder.getDuration());		
						mThread.start();
						mFirstTime = false;
					}
				} else {
					mBinder.pause();
					mIsPlaying = false;
					mPlayImageView.setImageResource(R.drawable.play);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		case R.id.img_lastsong:
		case R.id.img_nextsong:
			Toast.makeText(this, "�������һ��", Toast.LENGTH_SHORT).show();
			break;
		case R.id.img_mode:
			mCurMode = (mCurMode + 1) % 3;
			mModeImageView.setImageResource(mModeImage[mCurMode]);
			Toast.makeText(this, mModeStrings[mCurMode], Toast.LENGTH_SHORT).show();
			break;
		case R.id.img_download:
			Toast.makeText(this, "�޷����ӷ�����", Toast.LENGTH_SHORT).show();
			break;
		}
		
	}
}
