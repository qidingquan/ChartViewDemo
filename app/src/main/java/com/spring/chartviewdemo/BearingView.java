package com.spring.chartviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2018/2/12.
 * 类似速度表
 */

public class BearingView extends View {

    private int[] colors = {
            Color.parseColor("#f44e72"),
            Color.parseColor("#fbaa1c"),
            Color.parseColor("#65d5ef"),
            Color.parseColor("#19d588"),
            Color.parseColor("#b438f4")
    };
    private String[] values = {"0-10", "10-20", "20-30", "30-40", "40-50"};
    private Paint arcPaint;//圆弧画笔
    private Paint textPaint;//文字画笔
    private int arcWidth = 80;//圆弧宽度
    private int arcMid = 30;//圆弧突出大小
    private float oneTextSize = 20;//文字
    private float twoTextSize = 10;//文字
    private int oneTextColor = Color.BLACK;
    private int twoTextColor = Color.GRAY;
    private int midAngle = 10;//间隔度数
    private int swipeAngle;//绘制的幅度
    private int startAngle = 180;
    private float oneStr = 20f;
    private String twoStr = "高于平均";
    private RectF rectF;
    private Rect textOneBounds;
    private Rect textTwoBounds;
    private Bitmap pointerBitmap;
    private CPoint center;

    public BearingView(Context context) {
        this(context, null);
    }

    public BearingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BearingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        oneTextSize = sp2px(oneTextSize);
        twoTextSize = sp2px(twoTextSize);

        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStrokeWidth(arcWidth);
        arcPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(50);
        swipeAngle = (360 - startAngle - (colors.length - 1) * midAngle) / colors.length;
        textOneBounds = new Rect();
        textTwoBounds = new Rect();
        pointerBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_pointer);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制圆弧和圆弧中间文字
        drawArcText(canvas);
        //绘制中间文字
        drawCenterText(canvas);
    }

    /**
     * 已100为最大值
     *
     * @param value 进度值
     */
    public void setData(float value) {
        this.oneStr = value;
        invalidate();
    }

    private void drawArcText(Canvas canvas) {
        if (center == null) {
            center = new CPoint(getWidth() / 2, getHeight() / 2);
        }
        int selectInex = 0;
        //绘制圆弧
        for (int i = 0; i < colors.length; i++) {
            int minValue = i * 10;
            int maxValue = i * 10 + 10;
            float curValue = oneStr / 100 * 50;

            arcPaint.setColor(colors[i]);
            if (curValue < maxValue && curValue >= minValue) {//当前为选中
                selectInex = i;
                rectF = new RectF(arcWidth, arcWidth, getWidth() - arcWidth, getHeight() - arcWidth);
            } else {
                rectF = new RectF(arcWidth + arcMid, arcWidth + arcMid, getWidth() - arcWidth - arcMid, getHeight() - arcWidth + arcMid);
            }
            canvas.drawArc(rectF, startAngle + i * (swipeAngle + midAngle), swipeAngle, false, arcPaint);
            Path path = new Path();
            path.addArc(rectF, startAngle + i * (swipeAngle + midAngle), swipeAngle);
            textPaint.getTextBounds(values[i], 0, values[i].length(), textOneBounds);
            canvas.drawTextOnPath(values[i], path, 0, (arcWidth - textOneBounds.height()) / 2, textPaint);
        }
        int rotate = (selectInex - colors.length / 2) * 36;
        //绘制箭头
        Matrix matrix = new Matrix();
        matrix.postTranslate(getWidth() / 2 - pointerBitmap.getWidth() / 2, arcWidth + arcMid + 10);
        matrix.postRotate(rotate, center.x, center.y);
        canvas.drawBitmap(pointerBitmap, matrix, arcPaint);
    }

    private void drawCenterText(Canvas canvas) {
        //绘制中间文字
        textPaint.setColor(oneTextColor);
        textPaint.setTextSize(oneTextSize);
        textPaint.getTextBounds(String.valueOf(oneStr), 0, String.valueOf(oneStr).length(), textOneBounds);
        canvas.drawText(String.valueOf(oneStr), (rectF.width() - textOneBounds.width()) / 2 + arcWidth, (getHeight() - textOneBounds.height()) / 2, textPaint);
        //绘制底部文字
        textPaint.setColor(twoTextColor);
        textPaint.setTextSize(twoTextSize);
        textPaint.getTextBounds(twoStr, 0, twoStr.length(), textTwoBounds);
        canvas.drawText(twoStr, (rectF.width() - textTwoBounds.width()) / 2 + arcWidth, (getHeight() - textOneBounds.height()) / 2 + textOneBounds.height() + 5, textPaint);
    }

    public void getPosition(CPoint center, float dist, float angle, CPoint outPoint) {
        outPoint.x = (float) (center.x + dist * Math.cos(Math.toRadians(angle)));
        outPoint.y = (float) (center.y + dist * Math.sin(Math.toRadians(angle)));
    }

    private float sp2px(float value) {
        float scaleDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (float) (value * scaleDensity + 0.5);
    }
}
