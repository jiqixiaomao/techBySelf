package com.techbyself.donghua;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

/**
 * Created by Jack on 2015/10/19.
 */
public class LineScaleIndicator extends Indicator {

    public static final float SCALE=1.0f;

    float[] scaleYFloats=new float[]{SCALE, SCALE, SCALE,
            SCALE,SCALE, SCALE, SCALE, SCALE, SCALE,SCALE, SCALE, SCALE,
            SCALE,SCALE, SCALE, SCALE, SCALE, SCALE,SCALE,SCALE, SCALE, SCALE, SCALE, SCALE};

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float translateX=getWidth()/16;
        float translateY=getHeight()/2;
        for (int i = 0; i < 24; i++) {
            canvas.save();
            canvas.translate((2 + i * 2) * translateX - translateX / 2, translateY);
            canvas.scale(SCALE, scaleYFloats[i]);
            RectF rectF=new RectF(-translateX/2,-getHeight()/1f,translateX/2,getHeight()/1f);
            RectF rectF1=new RectF(-translateX/2,-getHeight()/2f,translateX/2,getHeight()/2f);
            RectF rectF2=new RectF(-translateX/2,-getHeight()/3f,translateX/2,getHeight()/3f);
            RectF rectF3=new RectF(-translateX/2,-getHeight()/4f,translateX/2,getHeight()/4f);
           RectF rectF4=new RectF(-translateX/2,-getHeight()/5f,translateX/2,getHeight()/5f);
           RectF rectF5=new RectF(-translateX/2,-getHeight()/6f,translateX/2,getHeight()/6f);
            RectF rectF6=new RectF(-translateX/2,-getHeight()/7f,translateX/2,getHeight()/7f);
           RectF rectF7=new RectF(-translateX/2,-getHeight()/8f,translateX/2,getHeight()/8f);
            RectF rectF8=new RectF(-translateX/2,-getHeight()/9f,translateX/2,getHeight()/9f);
            RectF rectF9=new RectF(-translateX/2,-getHeight()/10f,translateX/2,getHeight()/10f);
            RectF rectF10=new RectF(-translateX/2,-getHeight()/11f,translateX/2,getHeight()/11f);
            RectF rectF11=new RectF(-translateX/2,-getHeight()/12f,translateX/2,getHeight()/12f);
                if(i==0||i==23) {
                        canvas.drawRoundRect(rectF11, 10, 10, paint);
                }
                if(i==1||i==22) {
                    canvas.drawRoundRect(rectF10, 10, 10, paint);
                }
                if(i==2||i==21) {
                    canvas.drawRoundRect(rectF9, 10, 10, paint);
                }
            if(i==3||i==20) {
                canvas.drawRoundRect(rectF9, 10, 10, paint);
            }
            if(i==4||i==19) {
                canvas.drawRoundRect(rectF7, 10, 10, paint);
            }
            if(i==5||i==18) {
                canvas.drawRoundRect(rectF6, 10, 10, paint);
            }
            if(i==6||i==17) {
                canvas.drawRoundRect(rectF5, 10, 10, paint);
            }
            if(i==7||i==16) {
                canvas.drawRoundRect(rectF4, 10, 10, paint);
            }
            if(i==8||i==15) {
                canvas.drawRoundRect(rectF3, 10, 10, paint);
            }
            if(i==9||i==14) {
                canvas.drawRoundRect(rectF2, 10, 10, paint);
            }
            if(i==10||i==13) {
                canvas.drawRoundRect(rectF1, 10, 10, paint);
            }
            if(i==11||i==12) {
                canvas.drawRoundRect(rectF, 10, 10, paint);
            }
            canvas.restore();
        }
    }

    @Override
    public ArrayList<ValueAnimator> onCreateAnimators() {
        ArrayList<ValueAnimator> animators=new ArrayList<>();
        long[] delays=new long[]{100,200,300,400,500,300,400,500};
        for (int i = 0; i < 8; i++) {
            final int index=i;
            ValueAnimator scaleAnim=ValueAnimator.ofFloat(1, 0.3f, 1);
            scaleAnim.setDuration(1000);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);
            addUpdateListener(scaleAnim,new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleYFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            animators.add(scaleAnim);
        }
        return animators;
    }

}
