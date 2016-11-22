/*
 * Copyright 2014 - 2015 Henning Dodenhof
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vgaw.androidtest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CapOrRecView extends ImageView {
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLOR_DRAWABLE_DIMENSION = 2;

    private static final int DEFAULT_BORDER_WIDTH = 2;
    private static final int DEFAULT_BORDER_COLOR = Color.WHITE;
    private static final int DEFAULT_DRAW_MARGIN = 5;
    private static final int DEFAULT_FILL_COLOR = Color.TRANSPARENT;

    private final RectF mBorderRect = new RectF();
    private final Paint mBorderPaint = new Paint();
    private final Paint mDrawColorPaint = new Paint();

    private int mBorderColor;
    private int mBorderWidth;
    private int mDrawMargin;
    private int mDrawColor;
    private boolean mIsAction;

    private Bitmap mBitmap;
    private float mBorderRadius;
    private float mDrawMarginRadius;
    private float mDrawRadius;

    public CapOrRecView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CapOrRecView);

        mBitmap = getBitmapFromDrawable(a.getDrawable(R.styleable.CapOrRecView_drawPic));
        mBorderWidth = a.getDimensionPixelSize(R.styleable.CapOrRecView_borderWidth, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.CapOrRecView_borderColor, DEFAULT_BORDER_COLOR);
        mDrawMargin = a.getDimensionPixelSize(R.styleable.CapOrRecView_drawMargin, DEFAULT_DRAW_MARGIN);
        mDrawColor = a.getColor(R.styleable.CapOrRecView_drawColor, DEFAULT_FILL_COLOR);
        mIsAction = a.getBoolean(R.styleable.CapOrRecView_isAction, false);

        a.recycle();

        setScaleType(ScaleType.CENTER_CROP);
    }

    public void changeMode(){
        mIsAction = !mIsAction;
    }

    public boolean getMode(){
        return mIsAction;
    }

    public void setCenterY(float centerY){
        setY(centerY - getHeight() / 2);
    }

    public float getCenterY(){
        return getY() + getHeight() / 2;
    }

    public void setWidth(int width){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        setLayoutParams(params);
    }

    public void setHeight(int height){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
        setLayoutParams(params);
    }

    public void setDrawColor(int drawColor){
        this.mDrawColor = drawColor;
        invalidate();
    }

    public int getDrawColor(){
        return this.mDrawColor;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 用于绘制边框和drawMargin
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        // 用于绘制可绘制区域
        mDrawColorPaint.setStyle(Paint.Style.FILL);
        mDrawColorPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 白环的宽度（自定义），半径根据宽度和组件宽度算出来
        // 可绘制区域外边距宽度（自定义），半径根据宽度和上面参数结果算出来
        // 可绘制区域内边距（自定义）
        getRadius();

        float centerX = getWidth() / 2.0f;
        float centerY = getHeight() / 2.0f;
        // 绘制边框
        mBorderPaint.setColor(mBorderColor);
        canvas.drawCircle(centerX, centerY, mBorderRadius, mBorderPaint);
        // 绘制drawMargin
        //mBorderPaint.setColor(Color.TRANSPARENT);
        //canvas.drawCircle(centerX, centerY, mDrawMarginRadius, mBorderPaint);
        // 绘制图片
        float temp = getHalfPicWidth();
        canvas.drawBitmap(mBitmap, null, new RectF(centerX - temp, centerY - temp, centerX + temp, centerY + temp), null);
        // 绘制可绘制区域
        if (mDrawColor != Color.TRANSPARENT){
            mDrawColorPaint.setColor(mDrawColor);
            canvas.drawCircle(centerX, centerY, mDrawRadius, mDrawColorPaint);
        }
    }

    private void getRadius(){
        mBorderRect.set(0, 0, getWidth(), getHeight());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2.0f, (mBorderRect.width() - mBorderWidth) / 2.0f);

        mDrawMarginRadius = mBorderRadius - mBorderWidth / 2 - mDrawMargin / 2;
        mDrawRadius = mDrawMarginRadius - mDrawMargin / 2;
    }

    private float getHalfPicWidth(){
        return mDrawRadius / 2;
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}