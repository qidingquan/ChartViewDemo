package com.spring.chartviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2018/2/13.
 * 雷达蜘蛛图
 */

public class RadarView extends View {

    private int borderTextColor = Color.GREEN;
    private int borderTextSize = 15;
    private int midTextColor = Color.GREEN;
    private int midOneTextSize = 30;
    private int midTwoTextSize = 15;
    private int circleColor = Color.GREEN;
    private int circleWidth = 2;
    private int overColor = Color.GREEN;

    private Paint textPaint;
    private Paint circlePaint;
    private Paint overPaint;
    private Rect maxRect;
    private Rect minRect;

    private int[] values = new int[]{20, 30, 80, 60, 40};
    private String[] outTexts = {"心率", "耐力", "步数", "锻炼", "BMI"};
    private String centerOneText = "88";
    private String centerTwoText = "健康指数";
    private int circleNum = 5;//刻度数 几个圆
    private int midRadius;//圆与圆间的距离
    private int lineAngle;//线与线之间的角度
    private int textCircleSpace = 30;
    private CPoint outPoint;
    private CPoint centerPoint;
    private Path dataPath;

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        borderTextSize = sp2px(borderTextSize);
        midOneTextSize = sp2px(midOneTextSize);
        midTwoTextSize = sp2px(midTwoTextSize);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarView);

        borderTextColor = typedArray.getColor(R.styleable.RadarView_borderTextColor, borderTextColor);
        borderTextSize = (int) typedArray.getDimension(R.styleable.RadarView_borderTextSize, borderTextSize);
        midTextColor = typedArray.getColor(R.styleable.RadarView_midTextColor, midTextColor);
        midOneTextSize = (int) typedArray.getDimension(R.styleable.RadarView_midOneTextSize, midOneTextSize);
        midTwoTextSize = (int) typedArray.getDimension(R.styleable.RadarView_midTwoTextSize, midTwoTextSize);
        circleColor = typedArray.getColor(R.styleable.RadarView_circleColor, circleColor);
        circleWidth = (int) typedArray.getDimension(R.styleable.RadarView_circleWidth, circleWidth);
        overColor = typedArray.getColor(R.styleable.RadarView_overColor, overColor);
        circleNum = typedArray.getInteger(R.styleable.RadarView_circleNum, circleNum);
        textCircleSpace = (int) typedArray.getDimension(R.styleable.RadarView_textCircleSpace, textCircleSpace);

        typedArray.recycle();

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(borderTextSize);
        textPaint.setColor(borderTextColor);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(circleColor);
        circlePaint.setStrokeWidth(circleWidth);
        circlePaint.setStyle(Paint.Style.STROKE);

        overPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        overPaint.setColor(overColor);

        lineAngle = 360 / outTexts.length;
        maxRect = new Rect();
        minRect = new Rect();
        outPoint = new CPoint();
        dataPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (centerPoint == null) {
            centerPoint = new CPoint(getWidth() / 2, getHeight() / 2);
        }
        //计算圆与圆之间的距离
        textPaint.setTextSize(borderTextSize);
        textPaint.setColor(borderTextColor);
        textPaint.getTextBounds("健健康康", 0, 2, minRect);
        midRadius = (getWidth() - (minRect.width() + textCircleSpace) * 2 - circleWidth * circleNum * 2) / circleNum / 2;
        //绘制背景圆环和线边框文字
        drawBackGround(canvas);
        //绘制数据
        drawData(values);
        drawFilledPath(canvas, dataPath, Color.RED, 80);
        //绘制中间文字
        drawCenterText(canvas);
    }

    public void setData(int[] values, String centerOneText) {
        this.values = values;
        this.centerOneText = centerOneText;
        invalidate();
    }

    private void drawBackGround(Canvas canvas) {
        //1.绘制底部圆环
        for (int i = 1; i <= circleNum; i++) {
            circlePaint.setStrokeWidth(circleWidth);
            circlePaint.setAlpha(100);
            canvas.drawCircle(centerPoint.x, centerPoint.y, midRadius * i, circlePaint);
        }

        for (int i = 0; i < outTexts.length; i++) {
            //2.绘制圆心与外圆之间的连线 从最上面一点开始绘制
            getPosition(centerPoint, midRadius * circleNum, lineAngle * i - 90, outPoint);
            float[] linePoints = {centerPoint.x, centerPoint.y, outPoint.x, outPoint.y};
            circlePaint.setStrokeWidth(circleWidth);
            circlePaint.setAlpha(100);
            canvas.drawLines(linePoints, circlePaint);
            //3.绘制外圆点
            circlePaint.setStrokeWidth(8);
            circlePaint.setAlpha(50);
            canvas.drawCircle(outPoint.x, outPoint.y, 20, circlePaint);
            circlePaint.setStrokeWidth(10);
            circlePaint.setAlpha(100);
            canvas.drawCircle(outPoint.x, outPoint.y, 10, circlePaint);
            //4.绘制外圆值
            textPaint.setTextSize(borderTextSize);
            textPaint.setColor(borderTextColor);
            textPaint.getTextBounds("健健康康", 0, 2, minRect);
            switch (i) {
                case 0://最上面一点
                    canvas.drawText(outTexts[i], outPoint.x - minRect.width() / 2, outPoint.y - textCircleSpace, textPaint);
                    break;
                case 1:
                    canvas.drawText(outTexts[i], outPoint.x + textCircleSpace, outPoint.y + minRect.height() / 2, textPaint);
                    break;
                case 2:
                    canvas.drawText(outTexts[i], outPoint.x + textCircleSpace, outPoint.y + minRect.height() / 2, textPaint);
                    break;
                case 3:
                    canvas.drawText(outTexts[i], outPoint.x - minRect.width() - textCircleSpace, outPoint.y + minRect.height(), textPaint);
                    break;
                case 4:
                    canvas.drawText(outTexts[i], outPoint.x - minRect.width() - textCircleSpace, outPoint.y + minRect.height() / 2, textPaint);
                    break;
            }
        }
    }

    /**
     * 绘制数据值
     *
     * @param values 最大值已100计算
     */
    private void drawData(int[] values) {
        float maxRadius = midRadius * circleNum;
        //从上面一点开始绘制
        for (int i = 0; i < values.length; i++) {
            getPosition(centerPoint, (values[i] / 100f) * maxRadius, lineAngle * i - 90, outPoint);
            //获取值坐标点
            if (i == 0) {
                dataPath.moveTo(outPoint.x, outPoint.y);
            } else {
                dataPath.lineTo(outPoint.x, outPoint.y);
            }
        }

    }

    private void drawCenterText(Canvas canvas) {
        //中间上面文字
        textPaint.setColor(midTextColor);
        textPaint.setTextSize(midOneTextSize);
        textPaint.getTextBounds(centerOneText, 0, centerOneText.length(), maxRect);

        int startY = (int) (centerPoint.y);
        canvas.drawText(centerOneText, centerPoint.x - maxRect.width() / 2, startY, textPaint);
        //中间下面文字
        textPaint.setTextSize(midTwoTextSize);
        textPaint.getTextBounds(centerTwoText, 0, centerTwoText.length(), minRect);
        canvas.drawText(centerTwoText, centerPoint.x - minRect.width() / 2, startY + maxRect.height() + 10, textPaint);
    }

    /**
     * 获取线上的位置
     *
     * @param center   中间位置
     * @param dist     进度值
     * @param angle    角度
     * @param outPoint 返回点
     */
    public void getPosition(CPoint center, float dist, float angle, CPoint outPoint) {
        outPoint.x = (float) (center.x + dist * Math.cos(Math.toRadians(angle)));
        outPoint.y = (float) (center.y + dist * Math.sin(Math.toRadians(angle)));
    }

    private void drawFilledPath(Canvas c, Path filledPath, int fillColor, int fillAlpha) {

        int color = (fillAlpha << 24) | (fillColor & 0xffffff);

        if (android.os.Build.VERSION.SDK_INT >= 18) {
            int save = c.save();
            c.clipPath(filledPath);
            c.drawColor(color);
            c.restoreToCount(save);
        } else {
            // save
            Paint.Style previous = overPaint.getStyle();
            int previousColor = overPaint.getColor();
            // set
            overPaint.setStyle(Paint.Style.FILL);
            overPaint.setColor(color);
            c.drawPath(filledPath, overPaint);
            // restore
            overPaint.setColor(previousColor);
            overPaint.setStyle(previous);
        }
    }

    private int sp2px(int spValue) {
        float scale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5);
    }
}
