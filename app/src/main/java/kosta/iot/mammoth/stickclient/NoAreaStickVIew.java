package kosta.iot.mammoth.stickclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by kosta on 2016-06-09.
 */
public class NoAreaStickVIew extends ImageView {
    private Point point;  //터치된 좌표
    private Paint paint;  //공용 페인트

    private final int DEFAULT_SIZE = 400; //뷰에 값이 없을경우 기본 크기

    private int DEFAULT_CENTER = DEFAULT_SIZE / 2;  //중간 좌표 값
    private int STICK_RADIUS_BG_SIZE = DEFAULT_CENTER / 3 * 2;  //스틱 배경 원 사이즈

    public NoAreaStickVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.point = new Point(DEFAULT_CENTER, DEFAULT_CENTER);
        this.paint = new Paint();
    }

    @Override  //auto resize를 위한 작업
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = DEFAULT_SIZE;

        switch(widthMode) {
            case MeasureSpec.UNSPECIFIED:    // mode 가 셋팅되지 않은 크기가 넘어올때
            case MeasureSpec.AT_MOST:        // wrap_content 일 경우
                break;
            case MeasureSpec.EXACTLY:  //android:width, height 설정값이 있을때
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                if(width < height)
                    widthSize = width;
                else
                    widthSize = height;
                break;
        }

        setDefaultData(widthSize); //뷰 사이즈에 따라 기본값 다시 설정
        resetPoint();  //스틱 위치 초기화
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Point point = this.point;
        Paint paint = this.paint;

        //배경 원
        paint.setColor(Color.BLACK);
        canvas.drawCircle(DEFAULT_CENTER, DEFAULT_CENTER, STICK_RADIUS_BG_SIZE, paint);
        //스틱 선
        paint.setColor(Color.WHITE);
        canvas.drawLine(DEFAULT_CENTER, DEFAULT_CENTER, point.x, point.y, paint);
        //스틱 동그라미
        paint.setColor(Color.RED);
        canvas.drawCircle(point.x, point.y, STICK_RADIUS_BG_SIZE/2, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_MOVE:  //움직일때
                boundaryCheck((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:  //때었을때
                resetPoint();
                break;
        }

        this.invalidate();

        return true;
    }

    //뷰의 사이즈에 맞게 초기 설정값을 바꿈
    private void setDefaultData(int viewWidth){
        DEFAULT_CENTER = viewWidth / 2;
        STICK_RADIUS_BG_SIZE = DEFAULT_CENTER / 3 * 2;
        this.paint.setStrokeWidth(STICK_RADIUS_BG_SIZE/4);
    }

    //스틱 위치를 중간으로 되돌림
    private void resetPoint() {
        this.point.x = DEFAULT_CENTER;
        this.point.y = DEFAULT_CENTER;
    }

    //중심축을 기준으로 좌표 값 반환
    public Point getPoints() {
        int x = this.point.x;
        int y = this.point.y;

        x = (x - DEFAULT_CENTER) * 100 / STICK_RADIUS_BG_SIZE;
        y = (y - DEFAULT_CENTER) * 100 / STICK_RADIUS_BG_SIZE;

        Point p = new Point(x, y);

        return p;
    }

    //배경 원을 벗어나지 못하게 처리
    private void boundaryCheck(float x, float y) {
        float cx = DEFAULT_CENTER, cy = DEFAULT_CENTER;
        float rx = Math.abs((x - cx));
        float ry = Math.abs((y - cy));
        double radius = Math.sqrt(rx * rx + ry * ry);  //피타고라스 정리
        Point point = this.point;

        if (radius > STICK_RADIUS_BG_SIZE) {
            double tempX = ((double) Math.abs((DEFAULT_CENTER - x)) * (STICK_RADIUS_BG_SIZE / radius));
            double tempY = ((double) Math.abs((DEFAULT_CENTER - y)) * (STICK_RADIUS_BG_SIZE / radius));
            if (x < DEFAULT_CENTER)
                point.x = (int) (DEFAULT_CENTER - tempX);
            else
                point.x = (int) (DEFAULT_CENTER + tempX);

            if (y < DEFAULT_CENTER)
                point.y = (int) (DEFAULT_CENTER - tempY);
            else
                point.y = (int) (DEFAULT_CENTER + tempY);
        } else {
            point.x = (int) x;
            point.y = (int) y;
        }
    }
}


