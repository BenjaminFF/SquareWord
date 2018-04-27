package com.example.benja.squareword.GestureLock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.benja.squareword.R;

/**
 * Created by benja on 2018/4/23.
 */

public class ItemView extends View {

    private int mColor_Inner_NoFinger;
    private int mColor_Outer_NoFinger;
    private int mColor_FingerON;
    private int mColor_FingerUP;

    enum Status{
        No_FINGER,FINGER_UP,FINGER_ON;
    }

    private Status curStatus;

    private int mWidth,mHeight;

    private int mCenterX,mCenterY;

    private int mInnerRadius,mOuterRadius;

    private int mStrokeWidth;

    private Paint mPaint;

    private int mPadding;

    public ItemView(Context context,int Padding,int[] Colors) {
        super(context);

        curStatus=Status.No_FINGER;
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokeWidth=2;
        mPaint.setStrokeWidth(mStrokeWidth);

        mPadding=Padding;
        mColor_Outer_NoFinger=Colors[0];
        mColor_Inner_NoFinger=Colors[1];
        mColor_FingerON=Colors[2];
        mColor_FingerUP=Colors[3];
    }

    public ItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth=MeasureSpec.getSize(widthMeasureSpec);
        mHeight=MeasureSpec.getSize(heightMeasureSpec);
        mWidth=mWidth<mHeight?mWidth:mHeight;

        mOuterRadius=mCenterX=mCenterY=mWidth/2;
        mOuterRadius-=mStrokeWidth;

        mOuterRadius-=mPadding;

        mInnerRadius=mOuterRadius/3;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (curStatus){
            case No_FINGER:
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                mPaint.setColor(mColor_Outer_NoFinger);
                canvas.drawCircle(mCenterX,mCenterY,mOuterRadius,mPaint);

                mPaint.setColor(mColor_Inner_NoFinger);
                canvas.drawCircle(mCenterX,mCenterY,mInnerRadius,mPaint);
                break;
            case FINGER_ON:
                mPaint.setColor(mColor_FingerON);

                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(mCenterX,mCenterY,mOuterRadius,mPaint);

                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawCircle(mCenterX,mCenterY,mInnerRadius,mPaint);
                break;
            case FINGER_UP:
                mPaint.setColor(mColor_FingerUP);

                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(mCenterX,mCenterY,mOuterRadius,mPaint);

                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawCircle(mCenterX,mCenterY,mInnerRadius,mPaint);
                break;
        }
    }

    public void setCurStatus(Status curStatus) {
        this.curStatus = curStatus;
        invalidate();
    }
}
