package com.andoid.rgbPixel.looper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class SquareTrack extends View {

    private Paint drawPaint, canvasPaint = new Paint(Paint.DITHER_FLAG);//El "pincel" que pintará las líneas
    private float paintWidth; //Grosor del pincel
    private int cont, maxCont;
    private float drawX;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    private int squareSide;

    public SquareTrack(Context context, DisplayMetrics metrics/*, AttributeSet attrs*/) {
        super(context/*, attrs*/);

        this.setBackgroundColor(0xFF373636);

        drawPaint = new Paint();
        drawPaint.setColor(0xFFDA2B2B);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(paintWidth);
        drawPaint.setStyle(Paint.Style.STROKE);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        squareSide = ((metrics.widthPixels/2) - 8);
        cont = 1;
        drawX = 0;
    }

    public void draw(){ //Dibuja una línea en el canvas
        drawCanvas.drawLine(drawX+(paintWidth/2), 0,
                drawX+(paintWidth/2), this.getHeight(), drawPaint);
        invalidate();
        cont++;
        drawX = drawX + paintWidth;
    }

    public void cleanCanvas(){ //Borra lo pintado en el canvas
        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR)); //Modo borrar
        drawPaint.setStrokeWidth(this.getWidth()*2); //Establece el grosor del pincel al mismo grosor que el canvas
        drawCanvas.drawLine((paintWidth/2), 0,
                (paintWidth/2), this.getHeight(), drawPaint);
        invalidate();
        drawPaint.setXfermode(null); //Desactiva el modo borrar
        drawPaint.setStrokeWidth(paintWidth);
        cont = 1;
        drawX = 0;
    }

    public void setPaintColor (int color){ drawPaint.setColor(color); }

    public void setPaintWidth(float width){
        paintWidth = width;
        drawPaint.setStrokeWidth(paintWidth);
    }
    public float getPaintWidth (){ return paintWidth; }
    public void setPaintAlpha(int alpha) { drawPaint.setAlpha(alpha); }

    public void setCont (int c){ cont = c; }
    public int getCont (){ return cont; }

    public void setMaxCont (int max){ maxCont = max; }
    public int getMaxCont (){ return maxCont; }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = squareSide;
        int desiredHeight = squareSide;

        /*int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }*/

        //MUST CALL THIS
        setMeasuredDimension(desiredWidth, desiredHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
    }
}
