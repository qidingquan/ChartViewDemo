package com.spring.chartviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/22.
 * 心率图
 */

public class StepChartView extends View {

    private Paint backPaint;
    private Paint linePaint;
    private Paint textPaint;
    private Paint circlePaint;
    private int circleRadius = 10;
    private int circleColor = Color.parseColor("#44aef6");
    private int maxColor = Color.parseColor("#545454");
    //背景线颜色高度
    private int backLineColor = Color.parseColor("#d2d2d2");
    private int backLineHeight = 1;
    private int backLineNum = 5;
    //画布背景颜色
    private int backColor = Color.parseColor("#ffffff");
    //曲线颜色 高度
    private int lineColor = Color.parseColor("#0072ff");
    private int lineHeight = 2;
    //单位文字颜色 大小
    private int unitColor = Color.parseColor("#b4b4b4");
    private int unitTextSize = 12;
    //时间文字颜色大小 和 与线的间距
    private int dateColor = Color.parseColor("#252525");
    private int dateTextSize = 15;
    private int dateSpaceHeight = 10;
    private int backLineMid = 50;//背景线的间距
    private int bottomLineY;//最后一条线的位置

    private Rect textRect;
    private String unitStr = "单位：步";
    private String[] dates = {"2/20", "2/21", "2/22", "2/24", "今天"};
    private List<CPoint> dataList;
    private Path path;

    public StepChartView(Context context) {
        this(context, null);
    }

    public StepChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        unitTextSize = sp2px(unitTextSize);
        dateTextSize = sp2px(dateTextSize);
        backLineHeight = dp2px(backLineHeight);
        lineHeight = dp2px(lineHeight);
        dateSpaceHeight = dp2px(dateSpaceHeight);
        backLineMid = dp2px(backLineMid);
        circleRadius = dp2px(circleRadius);

        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStrokeWidth(backLineHeight);
        backPaint.setColor(backLineColor);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(lineHeight);
        linePaint.setColor(lineColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(unitColor);
        textPaint.setTextSize(unitTextSize);
        textRect = new Rect();
        path = new Path();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(circleColor);

        dataList = new ArrayList<>();
        dataList.add(new CPoint(0, 0));
        dataList.add(new CPoint(1, 80));
        dataList.add(new CPoint(2, 10));
        dataList.add(new CPoint(3, 20));
        dataList.add(new CPoint(4, 60));
        dataList.add(new CPoint(5, 90));
        dataList.add(new CPoint(6, 0));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(backColor);
        //计算背景线之间的垂直间距
        textPaint.setTextSize(dateTextSize);
        textPaint.getTextBounds(dates[dates.length - 1], 0, dates[dates.length - 1].length(), textRect);
        backLineMid = (getHeight() - backLineHeight * backLineNum - textRect.height() - dateSpaceHeight) / (backLineNum - 1);
        bottomLineY = 4 * backLineMid;
        //绘制背景线
        for (int i = 0; i < 5; i++) {
            canvas.drawLine(0, i * backLineMid + (i == 0 ? backLineHeight : 0), getWidth(),
                    i * backLineMid + (i == 0 ? backLineHeight : 0), backPaint);
            if (i == 1) {
                //绘制单位
                textPaint.setColor(unitColor);
                textPaint.setTextSize(unitTextSize);
                textPaint.getTextBounds(unitStr, 0, unitStr.length(), textRect);
                canvas.drawText(unitStr, 0, i * backLineHeight + i * backLineMid / 2, textPaint);
            }
            //绘制日期
            textPaint.setTextSize(dateTextSize);
            textPaint.setColor(dateColor);
            textPaint.getTextBounds(dates[i], 0, dates[i].length(), textRect);

            canvas.drawText(dates[i], i * (getWidth() / dates.length),
                    getHeight(), textPaint);
        }
        float maxY = bottomLineY;
        float maxX = 0;
        float lastValue = 0;
        float value = 0;
        for (CPoint cPoint : dataList) {
            lastValue = cPoint.y;
            cPoint.y = bottomLineY - (cPoint.y / 100f) * bottomLineY;//以最大120计算
            cPoint.x = (cPoint.x / dataList.size()) * getWidth();//以24小时计算
            if (cPoint.y < maxY) {
                value = lastValue;
                maxY = cPoint.y;
                maxX = cPoint.x;
            }
        }
        //绘制心率
        drawScrollLine(canvas);
        //最高点绘制圆和文字
        circlePaint.setAlpha(100);
        canvas.drawCircle(maxX, maxY, circleRadius, circlePaint);
        circlePaint.setAlpha(255);
        canvas.drawCircle(maxX, maxY, circleRadius / 2, circlePaint);
        String text = "8888";
        textPaint.setColor(maxColor);
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        float left = maxX + circleRadius + textRect.width() > getWidth() ? getWidth() - textRect.width() : maxX + circleRadius;
        float top = maxY - circleRadius - textRect.height() > 0 ? maxY - circleRadius - textRect.height() : textRect.height();
        canvas.drawText(value+"", left, top, textPaint);
    }

    private void drawScrollLine(Canvas canvas) {
        Path path = new Path();
        Path closePath = new Path();
        closePath.moveTo(0, bottomLineY);
        CPoint startp = new CPoint();
        CPoint endp = new CPoint();
        linePaint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < dataList.size() - 1; i++) {
            startp = dataList.get(i);
            endp = dataList.get(i + 1);
            int wt = (int) ((startp.x + endp.x) / 2);
            Point p3 = new Point();
            Point p4 = new Point();
            p3.y = (int) startp.y;
            p3.x = wt;
            p4.y = (int) endp.y;
            p4.x = wt;

            path.moveTo(startp.x, startp.y);
            path.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
            closePath.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
            LinearGradient gradient = new LinearGradient(0, 0, getWidth(), 0,
                    new int[]{Color.parseColor("#137dff"), Color.parseColor("#78e5cc")}, null, Shader.TileMode.CLAMP);
            linePaint.setShader(gradient);
            canvas.drawPath(path, linePaint);
        }
        closePath.moveTo(0, bottomLineY);
        linePaint.setStyle(Paint.Style.FILL);
        LinearGradient gradient = new LinearGradient(0, 0, 0, bottomLineY,
                new int[]{Color.parseColor("#ff0072ff"), Color.parseColor("#223edabb")}, null, Shader.TileMode.CLAMP);
        linePaint.setShader(gradient);
        canvas.drawPath(closePath, linePaint);

    }

    private void setData(List<CPoint> dataList) {
        this.dataList = dataList;
        postInvalidate();
    }

    private int sp2px(int spValue) {
        float scale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5);
    }

    private int dp2px(int dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5);
    }
}
