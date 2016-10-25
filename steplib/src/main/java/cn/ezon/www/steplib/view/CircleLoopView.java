package cn.ezon.www.steplib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import cn.ezon.www.steplib.utils.DeviceUtils;


/**
 * 带动画 颜色渐变的 圆环视图组件
 * 
 * @author yxy
 * 
 * 
 */
public class CircleLoopView extends View {

	public CircleLoopView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public CircleLoopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CircleLoopView(Context context) {
		super(context);
		init();
	}

	private Paint mBgLoopPaint;
	private Paint mLoopPaint;
	private Paint mTextPaint;
	// 动画状态
	private static final int ANIM_STATE_INIT = 0;
	private static final int ANIM_STATE_START = 1;
	private static final int ANIM_STATE_ANIMING = 2;
	private static final int ANIM_STATE_READY_DONE = 3;

	private int anim_state = ANIM_STATE_INIT;
	// 绘制一圈需要的时间
	private static final long ANIM_FULL_TIME = 3000;
	private long animStartTime;
	// 当前进度需要的时间
	private long needTime;
	private float mProgress = 0;

	public void setProgress(float progress) {
		setProgress(progress, true);
	}

	public void setProgress(float progress, boolean hasAnim) {
		if (hasAnim) {
			this.mProgress = progress;
			needTime = Math.min((long) ((float) mProgress / 360f * ANIM_FULL_TIME), ANIM_FULL_TIME);
			startAnim();
		} else {
			this.mProgress = progress;
			anim_state = ANIM_STATE_READY_DONE;
			postInvalidate();
		}
	}

	// 最大圆环宽度
	private final int max_loopwidth = 100;
	// 圆环宽度
	private int loopWidth = 5;

	public void setLoopWidth(int width) {
		if (width <= 0)
			return;
		loopWidth = Math.min(max_loopwidth, width);
		mLoopPaint.setStrokeWidth(loopWidth);
		mBgLoopPaint.setStrokeWidth(loopWidth);
	}

	// 最大字体大小
	private final int max_textSize = 100;
	// 字体大小
	private int textSize = 18;

	/**
	 * 设置中心字体大小
	 * 
	 * @param size
	 */
	public void setCenterTextSize(int size) {
		if (size <= 0) {
			return;
		}
		textSize = Math.min(max_textSize, size);
		mTextPaint.setTextSize(textSize);
	}

	private int textColor = Color.WHITE;

	/**
	 * 设置中心字体颜色
	 * 
	 * @param color
	 */
	public void setCenterTextColort(int color) {
		if (color <= 0) {
			return;
		}
		textColor = color;
		mTextPaint.setColor(textColor);
	}

	private int circleWidth = -1;

	/**
	 * 设置组件宽度
	 * 
	 * @param width
	 */
	public void setCircleWidth(int width) {
		circleWidth = width;
	}

	private void startAnim() {
		anim_state = ANIM_STATE_START;
		animStartTime = System.currentTimeMillis();
		postInvalidateDelayed(2);
	}

	private void init() {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(DeviceUtils.dip2px(getContext(), 16));
		mTextPaint.setColor(textColor);

		mBgLoopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBgLoopPaint.setAntiAlias(true);
		mBgLoopPaint.setColor(Color.rgb(72, 133, 178));
		mBgLoopPaint.setStyle(Style.STROKE);
		mBgLoopPaint.setStrokeWidth(loopWidth);

		mLoopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	private boolean hasSetPaint = false;

	private void setPaint(RectF rect) {
		int[] mColors = new int[] {// 渐变色数组
		0xFFD5FE01, 0xFFE4FF00, 0xFFFF0406, 0xFFFF0406, 0xFFFF0406, 0xFFE4FF00, 0xFF56FF06, 0xFF56FF06, 0xFF56FF06, 0xFFD5FE01 };
		Shader s = new SweepGradient(rect.centerX(), rect.centerY(), mColors, null);
		mLoopPaint.setShader(s);
		mLoopPaint.setStyle(Style.STROKE);
		mLoopPaint.setStrokeWidth(loopWidth);
		mLoopPaint.setStrokeCap(Paint.Cap.ROUND);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		minWidth = Math.min(viewHeigth, viewWidth) - loopWidth - 2;
		RectF rect = new RectF((viewWidth - minWidth) / 2, (viewHeigth - minWidth) / 2, (viewWidth - minWidth) / 2 + minWidth, (viewHeigth - minWidth) / 2
				+ minWidth);
		if (!hasSetPaint) {
			setPaint(rect);
			hasSetPaint = true;
		}
		switch (anim_state) {
		case ANIM_STATE_INIT:
			drawCircleLoop(canvas, rect, 0);
			break;
		case ANIM_STATE_START:
			drawCircleLoop(canvas, rect, 0);
			anim_state = ANIM_STATE_ANIMING;
			postInvalidateDelayed(3);
			break;
		case ANIM_STATE_ANIMING:
			long time = System.currentTimeMillis() - animStartTime;
			if (time >= needTime) {
				anim_state = ANIM_STATE_READY_DONE;
				drawCircleLoop(canvas, rect, mProgress);
			} else {
				drawCircleLoop(canvas, rect, (((float) time / (float) needTime) * mProgress));
			}
			postInvalidateDelayed(3);
			break;
		case ANIM_STATE_READY_DONE:
			anim_state = ANIM_STATE_READY_DONE;
			drawCircleLoop(canvas, rect, mProgress);
			break;
		}
	}

	/**
	 * 绘制内外边环
	 * 
	 * @param canvas
	 * @param rect
	 */
	private void drawInnerAndOuterCircle(Canvas canvas, RectF rect) {
		Paint p = new Paint(mBgLoopPaint);
		p.setStrokeWidth(loopWidth);
		RectF innerRect = new RectF(rect);
		canvas.drawArc(innerRect, -90, 360, false, p);
	}

	/**
	 * 绘制圆环区
	 * 
	 * @param canvas
	 * @param rect
	 * @param progress
	 */
	private void drawCircleLoop(Canvas canvas, RectF rect, float progress) {
		// setLoopPaintColor(progress);
		drawInnerAndOuterCircle(canvas, rect);
		// canvas.drawOval(rect, mLoopPaint);
		canvas.drawArc(rect, -90, progress, false, mLoopPaint);
		canvas.drawText(getProgressText(progress), (viewWidth - getTextWidth(progress)) / 2, viewHeigth / 2 + getTextHeight() / 4, mTextPaint);
	}

	private String getProgressText(float progress) {
		return (int) (((float) progress / 360f) * 100f) + "%";
	}

	private float getTextWidth(float progress) {
		return mTextPaint.measureText(getProgressText(progress));
	}

	private float getTextHeight() {
		FontMetrics fm = mTextPaint.getFontMetrics();
		return fm.descent - fm.ascent;
	}

	private int minWidth;
	private int viewWidth;
	private int viewHeigth;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (circleWidth == -1) {
			viewHeigth = MeasureSpec.getSize(heightMeasureSpec);
			viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		} else {
			viewWidth = circleWidth;
			viewHeigth = circleWidth;
		}
		setMeasuredDimension(viewWidth, viewHeigth);
	}

}
