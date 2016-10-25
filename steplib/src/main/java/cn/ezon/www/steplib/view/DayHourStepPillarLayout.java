package cn.ezon.www.steplib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.ezon.www.steplib.utils.DeviceUtils;
import cn.ezon.www.steplib.utils.TextUtils;
import cn.ezon.www.steplib.utils.UserUtils;

/**
 * 可拖动 以小时绘制全天步数的柱状图
 * <p/>
 * description：
 */
public class DayHourStepPillarLayout extends View {

    public DayHourStepPillarLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public DayHourStepPillarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DayHourStepPillarLayout(Context context) {
        super(context);
        init(context);
    }

    private String hourStr;
    private Context context;
    private int width;

    private void init(Context context) {
        this.context = context;
        hourStr = "小时";
        width = context.getResources().getDisplayMetrics().widthPixels - DeviceUtils.dip2px(getContext(), 40);
        ITEM_WIDTH = ((width - leftOffset) / DAY_HOUR) / 2;
        mChartPaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(textColor);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setTextSize(DeviceUtils.dip2px(context, 10));
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(lineColor);
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setTextSize(DeviceUtils.dip2px(context, 10));
        initBottomHeight();
    }

    private void initBottomHeight() {
        dp10 = DeviceUtils.dip2px(context, 10);
        BOTTOMHEIGHT = dp10 * 2;
    }

    private Paint mChartPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private Paint mLinePaint = new Paint();

    private List<RectFHolder> holderList = new ArrayList<RectFHolder>();
    // 动画总长时间
    private final float ANIM_TIME = 500;
    // 绘制全高度时间 ，剩下 300 ms 为回弹动画
    private final float FULL_HEIGHT_ANIM_TIME = 350;

    // 步数组件 竖线绘制起点坐标
    private float stepVLineX = 0;

    // 一个柱条的宽度
    private static float ITEM_WIDTH = 20;
    // 一天的时间
    private static final int DAY_HOUR = 24;
    // 组件高度
    private int viewHeigth;
    // 底部刻度高度
    private int BOTTOMHEIGHT = 50;
    // 组件高度
    private int viewWidth;
    // 最大步数
    private int maxStep = 0;
    //步进值
    private int graded = 5;

    private boolean isFirstDrawed = false;

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        isFirstDrawed = true;
        if (holderList.size() == 0) {

        } else {
            drawBg(canvas);
            boolean isRefresh = false;
            for (int i = 0; i < holderList.size(); i++) {
                RectFHolder holder = holderList.get(i);
                setPaint(holder.step);
                boolean draw = holder.draw(canvas);
                if (!isRefresh && draw) {
                    isRefresh = true;
                }
            }
            if (isRefresh) {
                postInvalidateDelayed(5);
            }
        }
    }

    private int leftOffset = 50;

    private int pillarColor1 = Color.rgb(0xC5, 0x24, 0x24);
    private int pillarColor2 = Color.rgb(0xD9, 0xD9, 0xD9);

    private void drawBg(Canvas canvas) {
        boolean isUseK = false;
        if (maxStep > 1000) {
            isUseK = true;
        }
        int index = 0;
        int textHeight = TextUtils.getFontHeight(mTextPaint) / 3;
        for (int i = 0; i < maxStep; ) {
            i = index * graded;
            float heightScanle = 1.0f - ((float) i / (float) maxStep);
            float top = topOffset + (getPillarAreaBottomCoord() - topOffset) * heightScanle;
            canvas.drawLine(leftOffset, top, ITEM_WIDTH * 2 * 24 + leftOffset, top, mLinePaint);
            String drawStr = String.valueOf(i);
            if (isUseK) {
                drawStr = (i % 1000f == 0 ? UserUtils.formatKeepZeroNumber(i / 1000f) : UserUtils.formatKeepOneNumber(i / 1000f)) + "K";
            }
            canvas.drawText(drawStr, leftOffset - mTextPaint.measureText(drawStr) - 4, top + textHeight, mTextPaint);
            index++;
        }

        for (int i = 0; i < 7; i++) {
            int x = (int) ((i * 4) * (ITEM_WIDTH * 2) + leftOffset);
            canvas.drawLine(x, topOffset, x, getPillarAreaBottomCoord(), mLinePaint);
        }

        int offset = 7;
        canvas.drawText("0", leftOffset - mTextPaint.measureText("0") / 2, getPillarAreaBottomCoord() + BOTTOMHEIGHT - offset, mTextPaint);
        canvas.drawText("12", (viewWidth - leftOffset) / 2 + leftOffset - (mTextPaint.measureText("12") + ITEM_WIDTH) / 2, getPillarAreaBottomCoord() + BOTTOMHEIGHT - offset, mTextPaint);
        canvas.drawText("24H", viewWidth - mTextPaint.measureText("24H") - ITEM_WIDTH, getPillarAreaBottomCoord() + BOTTOMHEIGHT - offset, mTextPaint);
    }

    /**
     * 设置画笔颜色
     *
     * @param step
     */
    private void setPaint(int step) {
        // 设置颜色
        if (step >= activeAvgStep) {
            mChartPaint.setColor(pillarColor1);
        } else {
            mChartPaint.setColor(pillarColor2);
        }
    }

    // 顶部偏移量
    private float topOffset = 20;
    private int lineColor = Color.rgb(0xe8, 0xe8, 0xe8);
    private int textColor = Color.rgb(0xc2, 0xc2, 0xc2);
    private int dp10;

    /**
     * 柱状区最底部的坐标
     *
     * @return
     */
    private float getPillarAreaBottomCoord() {
        return viewHeigth - BOTTOMHEIGHT;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeigth = MeasureSpec.getSize(heightMeasureSpec);
        width = context.getResources().getDisplayMetrics().widthPixels - DeviceUtils.dip2px(getContext(), 40);
        viewWidth = width;
        if (viewHeigth == 0) {
            viewHeigth = DeviceUtils.dip2px(context, 100);
        }
        setMeasuredDimension(viewWidth, viewHeigth);
    }

    public void reDraw() {
        width = context.getResources().getDisplayMetrics().widthPixels - DeviceUtils.dip2px(getContext(), 40);
        ITEM_WIDTH = ((width - leftOffset) / DAY_HOUR) / 2;
        viewWidth = width;
        setMeasuredDimension(viewWidth, viewHeigth);
    }

    private int activeAvgStep = 1;

    /**
     * 设置数据
     *
     * @param list
     */
    public void setStepData(List<Integer> list) {
        holderList.clear();
        int emptyStepStartIndex = 0;
        if (list != null) {
            emptyStepStartIndex = list.size();
            int sumStep = 0;
            int haveStepHourNumber = 0;
            for (int i = 0; i < list.size(); i++) {
                int step = list.get(i);
                if (step > 0) {
                    haveStepHourNumber++;
                }
                sumStep += step;
                maxStep = Math.max(maxStep, step);
                holderList.add(createStepHolder(i, list.get(i)));
            }
            if (haveStepHourNumber > 1 && sumStep > 0) {
                activeAvgStep = sumStep / haveStepHourNumber;
            }
        }

        if (maxStep > 20000) {
            graded = 5000;
        } else if (maxStep > 10000) {
            graded = 3000;
        } else if (maxStep > 5000) {
            graded = 2000;
        } else if (maxStep > 2500) {
            graded = 1000;
        } else if (maxStep > 2000) {
            graded = 500;
        } else if (maxStep > 1000) {
            graded = 400;
        } else if (maxStep > 500) {
            graded = 200;
        } else if (maxStep > 250) {
            graded = 100;
        } else if (maxStep > 100) {
            graded = 50;
        } else if (maxStep > 50) {
            graded = 25;
        } else if (maxStep > 10) {
            graded = 5;
        } else {
            graded = 2;
            maxStep = 10;
        }
        int index = 0;
        while (index * graded < maxStep) {
            index++;
        }
        maxStep = index * graded;
        if (emptyStepStartIndex < DAY_HOUR) {
            for (int i = emptyStepStartIndex; i < DAY_HOUR; i++) {
                holderList.add(createStepHolder(i, 0));
            }
        }
        if (isFirstDrawed) {
            postInvalidateDelayed(300);
        }
    }

    /**
     * 创建一个0步数的 柱状holder
     *
     * @param i
     * @return
     */
    private RectFHolder createStepHolder(int i, int step) {
        RectFHolder holder = new RectFHolder();
        RectF r = new RectF();
        holder.rect = r;
        holder.step = step;
        r.left = i * (ITEM_WIDTH * 2) + leftOffset;
        r.right = (i + 1) * (ITEM_WIDTH * 2) + leftOffset - ITEM_WIDTH;
        r.top = getPillarAreaBottomCoord();
        r.bottom = getPillarAreaBottomCoord();
        return holder;
    }

    private class RectFHolder {
        RectF rect;
        int step;
        // 动画是否完成
        boolean isAnimCompleted = false;
        // 是否正在进行动画
        boolean isAniming = false;
        // 是否已经进行过动画
        boolean isAnimed = false;
        boolean isSetTop = false;
        // 动画开始时间
        long animStartTime;

        final float shotScale = 0.1f;

        private void startAnim() {
            isAnimCompleted = false;
            isAniming = true;
            animStartTime = System.currentTimeMillis();
        }

        private void stopAnim() {
            isAnimCompleted = true;
            isAnimed = true;
            isAniming = false;
        }

        /**
         * 计算top坐标
         */
        private void countTopCoord() {
            // 计算top位置
            if (!isSetTop) {
                float heightScanle = 1.0f - ((float) step / (float) maxStep);
                rect.top = topOffset + (getPillarAreaBottomCoord() - topOffset) * heightScanle;
                rect.bottom = getPillarAreaBottomCoord();
            }
        }

        /**
         * 执行绘制
         *
         * @param canvas
         */
        private boolean draw(Canvas canvas) {
            if (step < 0) {
                return false;
            }
            countTopCoord();
            RectF drawRect = new RectF(rect);

            // 正在执行动画 且动画尚未完成
            if (isAniming && !isAnimCompleted) {
                long time = System.currentTimeMillis() - animStartTime;
                // 如果时间已过
                if (time > ANIM_TIME) {
                    stopAnim();
                    // 如果时间 已经超过全高度时间
                } else if (time >= FULL_HEIGHT_ANIM_TIME) {
                    float h = (((float) time - FULL_HEIGHT_ANIM_TIME) / (ANIM_TIME - FULL_HEIGHT_ANIM_TIME)) * rect.height() * shotScale;
                    drawRect.top = rect.bottom - rect.height() * (1.0f + shotScale) + h;
                } else {
                    // 绘制原高度1.1倍的高度
                    drawRect.top = rect.bottom - ((float) time / FULL_HEIGHT_ANIM_TIME) * rect.height() * (1.0f + shotScale);
                }
                canvas.drawRect(drawRect, mChartPaint);
                return true;
                // 动画还没有执行 准备进入界面
            } else if (!isAnimed) {
                startAnim();
                drawRect.top = drawRect.bottom;
                canvas.drawRect(drawRect, mChartPaint);
                return true;
            }
            canvas.drawRect(drawRect, mChartPaint);
            return false;
        }
    }

}
