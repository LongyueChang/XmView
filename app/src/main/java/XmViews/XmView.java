package XmViews;

import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by longyue on 2018/1/23.
 */

public class XmView extends View {
    private Paint textPaint;
    private float value;
    private int textHeight;
    private String mText;
    private Paint baseCirclePaint;
    private Paint redCirclePaint;
    private Paint redTPaint;
    private int centerWidth;
    private int centerHeight;
    private String centerText="0.0度";
    private String[] selection={"W","N","E"};
    private String selectionS="S";
    private Paint centerPaint;
    private int radius;
    private int outRadius;
    private Paint outCirclePaint;
    private Paint otherTonglePaint;
    private Paint lightPaint;
    private Paint degreePaint;
    private Paint bloadPaint;
    private Paint redTextPaint;
    private Paint shaderPaint;
    private ValueAnimator mValueAnimator;
    private float mCameraRotateX;
    private float mCameraRotateY;
    private float mCameraTranslateX;
    private float mCameraTranslateY;
    private Matrix mCameraMatrix;
    private Camera mCamera;
    private int width;
    private float mMaxCameraTranslate;
    private int mMaxCameraRotate=5;
    private Canvas mCanvas;

    public XmView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public XmView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //文本画笔
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setStrokeWidth(2.0f);
        textPaint.setTextSize(40.0f);

        //内圈圆
        baseCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseCirclePaint.setColor(Color.parseColor("#4b4a4a"));
        baseCirclePaint.setStrokeWidth(5.0f);
        baseCirclePaint.setStyle(Paint.Style.STROKE);

        //内圈投影
        shaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shaderPaint.setColor(Color.parseColor("#323232"));
        shaderPaint.setStyle(Paint.Style.FILL);
        shaderPaint.setStrokeWidth(5.0f);

        //外圈圆
        outCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outCirclePaint.setColor(Color.parseColor("#4b4a4a"));
        outCirclePaint.setStrokeWidth(5.0f);
        outCirclePaint.setStyle(Paint.Style.STROKE);

        //外圈红线
        redCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redCirclePaint.setColor(Color.RED);
        redCirclePaint.setStrokeWidth(8.0f);
        redCirclePaint.setStyle(Paint.Style.STROKE);

        //外圈亮线
        lightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightPaint.setColor(Color.WHITE);
        lightPaint.setStyle(Paint.Style.STROKE);
        lightPaint.setStrokeWidth(8.0f);

        //内圈三角形
        redTPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redTPaint.setColor(Color.RED);
        redTPaint.setStrokeWidth(5.0f);
        redTPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        //设置外圈三角形
        otherTonglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        otherTonglePaint.setColor(Color.parseColor("#4b4a4a"));
        otherTonglePaint.setStrokeWidth(5.0f);
        otherTonglePaint.setStyle(Paint.Style.FILL);

        //中间文本画笔设置
        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(Color.WHITE);
        centerPaint.setStrokeWidth(2.0f);
        centerPaint.setTextSize(100);
        centerPaint.setStyle(Paint.Style.FILL);

        //刻度画笔
        degreePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        degreePaint.setColor(Color.parseColor("#4b4a4a"));
        degreePaint.setStrokeWidth(2.0f);
        degreePaint.setStyle(Paint.Style.STROKE);

        //文字红色
        redTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redTextPaint.setColor(Color.RED);
        redTextPaint.setStrokeWidth(2.0f);
        redTextPaint.setStyle(Paint.Style.FILL);
        redTextPaint.setTextSize(50.0f);

        //粗画笔
        bloadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bloadPaint.setColor(Color.WHITE);
        bloadPaint.setStrokeWidth(2.0f);
        bloadPaint.setStyle(Paint.Style.STROKE);

        mCameraMatrix = new Matrix();
        mCamera = new Camera();
    }

    public void setValue(float val) {
        value = val;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        width = Math.min(widthSize, heightSize);
        if(widthMode==MeasureSpec.UNSPECIFIED){
            width =heightSize;
        }else if(heightMode==MeasureSpec.UNSPECIFIED){
            width =widthSize;
        }

        textHeight = width / 6;

        centerWidth = getWidth() / 2;
        centerHeight = getHeight() / 2;

        radius = centerWidth / 2;
        outRadius = centerWidth / 3 * 2;

        mMaxCameraTranslate =0.02f*outRadius;
        //setMeasuredDimension(width,width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        //绘制文本
        drawText(canvas);

        //设置3D晃动效果
        set3DMater();

        //绘制中间投影
        drawShader();

        //绘制内圈
        drawCircle();


        //绘制外圈
        drawOtherCircle();
        //绘制外圈三角形
        drawOtherTongle();

        //绘制刻度
        drawDegree();

        //绘制中间文本
        drawCenterCircle();
    }

    private void set3DMater() {
        mCameraMatrix.reset();
        mCamera.save();
        mCamera.rotateX(mCameraRotateX);
        mCamera.rotateY(mCameraRotateY);
        mCamera.getMatrix(mCameraMatrix);
        mCamera.restore();
        //camera默认旋转是View左上角为旋转中心
        //所以动作之前要，设置矩阵位置 -mTextHeight-mOutSideRadius
        mCameraMatrix.preTranslate(-centerWidth,-centerHeight);
        //动作之后恢复位置
        mCameraMatrix.postTranslate(centerWidth,centerHeight);
        //基于 Canvas 当前的变换，叠加上 Matrix 中的变换。
        mCanvas.concat(mCameraMatrix);
    }

    private void drawShader() {
        mCanvas.save();
        RadialGradient grShader = new RadialGradient(centerWidth, centerHeight, outRadius,
                Color.parseColor("#323232"), Color.parseColor("#000000"), Shader.TileMode.CLAMP);

        shaderPaint.setShader(grShader);
        mCanvas.drawCircle(centerWidth,centerHeight,outRadius,shaderPaint);

        invalidate();
        mCanvas.restore();
    }

    private void drawDegree() {
        mCanvas.save();
        mCanvas.rotate(value,centerWidth,centerHeight);


        for (int i = 0; i < 240; i++) {

//            float x1 = (float) (centerWidth + (radius / 3 - 30) * Math.cos(i*3/2) * Math.PI);
//            float y1 = (float) (centerHeight + (radius / 3 - 30) * Math.sin(i*3/2) * Math.PI);

            float startX = (float) (centerWidth + (radius- 10) * Math.cos((i*3/2) * Math.PI / 180));
            float startY = (float) (centerHeight + (radius- 10) * Math.sin((i*3/2) * Math.PI / 180));
            float endX = (float) (centerWidth + (radius- 30) * Math.cos((i*3/2) * Math.PI / 180));
            float endY = (float) (centerHeight + (radius - 30) * Math.sin((i*3/2) * Math.PI / 180));

            float x1 = (float) (centerWidth + (radius- 50) * Math.cos((i*3/2) * Math.PI / 180));
            float y1 = (float) (centerHeight + (radius - 50) * Math.sin((i*3/2) * Math.PI / 180));


            if(i==0||i==60||i==120||i==180) {
                mCanvas.drawLine(startX, startY, endX, endY, bloadPaint);
            }else {
                mCanvas.drawLine(startX, startY, endX, endY, degreePaint);
            }

            if(i==60){
                mCanvas.drawText(selectionS,x1,y1,redTextPaint);
            }else if(i==120){
               mCanvas.drawText(selection[0],x1,y1,textPaint);
            }else if(i==180){
               mCanvas.drawText(selection[1],x1,y1,textPaint);
            }else if(i==0){
              mCanvas.drawText(selection[2],x1,y1,textPaint);
            }
        }



        invalidate();
        mCanvas.restore();
    }

    private void drawOtherTongle() {
        mCanvas.save();
        Path tonglePath = new Path();
        tonglePath.moveTo(centerWidth,centerHeight-outRadius-textHeight/4);
        tonglePath.lineTo(centerWidth-textHeight/4,centerHeight-outRadius);
        tonglePath.lineTo(centerWidth+textHeight/4,centerHeight-outRadius);
        tonglePath.close();
        mCanvas.drawPath(tonglePath,otherTonglePaint);

        invalidate();
        mCanvas.restore();
    }

    private void drawOtherCircle() {
        mCanvas.save();
        RectF rectF = new RectF
                (centerWidth - outRadius, centerHeight - outRadius, centerWidth + outRadius, centerHeight + outRadius);

        mCanvas.drawArc(rectF,120.0f,140.0f,false,outCirclePaint);
        mCanvas.drawArc(rectF,280.0f,140.0f,false,outCirclePaint);

        //绘制红线
        mCanvas.drawArc(rectF,120.0f,130.0f,false,redCirclePaint);

        //绘制外圈亮线
        mCanvas.drawArc(rectF,60.0f,10.0f,false,lightPaint);

        invalidate();

        mCanvas.restore();
    }

    private void drawCenterCircle() {
        Paint.FontMetricsInt fm = centerPaint.getFontMetricsInt();

        float startWidth = centerWidth-centerPaint.measureText((int)value + "C·")/2;
        int baseLine = centerHeight + (fm.bottom - fm.top) / 2 - fm.bottom;
        mCanvas.drawText((int)value+"C·",startWidth,baseLine,centerPaint);
        invalidate();
    }

    private void drawCircle() {
        mCanvas.save();
        RectF rectF = new RectF(centerWidth-radius,centerHeight-radius,centerWidth+radius,centerHeight+radius);

        //绘制内圆
        mCanvas.drawCircle(centerWidth,centerHeight,radius,baseCirclePaint);
        //绘制内圈弧线
        mCanvas.drawArc(rectF,-90.0f,value-180.0f,false,redCirclePaint);

        //绘制内圈三角形
        Path tonglePath = new Path();

        float x1 = (float) (centerWidth + radius * Math.cos((value+90) * Math.PI / 180));
        float y1 = (float) (centerHeight + radius * Math.sin((value+90) * Math.PI / 180));


        float pointRadius=20.0f;

        mCanvas.drawCircle(x1,y1,pointRadius,redTPaint);


        int mTriangleHeight=(outRadius-radius)/2;
        mCanvas.rotate(value,getWidth()/2,outRadius+textHeight);

        tonglePath.moveTo(x1,y1);

        //内接三角形的边长,简单数学运算
        float mTriangleSide = (float) ((mTriangleHeight/(Math.sqrt(3)))*2);
        tonglePath.lineTo(x1-mTriangleSide/2,textHeight+mTriangleHeight*2);
        tonglePath.lineTo(x1+mTriangleSide/2,textHeight+mTriangleHeight*2);
        tonglePath.close();

        //mCanvas.drawPath(tonglePath,redTPaint);

        invalidate();
        mCanvas.restore();
    }

    private void drawText(Canvas canvas) {

        if (value <=15|| value >=345){
            mText = "北";
        }else if (value >15&& value <=75){
            mText = "东北";
        }else if (value >75&& value <=105){
            mText = "东";
        }else if (value >105&& value <=165){
            mText ="东南";
        }else if (value >165&& value <=195){
            mText = "南";
        }else if (value >195&& value <=255){
            mText = "西南";
        }else if (value >255&& value <=285){
            mText = "西";
        }else if (value >285&& value <345){
            mText ="西北";
        }

        Paint.FontMetricsInt fm = textPaint.getFontMetricsInt();
        int centerLine = textHeight / 2;
        int baseLine = centerLine + (fm.bottom - fm.top) / 2 - fm.bottom;

        int startWidth = (int) (centerWidth - textPaint.measureText(mText)/2);

        canvas.drawText(mText,startWidth, baseLine, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            //按下
            case MotionEvent.ACTION_DOWN:
                if(mValueAnimator!=null&&mValueAnimator.isRunning()){
                    mValueAnimator.cancel();
                }
                getCameraRotate(event);
                getCameraTranslate(event);
                break;

            //移动
            case MotionEvent.ACTION_MOVE:
                Log.i("TAG------------>", "手指移动："+event.getX());
                getCameraRotate(event);
                getCameraTranslate(event);
                break;

            //抬起
            case MotionEvent.ACTION_UP:
                Log.i("TAG------------>", "手指抬起了");
                startRestore();
                break;
        }

        return true;
    }

    private void startRestore() {
        final String cameraRotateXName = "cameraRotateX";
        final String cameraRotateYName = "cameraRotateY";
        final String canvasTranslateXName = "canvasTranslateX";
        final String canvasTranslateYName = "canvasTranslateY";
        PropertyValuesHolder cameraRotateXHolder =
                PropertyValuesHolder.ofFloat(cameraRotateXName, mCameraRotateX, 0);
        PropertyValuesHolder cameraRotateYHolder =
                PropertyValuesHolder.ofFloat(cameraRotateYName, mCameraRotateY, 0);
        PropertyValuesHolder canvasTranslateXHolder =
                PropertyValuesHolder.ofFloat(canvasTranslateXName, mCameraTranslateX, 0);
        PropertyValuesHolder canvasTranslateYHolder =
                PropertyValuesHolder.ofFloat(canvasTranslateYName, mCameraTranslateY, 0);
        mValueAnimator = ValueAnimator.ofPropertyValuesHolder(cameraRotateXHolder,
                cameraRotateYHolder, canvasTranslateXHolder, canvasTranslateYHolder);
        mValueAnimator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                float f = 0.571429f;
                return (float) (Math.pow(2, -2 * input) * Math.sin((input - f / 4) * (2 * Math.PI) / f) + 1);
            }
        });
        mValueAnimator.setDuration(2000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCameraRotateX = (float) animation.getAnimatedValue(cameraRotateXName);
                mCameraRotateY = (float) animation.getAnimatedValue(cameraRotateYName);
                mCameraTranslateX = (float) animation.getAnimatedValue(canvasTranslateXName);
                mCameraTranslateY = (float) animation.getAnimatedValue(canvasTranslateYName);

                Log.i("TAG------------>", "监听动画参数:"+mCameraRotateX+","+mCameraRotateY+","+mCameraTranslateX+","+mCameraTranslateY);
            }
        });
        mValueAnimator.start();
    }

    /**
     * 获取Camera，平移大小
     * @param event
     */
    private void getCameraTranslate(MotionEvent event) {
        float translateX = (event.getX() - getWidth() / 2);
        float translateY = (event.getY() - getHeight()/2);
        //求出此时位移的大小与半径之比
        float[] percentArr = getPercent(translateX, translateY);
        //最终位移的大小按比例匀称改变
        mCameraTranslateX = percentArr[0] * mMaxCameraTranslate;
        mCameraTranslateY = percentArr[1] * mMaxCameraTranslate;
    }
    /**
     * 让Camera旋转,获取旋转偏移大小
     * @param event
     */
    private void getCameraRotate(MotionEvent event) {
        float mRotateX = -(event.getY()-(getHeight())/2);
        float mRotateY = (event.getX()-getWidth()/2);
        //求出旋转大小与半径之比
        float[] percentArr = getPercent(mRotateX,mRotateY);
        mCameraRotateX = percentArr[0]*mMaxCameraRotate;
        mCameraRotateY = percentArr[1]*mMaxCameraRotate;
    }
    /**
     * 获取比例
     * @param mCameraRotateX
     * @param mCameraRotateY
     * @return
     */
    private float[] getPercent(float mCameraRotateX, float mCameraRotateY) {
        float[] percentArr = new float[2];
        float percentX = mCameraRotateX/width;
        float percentY = mCameraRotateY/width;
        //处理一下比例值
        if (percentX > 1) {
            percentX = 1;
        } else if (percentX < -1) {
            percentX = -1;
        }
        if (percentY > 1) {
            percentY = 1;
        } else if (percentY < -1) {
            percentY = -1;
        }
        percentArr[0] = percentX;
        percentArr[1] = percentY;
        return percentArr;
    }
}
