package io.github.xiewinson.movingparticlesview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.xiewinson.movingparticlesview.util.ScreenUtil;

public class MovingParticlesView extends View {

    private Paint bgPaint;
    private Paint linePaint;
    private Paint ballPaint;

    private int width, height;
    private float moveX, moveY;

    private List<Ball> balls = new ArrayList<>();

    private float minCircleRadius;
    private float maxCircleRadius;
    private float maxPointDistance;
    private float maxLineWidth;
    private float ballCount = 100;
    private float minSpeed;
    private float maxSpeed;

    public static final int DEFAULT_MIN_SPEED = 1;
    public static final int DEFAULT_MAX_SPEED = 3;
    public static final int DEFAULT_MIN_CIRCLE_RADIUS_DP = 2;
    public static final int DEFAULT_MAX_CIRCLE_RADIUS_DP = 5;
    private static final float DEFAULT_MOVE_DISTANCE_RATE = 0.2f;
    private static final float DEFAULT_MAX_POINT_DISTANCE_PERCENT = 0.1f;
    private static final int MAX_ALPHA = 130;
    private static final int MAX_LINE_WIDTH_DP = 2;

    public static final int DEFUALT_INVALID_INTERAL = 60;

    public MovingParticlesView(Context context) {
        this(context, null);
    }

    public MovingParticlesView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovingParticlesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setAntiAlias(true);

        ballPaint = new Paint(linePaint);
        ballPaint.setStyle(Paint.Style.FILL);

        bgPaint = new Paint(ballPaint);
        bgPaint.setColor(Color.parseColor("#00BCD4"));

        minCircleRadius = ScreenUtil.dp2px(context, DEFAULT_MIN_CIRCLE_RADIUS_DP);
        maxCircleRadius = ScreenUtil.dp2px(context, DEFAULT_MAX_CIRCLE_RADIUS_DP);
        maxLineWidth = ScreenUtil.dp2px(context, MAX_LINE_WIDTH_DP);

        minSpeed = ScreenUtil.dp2px(context, DEFAULT_MIN_SPEED);
        maxSpeed = ScreenUtil.dp2px(context, DEFAULT_MAX_SPEED);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPaint(bgPaint);
        drawBallsAndLines(canvas);
        postInvalidateDelayed(DEFUALT_INVALID_INTERAL);

    }

    private void drawBallsAndLines(Canvas canvas) {
        for (Ball ball : balls) {
            ball.move();
            ball.draw(canvas);

            if (moveX > 0 && moveY > 0) {
                float distance = ballAndMouse(ball);
                if (distance <= maxPointDistance) {
                    linePaint.setStrokeWidth(maxLineWidth - maxLineWidth * 0.6f * distance / maxPointDistance);
                    linePaint.setAlpha(MAX_ALPHA - (int) ((MAX_ALPHA * distance / maxPointDistance)));
                    canvas.drawLine(ball.x, ball.y, moveX, moveY, linePaint);
                }
            }
        }

        for (int i = 0; i < balls.size(); i++) {
            Ball ball0 = balls.get(i);
            for (int j = 0; j < balls.size(); j++) {
                Ball ball1 = balls.get(j);

                float distance = ballAndBall(ball0, ball1);
                if (distance <= maxPointDistance) {
                    linePaint.setStrokeWidth(maxLineWidth - maxLineWidth * 0.6f * distance / maxPointDistance);
                    linePaint.setAlpha(MAX_ALPHA - (int) ((MAX_ALPHA * distance / maxPointDistance)));
                    canvas.drawLine(ball0.x, ball0.y, ball1.x, ball1.y, linePaint);
                }
            }
        }

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        moveX = -1;
        moveY = -1;
        initBg();
    }


    private void initBg() {
        maxPointDistance = (width > height ? width : height) * DEFAULT_MAX_POINT_DISTANCE_PERCENT;
        balls.clear();
        for (int i = 0; i < ballCount; i++) {
            balls.add(new Ball());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        moveX = event.getX();
        moveY = event.getY();
        int action = event.getAction();
//        if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL && balls.size() > 0) {
            Ball ball = balls.get(new Random().nextInt(balls.size()));
//            Ball ball = new Ball();
            ball.x = moveX;
            ball.y = moveY;
            moveX = -1;
            moveY = -1;
//        }

        return true;
    }


    private float ballAndMouse(Ball ball) {
        float disX = Math.abs(moveX - ball.x);
        float disY = Math.abs(moveY - ball.y);
        return (float) Math.sqrt(disX * disX + disY * disY);
    }

    private float ballAndBall(Ball ball0, Ball ball1) {
        float disX = Math.abs(ball0.x - ball1.x);
        float disY = Math.abs(ball0.y - ball1.y);
        return (float) Math.sqrt(disX * disX + disY * disY);
    }

    private class Ball {
        private float r;
        private float x, y, speedX, speedY;

        private float randomNum(float x, float y) {
            return (float) Math.floor(Math.random() * (y - x + 1) + x);
        }

        public Ball() {
            this.r = randomNum(minCircleRadius, maxCircleRadius);
            this.x = randomNum(this.r, width - this.r);
            this.y = randomNum(this.r, height - this.r);

            this.speedX = randomNum(minSpeed, maxSpeed) * (randomNum(0, 1) == 1 ? 1 : -1);
            this.speedY = randomNum(minSpeed, maxSpeed) * (randomNum(0, 1) == 1 ? 1 : -1);
        }

        public void move() {
            this.x += this.speedX * DEFAULT_MOVE_DISTANCE_RATE;
            this.y += this.speedY * DEFAULT_MOVE_DISTANCE_RATE;

            if (this.x <= this.r) {
                this.x = this.r;
                this.speedX *= -1;
            }
            if (this.x >= width - this.r) {
                this.x = width - this.r;
                this.speedX *= -1;
            }

            if (this.y <= this.r) {
                this.y = this.r;
                this.speedY *= -1;
            }

            if (this.y >= height - this.r) {
                this.y = height - this.r;
                this.speedY *= -1;
            }
        }

        public void draw(Canvas canvas) {
            canvas.drawCircle(this.x, this.y, this.r, ballPaint);
        }
    }


}
