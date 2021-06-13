package com.andoid.rgbPixel.looper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

public class MetronomeBar extends View {
    Metronome metronome = Metronome.getInstance();

    private Paint drawPaint, canvasPaint = new Paint(Paint.DITHER_FLAG);//El "pincel" que pintará las líneas
    private int paintColor = 0xFFF1D800; //Color del pincel
    private float paintWidth; //Grosor del pincel
    private int cont, maxCont;
    private float drawX, barX;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    private int width, height;

    public MetronomeBar (Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setBackgroundColor(0xFF282727);

        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(paintWidth);
        drawPaint.setStyle(Paint.Style.STROKE);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        cont = 1;
        drawX = 0;
        width = this.getWidth();
        height = this.getHeight();
    }

    public void draw(){ //Dibuja una línea en el canvas
        //-----------------Borrar línea antigua--------------------------
        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR)); //Modo borrar
        drawCanvas.drawLine(drawX-(paintWidth/2), 0,
                drawX-(paintWidth/2), this.getHeight(), drawPaint);
        //-----------------Dibujar línea nueva--------------------------
        drawPaint.setXfermode(null); //Desactiva el modo borrar
        drawCanvas.drawLine(drawX+(paintWidth/2), 0,
                drawX+(paintWidth/2), this.getHeight(), drawPaint);
        /*drawCanvas.drawLine(drawX+(paintWidth/2),0,
                drawX +(paintWidth/2),this.getHeight(), drawPaint);*/ //Para testeo
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

    public void setPaintWidth(float w){
        paintWidth = w;
        drawPaint.setStrokeWidth(paintWidth);
    }
    public float getPaintWidth (){ return paintWidth; }

    public void setCont (int c){ cont = c; }
    public int getCont (){ return cont; }

    public void setDrawX (int x) { drawX = x; }

    public void setMaxCont (int max){ maxCont = max; }
    public int getMaxCont (){ return maxCont; }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 100;
        int desiredHeight = 100;

        int widthMode = MeasureSpec.AT_MOST;//MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.AT_MOST;//MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.max(desiredWidth, widthSize);
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
            height = Math.max(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
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
        barX = (float) width/metronome.getBeats();
        drawPaint.setColor(0xFFAAA9A9);
        for (int i=1; i<metronome.getBeats(); i++) {
            drawCanvas.drawLine(barX * i, 0,
                    barX*i, height, drawPaint);
        }
        //Margen izquierdo
        drawCanvas.drawLine(paintWidth/2, 0,
                paintWidth/2, this.getHeight(), drawPaint);
        //Margen derecho
        drawCanvas.drawLine(this.getWidth()-paintWidth/2, 0,
                this.getWidth()-paintWidth/2, this.getHeight(), drawPaint);
        //Margen superior
        drawCanvas.drawLine(0, paintWidth/2,
                this.getWidth(), paintWidth/2, drawPaint);
        //Margen inferior
        drawCanvas.drawLine(0, this.getHeight()-paintWidth/2,
                this.getWidth(), this.getHeight()-paintWidth/2, drawPaint);
        drawPaint.setColor(paintColor);
        invalidate();
    }

}