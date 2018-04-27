package com.example.benja.squareword.GestureLock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.benja.squareword.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benja on 2018/4/23.
 */

public class SquareGroup extends RelativeLayout {

    private int mColor_Inner_NoFinger;
    private int mColor_Outer_NoFinger;
    private int mColor_FingerON;
    private int mColor_FingerUP;

    private Context mContext;

    private int mWidth,mHeight;

    private Paint mPaint;

    private ItemView[] mItemViews;

    private int mItemWidth;

    private int mItemViewGap;   //itemViewGap=itemView的Padding*2

    //边上的Item个数
    private int mSideItems;

    private int mColors[];

    private int[] mAnswer = { 1, 2, 3, 5, 9 };

    private List<Integer> mChoose = new ArrayList<Integer>();

    private Path mPath;

    private float mLastPathX,mLastPathY;

    private float mTargetX,mTargetY;

    private int mLastId;

    public SquareGroup(Context context) {
        super(context);
    }

    public SquareGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray=getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SquareGroup,0,0);
        mColor_Inner_NoFinger=typedArray.getColor(R.styleable.SquareGroup_mColorInnerNoFinger,0xFF000000);
        mColor_Outer_NoFinger=typedArray.getColor(R.styleable.SquareGroup_mColorOuterNoFinger,0xFF000000);
        mColor_FingerON=typedArray.getColor(R.styleable.SquareGroup_mColorFingerON,0xFF000000);
        mColor_FingerUP=typedArray.getColor(R.styleable.SquareGroup_mColorFingerUP,0xFF000000);

        mSideItems=typedArray.getInteger(R.styleable.SquareGroup_mSideItems,3);

        typedArray.recycle();

        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        mContext=context;

        mColors=new int[4];

        mColors[0]=mColor_Outer_NoFinger;
        mColors[1]=mColor_Inner_NoFinger;
        mColors[2]=mColor_FingerON;
        mColors[3]=mColor_FingerUP;

        mPath=new Path();

        Log.i("Test","initial");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth=MeasureSpec.getSize(widthMeasureSpec);
        mHeight=MeasureSpec.getSize(heightMeasureSpec);
        mHeight=mWidth=mWidth < mHeight ? mWidth : mHeight;

        if (mItemViews==null){
            mItemViews=new ItemView[mSideItems*mSideItems];

            for(int i=0;i<mItemViews.length;i++){
                mItemWidth=mWidth/mSideItems;

                mItemViewGap=mItemWidth/4;
                mPaint.setStrokeWidth((mItemWidth-mItemViewGap)/3);

                mItemViews[i]=new ItemView(mContext,mItemViewGap/2,mColors);
                mItemViews[i].setId(i+1);

                RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(mItemWidth,mItemWidth);

                if(i%mSideItems!=0){   //如果item在不第一列，就设置位置为前一个item的右边
                    layoutParams.addRule(RelativeLayout.RIGHT_OF,mItemViews[i-1].getId());
                }if(i>mSideItems-1){   // 从第二行开始，设置为上一行同一位置View的下面
                    layoutParams.addRule(RelativeLayout.BELOW,mItemViews[i-mSideItems].getId());
                }
                addViewInLayout(mItemViews[i],i,layoutParams);
                requestLayout();
            }
        }

        Log.i("Test","onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.i("Test","onLayout");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                mPaint.setColor(Color.BLACK);
                mPaint.setAlpha(50);
                ItemView child=getChildByPosition((int)event.getX(),(int)event.getY());
                if (child!=null){
                    int mId=child.getId();
                    if(!mChoose.contains(mId)){
                        mChoose.add(mId);
                        child.setCurStatus(ItemView.Status.FINGER_ON);
                        mLastPathX=(child.getLeft()+child.getRight())/2;
                        mLastPathY=(child.getTop()+child.getBottom())/2;
                        if(mChoose.size()==1){
                            mPath.moveTo(mLastPathX,mLastPathY);
                            mLastId=mId;
                        }else {
                            mPath.lineTo(mLastPathX,mLastPathY);
                            View lastChild=getChildAt(mLastId-1);
                            int mCenterX=(lastChild.getLeft()+lastChild.getRight())/2;
                            int mCenterY=(lastChild.getTop()+lastChild.getBottom())/2;
                            int distanceX=(int)Math.abs(mCenterX-mLastPathX);
                            int distanceY=(int)Math.abs(mCenterY-mLastPathY);
                            int distance;
                            if (distanceX==0){
                                distance=distanceY;
                            }else {
                                distance=distanceX;
                            }
                            Log.i("aTest",distanceX+"");
                            boolean crossMiddle=(distance==mItemWidth*2)||(distance*distance==2*(mItemWidth*2)*(mItemWidth*2));
                            if(!mChoose.contains((mLastId+mId)/2)&&crossMiddle){
                                mItemViews[(mLastId+mId)/2-1].setCurStatus(ItemView.Status.FINGER_ON);
                                mChoose.add((mLastId+mId)/2);
                            }
                            mLastId=mId;
                        }
                    }
                }
                mTargetX=event.getX();
                mTargetY=event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if(mChoose.size()!=0){
                    if(checkAnswer()){
                        Toast.makeText(getContext(),"UnLocked!",Toast.LENGTH_SHORT).show();
                    }else {
                        for(ItemView item:mItemViews){
                            item.setCurStatus(ItemView.Status.FINGER_UP);
                        }
                        Toast.makeText(getContext(),"TryAgain!",Toast.LENGTH_SHORT).show();
                    }
                    mPath.reset();
                    mChoose.clear();
                }
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.i("Test","onDraw");
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Log.i("Test","dispatchDraw");
        canvas.drawPath(mPath,mPaint);

        if (mChoose.size() > 0)
        {
            if (mLastPathX != 0 && mLastPathY != 0)
                canvas.drawLine(mLastPathX, mLastPathY, mTargetX,
                        mTargetY, mPaint);
        }
    }

    private void reset(){
        mChoose.clear();
        mPath.reset();
        for(ItemView item:mItemViews){
            item.setCurStatus(ItemView.Status.No_FINGER);
        }
    }

    private boolean isPositonInChild(ItemView item,int x,int y){
        int mCenterX=(item.getLeft()+item.getRight())/2;
        int mCenterY=(item.getTop()+item.getBottom())/2;
        int mRadius=(item.getWidth()-mItemViewGap)/2;
        if ((x-mCenterX)*(x-mCenterX)+(y-mCenterY)*(y-mCenterY)<mRadius*mRadius){
            return true;
        }
        return false;
    }

    private ItemView getChildByPosition(int x,int y){
        for(ItemView item:mItemViews){  //child的padding=mItemViewGap/2
            if(isPositonInChild(item,x,y)){
                return item;
            }
        }
        return null;
    }

    private boolean checkAnswer(){

        for(int i=0;i<mChoose.size();i++){
            if (mChoose.get(i)!=mAnswer[i]){
                return false;
            }
        }
        return true;
    }

    private int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
