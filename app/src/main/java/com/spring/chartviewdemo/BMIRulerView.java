package com.spring.chartviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * BMI
 */

public class BMIRulerView extends View {

    private int mWidth;
    private int mHeight;

    private float mSpaceWidth = 50;    //  尺子刻度2条线之间的距离
    private float mCircleHeight = 10;    //  mLineMidHeight  表示中间的高度(也就是 5  15 25 等时的高度)
    private float mLineHeight = 2;          //中间线的高度
    private int mLineMaxColor = Color.BLACK;//最大刻度颜色
    private int mLineMidColor = Color.GRAY; //中间刻度颜色
    private int mLineMinColor = Color.LTGRAY;//最小刻度颜色

    private int mLineColor = Color.LTGRAY;   //中间线的颜色
    private int mTextColor = Color.BLACK;    //文字的颜色

    private float mTextMarginTop = 10;    //o
    private float mTextSize = 30;         //尺子刻度下方数字 textsize
    private float mTextHeight;            //尺子刻度下方数字  的高度
    private Paint mTextPaint;             // 尺子刻度下方数字( 也就是每隔10个出现的数值) paint
    private Paint mLinePaint;             //  尺子刻度  paint
    private Bitmap bgBitmap;

    public BMIRulerView(Context context) {
        this(context, null);

    }

    public BMIRulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BMIRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {

        this.mTextHeight = myfloat(mTextHeight);
        this.mLineHeight = myfloat(mLineHeight);


        final TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RulerView);

        mLineMaxColor = typedArray.getColor(R.styleable.RulerView_lineMaxColor, mLineMaxColor);
        mLineMidColor = typedArray.getColor(R.styleable.RulerView_lineMidColor, mLineMidColor);
        mLineMinColor = typedArray.getColor(R.styleable.RulerView_lineMinColor, mLineMinColor);
        mLineColor = typedArray.getColor(R.styleable.RulerView_lineColor, mLineColor);
        mLineHeight = typedArray.getDimension(R.styleable.RulerView_lineHeight, mLineHeight);

        mTextSize = typedArray.getDimension(R.styleable.RulerView_textSize, mTextSize);
        mTextColor = typedArray.getColor(R.styleable.RulerView_textColor, mTextColor);
        mTextMarginTop = typedArray.getDimension(R.styleable.RulerView_textMarginTop, mTextMarginTop);


        typedArray.recycle();

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextHeight = getFontHeight(mTextPaint);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);
        bgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bg_notice);

    }


    public static int myfloat(float paramFloat) {
        return (int) (0.5F + paramFloat * 1.0f);
    }

    private float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mWidth = w;
            mHeight = h;
            mSpaceWidth = (mWidth -mCircleHeight / 2*12)/12;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float left, radius;
        String value;
        String title;
        //绘制底线
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mLineHeight);
        canvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, mLinePaint);

        for (int i = 1; i <= 12; i++) {
            left = i * mSpaceWidth;
            radius = mCircleHeight / 2;
            if (i == 2) {
                mLinePaint.setColor(Color.parseColor("#34464f"));
                mTextPaint.setColor(Color.parseColor("#34464f"));
                value = "14.9";
                canvas.drawText(value, left - mTextPaint.measureText(value) / 2,
                        mHeight / 2 + mTextMarginTop + mTextHeight, mTextPaint);
                title = "偏轻";
                canvas.drawText(title, left - mTextPaint.measureText(title) / 2,
                        mHeight / 2 - mTextMarginTop - 20, mTextPaint);
            } else if (i == 5) {
                mLinePaint.setColor(Color.parseColor("#65d5ef"));
                mTextPaint.setColor(Color.parseColor("#65d5ef"));
                //绘制下面文字
                value = "15.5";
                canvas.drawText(value, left - mTextPaint.measureText(value) / 2,
                        mHeight / 2 + mTextMarginTop + mTextHeight, mTextPaint);    // 在为整数时,画 数值
                //绘制背景图
                canvas.drawBitmap(bgBitmap, left - bgBitmap.getWidth() / 2, mHeight / 2 - bgBitmap.getHeight() - mTextMarginTop - 50, mLinePaint);
                //绘制上面文字
                mTextPaint.setColor(Color.parseColor("#ffffff"));
                title = "健康";
                canvas.drawText(title, left - mTextPaint.measureText(title) / 2,
                        mHeight / 2 - mTextMarginTop - 40 - bgBitmap.getHeight() / 2, mTextPaint);
            } else if (i == 8) {
                mLinePaint.setColor(Color.parseColor("#34464f"));
                mTextPaint.setColor(Color.parseColor("#34464f"));
                value = "17.3";
                canvas.drawText(value, left - mTextPaint.measureText(value) / 2,
                        mHeight / 2 + mTextMarginTop + mTextHeight, mTextPaint);    // 在为整数时,画 数值
                title = "超重";
                canvas.drawText(title, left - mTextPaint.measureText(title) / 2,
                        mHeight / 2 - mTextMarginTop - 20, mTextPaint);
            } else if (i == 11) {
                mLinePaint.setColor(Color.parseColor("#34464f"));
                mTextPaint.setColor(Color.parseColor("#34464f"));
                value = "18.3";
                canvas.drawText(value, left - mTextPaint.measureText(value) / 2,
                        mHeight / 2 + mTextMarginTop + mTextHeight, mTextPaint);    // 在为整数时,画 数值
                title = "肥胖";
                canvas.drawText(title, left - mTextPaint.measureText(title) / 2,
                        mHeight / 2 - mTextMarginTop - 20, mTextPaint);
            } else {
                mLinePaint.setColor(Color.parseColor("#a7a7a7"));
            }
            mLinePaint.setStrokeWidth(0);
            //绘制圆点
            canvas.drawCircle(left, mHeight / 2, radius, mLinePaint);

        }
    }

}
