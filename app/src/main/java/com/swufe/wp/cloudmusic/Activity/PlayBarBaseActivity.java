package com.swufe.wp.cloudmusic.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;

import com.swufe.wp.cloudmusic.R;
import com.swufe.wp.cloudmusic.Fragment.PlaybarFragment;

public abstract class PlayBarBaseActivity extends BaseActivity {

    private PlaybarFragment playBarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        show();
    }

    private void show(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (playBarFragment == null) {
            playBarFragment = PlaybarFragment.newInstance();
            ft.add(R.id.fragment_playbar, playBarFragment).commit();
        }else {
            ft.show(playBarFragment).commit();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
