package com.spring.chartviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/22.
 * 心率图
 */

public class ECGView extends View {

    private Paint backPaint;
    private Paint linePaint;
    private int backLineColor = Color.parseColor("#2a3642");
    private int backColor = Color.parseColor("#2a2930");
    private int lineColor = Color.parseColor("#65d5ef");
    private List<CPoint> dataList;

    public ECGView(Context context) {
        this(context, null);
    }

    public ECGView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ECGView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStrokeWidth(5);
        backPaint.setColor(backLineColor);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5);
        linePaint.setColor(lineColor);

        dataList = new ArrayList<>();
        dataList.add(new CPoint(0, 0));
        dataList.add(new CPoint(8, 60));
        dataList.add(new CPoint(12, -50));
        dataList.add(new CPoint(14, 10));
        dataList.add(new CPoint(18, -30));
        dataList.add(new CPoint(20, 100));
        dataList.add(new CPoint(23, -50));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(backColor);
        //绘制背景线
        for (int i = 0; i < 5; i++) {
            canvas.drawLine(0, i * getHeight() / 4, getWidth(), i * getHeight() / 4, backPaint);
        }
        Path path = new Path();
        path.moveTo(0, getHeight() / 2);
        for (CPoint cPoint : dataList) {
            cPoint.y = (cPoint.y / 120f) * getHeight() / 2;//以最大120计算
            cPoint.x = (cPoint.x / 24f) * getWidth();//以24小时计算
            path.lineTo(cPoint.x, getHeight() / 2 - cPoint.y);
        }
        //绘制心率
        canvas.drawPath(path, linePaint);
    }

    private void setData(List<CPoint> dataList) {
        this.dataList = dataList;
        postInvalidate();
    }

}
