package com.zhou.ffmpegdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.zhou.ffmpegdemo.R;

/**
 * Created by zhou on 2018/4/30.
 */

public class ProgramView extends View {

    private Context context;
    private Paint mLeftRect;
    private Paint mRightRect;
    int width;
    int height;
    private Bitmap mCenterBm;
    int _left;
    int _top;
    private int leftRectWidth;
    private int rightRectWidth;
    float progress = 1.0f;
    private Paint mTextPaint;
    private Rect mTextRect;
    private String text;


    public ProgramView(Context context) {
        this(context, null);
    }

    public ProgramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initBm();
        initPaint();
        initDistance();
    }

    private void initDistance() {
       // _left = 10;
        _left = DisplayUtil.dip2px(context,10);
        _top = 0;

    }

    public void setBm(@DrawableRes int resouce) {
        mCenterBm = BitmapFactory.decodeResource(getResources(), resouce);
    }

    private void initBm() {
        mCenterBm = BitmapFactory.decodeResource(getResources(), R.drawable.flash);
    }

    private void initPaint() {
        mLeftRect = new Paint();
        mLeftRect.setStyle(Paint.Style.FILL_AND_STROKE);
        mLeftRect.setAntiAlias(true);
        mLeftRect.setColor(Color.GREEN);

        mRightRect = new Paint();
        mRightRect.setStyle(Paint.Style.FILL_AND_STROKE);
        mRightRect.setAntiAlias(true);
        mRightRect.setColor(Color.WHITE);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(DisplayUtil.sp2px(context,12));
        mTextRect = new Rect();
        mTextPaint.getTextBounds("12L", 0, 3, mTextRect);

    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int resultWidth = getSizeResult(widthSize, widthMode, true);
        int resultHeight = getSizeResult(heightSize, heightMode, false);
        setMeasuredDimension(resultWidth, resultHeight);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //mCenterBm.setHeight(h/2);
        leftRectWidth = (width - _left) / 2;
        rightRectWidth = (width - _left) / 2;

        int bmHeight = height -10;
        int bmWidth = height- 10;
        mCenterBm = Bitmap.createScaledBitmap(mCenterBm, bmWidth, bmHeight, true);
    }


    private int getSizeResult(int size, int widthMode, boolean isWidth) {
        if (widthMode == MeasureSpec.AT_MOST) {
            if (isWidth) {
               // width = 180;
                width = DisplayUtil.dip2px(context,180);
            } else {
                //height = 20;
                height = DisplayUtil.dip2px(context,20);
            }

        } else if (widthMode == MeasureSpec.EXACTLY) {
            if (isWidth) {
                //width = Math.max(size, 180);
                width = Math.max(size,DisplayUtil.dip2px(context,180));
            } else {
                //height = Math.max(size, 20);
                height = Math.max(size, DisplayUtil.dip2px(context,20));
            }
        }
        if (isWidth) {
            return width;
        } else {
            return height;
        }
    }

    public void setProgress(float progress) {
        if (progress > 1 || progress < 0) {
            return;
        }
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int l = (int) (width * progress);

        drawProgress(canvas, l);

        drawBm(canvas, l);

    }

    private void drawBm(Canvas canvas, int l) {
        int bmLeft = (l - mCenterBm.getWidth()) / 2 + _left;
        canvas.drawBitmap(mCenterBm, (l - mCenterBm.getWidth()) / 2 + _left, (height - mCenterBm.getHeight()) / 2, new Paint());

        //draw text
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        int textX = bmLeft + mCenterBm.getWidth();

        float descent = fontMetrics.descent;
        int textY = (int) ((height - mTextRect.bottom) / 2 + descent);
        canvas.drawText("12L", textX, textY, mTextPaint);
    }

    private void drawProgress(Canvas canvas, int l) {
        canvas.drawRect(new Rect(_left, _top, l, _top + height), mLeftRect);
        canvas.drawRect(new Rect(l, _top, width, _top + height), mRightRect);
    }
}
