package com.swufe.wp.cloudmusic.Activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.swufe.wp.cloudmusic.Database.DBManager;
import com.swufe.wp.cloudmusic.Fragment.PlaybarFragment;
import com.swufe.wp.cloudmusic.R;
import com.swufe.wp.cloudmusic.Receiver.PlayerManagerReceiver;
import com.swufe.wp.cloudmusic.Service.MusicService;
import com.swufe.wp.cloudmusic.utils.Constant;
import com.swufe.wp.cloudmusic.utils.CustomAttrValueUtil;
import com.swufe.wp.cloudmusic.utils.DisplayUtil;
import com.swufe.wp.cloudmusic.utils.FastBlurUtil;
import com.swufe.wp.cloudmusic.utils.MergeImage;
import com.swufe.wp.cloudmusic.utils.MyMusicUtil;
import com.swufe.wp.cloudmusic.View.PlayingPopWindow;

import java.util.Locale;

public class PlayActivity extends BaseActivity implements  View.OnClickListener {

    private static final String TAG = PlayActivity.class.getName();
    private DBManager dbManager;
    private ImageView backIv;
    private ImageView playIv;
    private ImageView menuIv;
    private ImageView preIv;
    private ImageView nextIv;
    private ImageView modeIv;
    private ImageView bgImgv;
    private ImageView discImagv;
    private ImageView needleImagv;

    private TextView curTimeTv;
    private TextView totalTimeTv;

    private TextView musicNameTv;
    private TextView singerNameTv;

    private SeekBar seekBar;

    private PlayReceiver mReceiver;

    private boolean isStop;

    private int mProgress;
    private int duration;
    private int current;

    private ObjectAnimator objectAnimator = null;
    private RotateAnimation rotateAnimation = null;
    private RotateAnimation rotateAnimation2 = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            seekBar.setProgress(msg.what);
            curTimeTv.setText(formatTime(msg.what));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        dbManager = DBManager.getInstance(PlayActivity.this);
        setStyle();
        initView();
        register();
    }




    private void initView() {
        backIv = findViewById(R.id.iv_back);
        playIv = findViewById(R.id.iv_play);
        menuIv = findViewById(R.id.iv_menu);
        preIv = findViewById(R.id.iv_prev);
        nextIv = findViewById(R.id.iv_next);
        modeIv = findViewById(R.id.iv_mode);
        curTimeTv = findViewById(R.id.tv_current_time);
        totalTimeTv =  findViewById(R.id.tv_total_time);
        musicNameTv = findViewById(R.id.tv_title);
        singerNameTv =  findViewById(R.id.tv_artist);
        seekBar =  findViewById(R.id.activity_play_seekbar);
        discImagv = findViewById(R.id.music_disc_imagv);
        needleImagv = findViewById(R.id.music_needle_imag);
        bgImgv=findViewById(R.id.music_bg_imgv);


        backIv.setOnClickListener(this);
        playIv.setOnClickListener(this);
        menuIv.setOnClickListener(this);
        preIv.setOnClickListener(this);
        nextIv.setOnClickListener(this);
        modeIv.setOnClickListener(this);
        setSeekBarBg();
        initPlayMode();
        initTitle();
        initBip();
        initPlayIv();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                seekBar_touch = true;	//可以拖动标志
                int musicId = MyMusicUtil.getIntShared(Constant.KEY_ID);
                if (musicId == -1) {
                    Intent intent = new Intent(MusicService.PLAYER_MANAGER_ACTION);
                    intent.putExtra("cmd", Constant.COMMAND_STOP);
                    sendBroadcast(intent);
                    Toast.makeText(PlayActivity.this, "歌曲不存在", Toast.LENGTH_LONG).show();
                    return;
                }

                //发送播放请求
                Intent intent = new Intent(MusicService.PLAYER_MANAGER_ACTION);
                intent.putExtra("cmd", Constant.COMMAND_PROGRESS);
                intent.putExtra("current", mProgress);
                sendBroadcast(intent);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                mProgress = progress;
                initTime();
            }
        });
    }



    private void initBip() {
        isStop = false;
        int musicId = MyMusicUtil.getIntShared(Constant.KEY_ID);
        String albumbip = getAlbumArt(dbManager.getMusicInfo(musicId).get(9));
        Bitmap bip = null;
        if (albumbip != null) {
            bip = BitmapFactory.decodeFile(albumbip);
        } else {
            bip = BitmapFactory.decodeResource(getResources(), R.mipmap.changpian);
        }
        final float widthHeightSize = (float) (DisplayUtil.getScreenWidth(PlayActivity.this)
                * 1.0 / DisplayUtil.getScreenHeight(this) * 1.0);
        int cropBitmapWidth = (int) (widthHeightSize * bip.getHeight());
        int cropBitmapWidthX = (int) ((bip.getWidth() - cropBitmapWidth) / 2.0);
        /*切割部分图片*/
        Bitmap cropBitmap = Bitmap.createBitmap(bip, cropBitmapWidthX, 0, cropBitmapWidth,
                bip.getHeight());
        /*缩小图片*/
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(cropBitmap, bip.getWidth() / 50, bip
                .getHeight() / 50, false);
        /*模糊化*/
        if (bip != null) {
            Bitmap bgbm =  FastBlurUtil.doBlur(scaleBitmap, 8, true);//将专辑虚化

            /*Bitmap bitmap = Bitmap.createBitmap(DisplayUtil.getScreenWidth(PlayActivity.this),DisplayUtil.getScreenHeight(PlayActivity.this), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bgbm, 0, 0, null);
            Canvas canvas1 = new Canvas(bitmap);
            canvas1.drawColor(0x33000000);
            bgImgv.setImageBitmap(bitmap);*/
            Drawable foregroundDrawable = new BitmapDrawable(bgbm);
            foregroundDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            bgImgv.setImageBitmap(bgbm);                                    //设置虚化后的专辑图片为背景
            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.play_page_disc);//BitmapFactory.decodeResource用于根据给定的资源ID从指定的资源文件中解析、创建Bitmap对象。
            Bitmap bm = MergeImage.mergeThumbnailBitmap(bitmap1,bip);//将专辑图片放到圆盘中
            discImagv.setImageBitmap(bm);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.changpian);
            bgImgv.setImageBitmap(bitmap);
            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.play_page_disc);
            Bitmap bm = MergeImage.mergeThumbnailBitmap(bitmap1, bitmap);
            discImagv.setImageBitmap(bm);
        }


        //实例化，设置旋转对象
        objectAnimator = ObjectAnimator.ofFloat(discImagv, "rotation", 0f, 360f);
        //设置转一圈要多长时间
        objectAnimator.setDuration(8000);
        //设置旋转速率
        objectAnimator.setInterpolator(new LinearInterpolator());
        //设置循环次数 -1为一直循环
        objectAnimator.setRepeatCount(-1);
        //设置转一圈后怎么转
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        int status = PlayerManagerReceiver.status;
        if(status==Constant.STATUS_PLAY) {
            objectAnimator.start();
        }
        rotateAnimation = new RotateAnimation(-25f, 0f, Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF, 0.1f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(0);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setStartOffset(500);
        needleImagv.setAnimation(rotateAnimation);
        rotateAnimation.cancel();


        rotateAnimation2 = new RotateAnimation(0f, -25f, Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF, 0.1f);
        rotateAnimation2.setDuration(500);
        rotateAnimation2.setInterpolator(new LinearInterpolator());
        rotateAnimation2.setRepeatCount(0);
        rotateAnimation2.setFillAfter(true);
        needleImagv.setAnimation(rotateAnimation2);
        rotateAnimation2.cancel();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_mode:
                switchPlayMode();
                break;
            case R.id.iv_play:
                play();
                break;
            case R.id.iv_next:
                objectAnimator.pause();
                needleImagv.startAnimation(rotateAnimation2);
                MyMusicUtil.playNextMusic(this);
                initBip();
                break;
            case R.id.iv_prev:
                objectAnimator.pause();
                needleImagv.startAnimation(rotateAnimation2);
                MyMusicUtil.playPreMusic(this);
                initBip();
                break;
            case R.id.iv_menu:
                showPopFormBottom();
                break;
        }
    }


    private void initPlayIv(){
        int status = PlayerManagerReceiver.status;
        switch (status) {
            case Constant.STATUS_STOP:
                playIv.setSelected(false);
                break;
            case Constant.STATUS_PLAY:
                playIv.setSelected(true);
                break;
            case Constant.STATUS_PAUSE:
                playIv.setSelected(false);
                break;
            case Constant.STATUS_RUN:
                playIv.setSelected(true);
                break;
        }
    }

    private void initPlayMode() {
        int playMode = MyMusicUtil.getIntShared(Constant.KEY_MODE);
        if (playMode == -1) {
            playMode = 0;
        }
        modeIv.setImageLevel(playMode);
    }

    private void initTitle() {
        int musicId = MyMusicUtil.getIntShared(Constant.KEY_ID);
        if (musicId == -1) {
            musicNameTv.setText("网易云音乐");
            singerNameTv.setText("好音质");
        } else {
            musicNameTv.setText(dbManager.getMusicInfo(musicId).get(1));
            singerNameTv.setText(dbManager.getMusicInfo(musicId).get(2));

        }
    }

    private void initTime() {
        curTimeTv.setText(formatTime(current));
        totalTimeTv.setText(formatTime(duration));
//        if (progress - mLastProgress >= 1000) {
//            tvCurrentTime.setText(formatTime(progress));
//            mLastProgress = progress;
//        }
    }

    private String formatTime(long time) {
        return formatTime("mm:ss", time);
    }

    public static String formatTime(String pattern, long milli) {
        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String mm = String.format(Locale.getDefault(), "%02d", m);
        String ss = String.format(Locale.getDefault(), "%02d", s);
        return pattern.replace("mm", mm).replace("ss", ss);
    }

    private void switchPlayMode() {
        int playMode = MyMusicUtil.getIntShared(Constant.KEY_MODE);
        switch (playMode) {
            case Constant.PLAYMODE_SEQUENCE:
                MyMusicUtil.setShared(Constant.KEY_MODE, Constant.PLAYMODE_RANDOM);
                break;
            case Constant.PLAYMODE_RANDOM:
                MyMusicUtil.setShared(Constant.KEY_MODE, Constant.PLAYMODE_SINGLE_REPEAT);
                break;
            case Constant.PLAYMODE_SINGLE_REPEAT:
                MyMusicUtil.setShared(Constant.KEY_MODE, Constant.PLAYMODE_SEQUENCE);
                break;
        }
        initPlayMode();
    }

    private void setSeekBarBg(){
        try {
            int progressColor = CustomAttrValueUtil.getAttrColorValue(R.attr.colorPrimary,R.color.colorAccent,this);
            LayerDrawable layerDrawable = (LayerDrawable) seekBar.getProgressDrawable();
            ScaleDrawable scaleDrawable = (ScaleDrawable)layerDrawable.findDrawableByLayerId(android.R.id.progress);
            GradientDrawable drawable = (GradientDrawable) scaleDrawable.getDrawable();
            drawable.setColor(progressColor);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void play() {
        int musicId;
        musicId = MyMusicUtil.getIntShared(Constant.KEY_ID);
        if (musicId == -1 || musicId == 0) {
            musicId = dbManager.getFirstId(Constant.LIST_ALLMUSIC);
            Intent intent = new Intent(Constant.MP_FILTER);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_STOP);
            sendBroadcast(intent);
            Toast.makeText(PlayActivity.this, "歌曲不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        //如果当前媒体在播放音乐状态，则图片显示暂停图片，按下播放键，则发送暂停媒体命令，图片显示播放图片。以此类推。
        if (PlayerManagerReceiver.status == Constant.STATUS_PAUSE) {
            Intent intent = new Intent(MusicService.PLAYER_MANAGER_ACTION);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);
            objectAnimator.resume();
            needleImagv.startAnimation(rotateAnimation);
            sendBroadcast(intent);
        } else if (PlayerManagerReceiver.status == Constant.STATUS_PLAY) {
            Intent intent = new Intent(MusicService.PLAYER_MANAGER_ACTION);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_PAUSE);
            objectAnimator.pause();
            needleImagv.startAnimation(rotateAnimation2);
            sendBroadcast(intent);
        } else {
            //为停止状态时发送播放命令，并发送将要播放歌曲的路径
            String path = dbManager.getMusicPath(musicId);
            Intent intent = new Intent(MusicService.PLAYER_MANAGER_ACTION);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);
            intent.putExtra(Constant.KEY_PATH, path);
            Log.i(TAG, "onClick: path = " + path);
            sendBroadcast(intent);
        }
    }

    public void showPopFormBottom() {
        PlayingPopWindow playingPopWindow = new PlayingPopWindow(PlayActivity.this);
        playingPopWindow.showAtLocation(findViewById(R.id.activity_play), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha=0.7f;
        getWindow().setAttributes(params);

        playingPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha=1f;
                getWindow().setAttributes(params);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegister();
    }

    private void register() {
        mReceiver = new PlayReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlaybarFragment.ACTION_UPDATE_UI_PlayBar);
        registerReceiver(mReceiver, intentFilter);
    }

    private void unRegister() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    class PlayReceiver extends BroadcastReceiver {

        int status;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            initTitle();
            status = intent.getIntExtra(Constant.STATUS, 0);
            current = intent.getIntExtra(Constant.KEY_CURRENT, 0);
            duration = intent.getIntExtra(Constant.KEY_DURATION, 100);
            switch (status) {
                case Constant.STATUS_STOP:
                    playIv.setSelected(false);
                    break;
                case Constant.STATUS_PLAY:
                    playIv.setSelected(true);
                    break;
                case Constant.STATUS_PAUSE:
                    playIv.setSelected(false);
                    break;
                case Constant.STATUS_RUN:
                    playIv.setSelected(true);
                    seekBar.setMax(duration);
                    seekBar.setProgress(current);
                    break;
                default:
                    break;
            }

        }
    }

    private void setStyle() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
    private String getAlbumArt(String album_id)

    {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[] { "album_art" };
        Cursor cur = this.getContentResolver().query(Uri.parse(mUriAlbums + "/" + album_id), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0)
        { cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }
}

