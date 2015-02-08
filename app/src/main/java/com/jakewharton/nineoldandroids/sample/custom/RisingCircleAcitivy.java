package com.jakewharton.nineoldandroids.sample.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jakewharton.nineoldandroids.sample.R;
import com.jakewharton.nineoldandroids.sample.apidemos.ShapeHolder;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;

public class RisingCircleAcitivy extends Activity {

    LinearLayout container;
    LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rising_circle_acitivy);
        root = (LinearLayout) findViewById(R.id.root);
        container = (LinearLayout) findViewById(R.id.container);
        final MyAnimationView animView = new MyAnimationView(this);
        container.addView(animView);

        Button starter = (Button) findViewById(R.id.startButton);
        starter.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                animView.startAnimation();
            }
        });
    }

    public class MyAnimationView extends View implements ValueAnimator.AnimatorUpdateListener {

        public final ArrayList<ShapeHolder> balls = new ArrayList<ShapeHolder>();
        AnimatorSet animation = null;
        int DURATION = 500;
        private float mDensity;
        float BALL_SIZE;

        public MyAnimationView(Context context) {
            super(context);

            mDensity = getContext().getResources().getDisplayMetrics().density;
            BALL_SIZE = 50f * mDensity;

            View parent = (View) getParent();
            ShapeHolder ball0 = addBall(300f, 25f);
//            ShapeHolder ball1 = addBall(150f, 25f);
//            ShapeHolder ball2 = addBall(250f, 25f);
//            ShapeHolder ball3 = addBall(350f, 25f);
        }

        private void createAnimation() {
            if (animation == null) {
                View parent = (View) getParent().getParent();
                ObjectAnimator anim1 = ObjectAnimator.ofFloat(balls.get(0), "y",
                        parent.getHeight() - balls.get(0).getHeight(), 0f).setDuration(DURATION);
//                anim1.addUpdateListener(this);

                ShapeHolder ball = balls.get(0);
                float SCALED_WIDTH = ball.getWidth() * 4;
                float SCALED_HEIGHT = ball.getHeight() * 4;
                PropertyValuesHolder pvhW = PropertyValuesHolder.ofFloat("width", ball.getWidth(),
                        SCALED_WIDTH);
                PropertyValuesHolder pvhH = PropertyValuesHolder.ofFloat("height", ball.getHeight(),
                        SCALED_HEIGHT);
                PropertyValuesHolder pvTX = PropertyValuesHolder.ofFloat("x", ball.getX(),
                        ball.getX() - (SCALED_WIDTH - ball.getWidth())/2f);
                PropertyValuesHolder pvTY = PropertyValuesHolder.ofFloat("y", parent.getHeight() - ball.getY(),
                        ball.getY() - BALL_SIZE/2f);
                ObjectAnimator whxyBouncer = ObjectAnimator.ofPropertyValuesHolder(ball, pvhW, pvhH,
                        pvTX, pvTY).setDuration(DURATION);
                whxyBouncer.setInterpolator(new AccelerateInterpolator());
                whxyBouncer.addUpdateListener(this);
                whxyBouncer.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        setClipping(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setClipping(true);
                    }

                    private void setClipping(boolean set) {
                        container.setClipChildren(set);
                        container.setClipToPadding(set);
                        root.setClipChildren(set);
                        root.setClipToPadding(set);
                    }
                });


                PropertyValuesHolder wSC = PropertyValuesHolder.ofFloat("width", SCALED_WIDTH,
                        SCALED_WIDTH*3);
                PropertyValuesHolder hSC = PropertyValuesHolder.ofFloat("height", SCALED_HEIGHT,
                        SCALED_HEIGHT*3);
                float startX = ball.getX() - (SCALED_WIDTH - ball.getWidth())/2f;
                PropertyValuesHolder adjustX = PropertyValuesHolder.ofFloat("x", startX,
                        startX - (SCALED_WIDTH*3 - SCALED_WIDTH)/2f);
                float startY = ball.getY() - BALL_SIZE/2f;
                PropertyValuesHolder adjustY = PropertyValuesHolder.ofFloat("y", startY,
                        startY - (SCALED_HEIGHT*3 - SCALED_HEIGHT)/2f);

                ObjectAnimator scaleOnly = ObjectAnimator.ofPropertyValuesHolder(ball, wSC, hSC, adjustX, adjustY).setDuration(DURATION);
                scaleOnly.setInterpolator(new DecelerateInterpolator());
                scaleOnly.addUpdateListener(this);

//                ObjectAnimator anim2 = anim1.clone();
//                anim2.setTarget(balls.get(1));


//                ShapeHolder ball2 = balls.get(2);
//                ObjectAnimator animDown = ObjectAnimator.ofFloat(ball2, "y",
//                        0f, getHeight() - ball2.getHeight()).setDuration(500);
//                animDown.setInterpolator(new AccelerateInterpolator());
//                ObjectAnimator animUp = ObjectAnimator.ofFloat(ball2, "y",
//                        getHeight() - ball2.getHeight(), 0f).setDuration(500);
//                animUp.setInterpolator(new DecelerateInterpolator());
//                AnimatorSet s1 = new AnimatorSet();
//                s1.playSequentially(animDown, animUp);
//                animDown.addUpdateListener(this);
//                animUp.addUpdateListener(this);
//                AnimatorSet s2 = (AnimatorSet) s1.clone();
//                s2.setTarget(balls.get(3));

                animation = new AnimatorSet();
//                animation.setInterpolator(new AccelerateInterpolator());
                animation.playSequentially(whxyBouncer, scaleOnly);
//                animation.playTogether(anim1, anim2, s1);
//                animation.playSequentially(s1, s2);
            }
        }

        private ShapeHolder addBall(float x, float y) {
            OvalShape circle = new OvalShape();
            circle.resize(50f * mDensity, 50f * mDensity);
            ShapeDrawable drawable = new ShapeDrawable(circle);
            ShapeHolder shapeHolder = new ShapeHolder(drawable);
            shapeHolder.setX(x - 25f);
            shapeHolder.setY(y - 25f);
            int red = (int)(100 + Math.random() * 155);
            int green = (int)(100 + Math.random() * 155);
            int blue = (int)(100 + Math.random() * 155);
            int color = 0xff000000 | red << 16 | green << 8 | blue;
            Paint paint = drawable.getPaint(); //new Paint(Paint.ANTI_ALIAS_FLAG);
            int darkColor = 0xff000000 | red/4 << 16 | green/4 << 8 | blue/4;
            RadialGradient gradient = new RadialGradient(37.5f, 12.5f,
                    50f, color, darkColor, Shader.TileMode.CLAMP);
            paint.setShader(gradient);
            shapeHolder.setPaint(paint);
            balls.add(shapeHolder);
            return shapeHolder;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            for (int i = 0; i < balls.size(); ++i) {
                ShapeHolder shapeHolder = balls.get(i);
                canvas.save();
                canvas.translate(shapeHolder.getX(), shapeHolder.getY());
//                canvas.scale(shapeHolder.getWidth()/BALL_SIZE,
//                             shapeHolder.getHeight()/BALL_SIZE,
//                             shapeHolder.getX(), shapeHolder.getY());
                shapeHolder.getShape().draw(canvas);
                canvas.restore();
            }
        }

        public void startAnimation() {
            createAnimation();
            animation.start();
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            invalidate();
        }

    }
}
