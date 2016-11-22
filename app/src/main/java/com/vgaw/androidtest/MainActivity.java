package com.vgaw.androidtest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by caojin on 2016/10/25.
 */

public class MainActivity extends Activity {
    private CapOrRecView iv_camera;
    private CapOrRecView iv_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_camera = (CapOrRecView) findViewById(R.id.iv_camera);
        iv_record = (CapOrRecView) findViewById(R.id.iv_record);
        iv_camera.setOnClickListener(listener);
        iv_record.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (((CapOrRecView)v).getMode()){
                getAction(v);
            }else {
                getChangePosition(v);
            }
        }
    };

    private void getChangePosition(View view){
        AnimatorSet set = new AnimatorSet();
        set.play(getAnimator(iv_camera, iv_record)).with(getAnimator(iv_record, iv_camera));
        set.start();
        if (view.getId() == R.id.iv_camera){
            Log.e("fuck", "change$camera$");
        }else {
            Log.e("fuck", "change$record$");
        }
    }

    private void getAction(View view){
        if (view.getId() == R.id.iv_camera){
            Log.e("fuck", "action$camera$");
        }else {
            Log.e("fuck", "action$record$");
        }
    }

    private ObjectAnimator getAnimator(final CapOrRecView view, final CapOrRecView theOther){
        float dy = getCenterY(theOther) - getCenterY(view);
        int widthStart = view.getWidth();
        int widthEnd = theOther.getWidth();
        final float start = view.getCenterY();
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("centerY", start, start + dy);
        PropertyValuesHolder pvhWidth = PropertyValuesHolder.ofInt("width", widthStart, widthEnd);
        PropertyValuesHolder pvhHeight = PropertyValuesHolder.ofInt("height", widthStart, widthEnd);
        PropertyValuesHolder pvhDrawColor;
        if (view == iv_camera){
            if (dy > 0){
                pvhDrawColor = PropertyValuesHolder.ofInt("drawColor", Color.parseColor("#00FFFFFF"), Color.WHITE);
            }else {
                pvhDrawColor = PropertyValuesHolder.ofInt("drawColor", Color.WHITE, Color.parseColor("#00FFFFFF"));
            }
        }else {
            if (dy > 0){
                pvhDrawColor = PropertyValuesHolder.ofInt("drawColor", Color.parseColor("#00FF0000"), Color.RED);
            }else {
                pvhDrawColor = PropertyValuesHolder.ofInt("drawColor", Color.RED, Color.parseColor("#00FF0000"));
            }
        }
        pvhDrawColor.setEvaluator(new IntEvaluator(){
            @Override
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return blendColors(startValue, endValue, fraction);
            }
        });
        ObjectAnimator a1 = ObjectAnimator.ofPropertyValuesHolder(view, pvhY, pvhWidth, pvhHeight, pvhDrawColor);
        a1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setClickable(true);
                view.changeMode();
            }
        });
        return a1;
    }

    private int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float a = (Color.alpha(color1) * inverseRation) + (Color.alpha(color2) * ratio);
        return Color.argb((int) a, Color.red(color1), Color.green(color1), Color.blue(color1));
    }

    private float getCenterY(View view){
        return view.getY() + view.getHeight() / 2;
    }
}
