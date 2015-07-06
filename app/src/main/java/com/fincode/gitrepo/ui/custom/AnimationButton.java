package com.fincode.gitrepo.ui.custom;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Region;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;

// Кастомная анимационная кнопка
public class AnimationButton extends Button {

	private float mDownX;
	private float mDownY;

	private float mRadius;

	private Paint mPaint;

	public AnimationButton(final Context context) {
		super(context);
		init();
	}

	public AnimationButton(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AnimationButton(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setAlpha(100);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(@NonNull final MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_UP) {
			mDownX = event.getX();
			mDownY = event.getY();

			ObjectAnimator animator = ObjectAnimator.ofFloat(this, "radius", 0,
					getWidth() * 3.0f);
			animator.setInterpolator(new AccelerateInterpolator());
			animator.setDuration(400);
			animator.start();
		}
		return super.onTouchEvent(event);
	}

	public void setRadius(final float radius) {
		mRadius = radius;
		if (mRadius > 0) {
			RadialGradient radialGradient = new RadialGradient(mDownX, mDownY,
					mRadius * 3, Color.TRANSPARENT, Color.BLACK,
					Shader.TileMode.MIRROR);
			mPaint.setShader(radialGradient);
		}
		invalidate();
	}

	private Path mPath = new Path();
	private Path mPath2 = new Path();

	@Override
	protected void onDraw(@NonNull final Canvas canvas) {
		super.onDraw(canvas);

		mPath2.reset();
		mPath2.addCircle(mDownX, mDownY, mRadius, Path.Direction.CW);

		canvas.clipPath(mPath2);

		mPath.reset();
		mPath.addCircle(mDownX, mDownY, mRadius / 3, Path.Direction.CW);

		canvas.clipPath(mPath, Region.Op.DIFFERENCE);

		canvas.drawCircle(mDownX, mDownY, mRadius, mPaint);
	}
}