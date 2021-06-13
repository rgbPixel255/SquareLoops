package com.andoid.rgbPixel.looper;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Tracks{

    Metronome metronome = Metronome.getInstance();
    ColorManager color = ColorManager.getInstance();

    private ConstraintLayout consLyt;
    public LinearLayout lyt1, lyt2;
    private TableLayout.LayoutParams params = //Establece los parámetros de los elementos más grandes
            new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 2.0f);
    private TableLayout.LayoutParams squareParams;
    private TableLayout.LayoutParams recParams;
    private TableLayout.LayoutParams btnParams; //Establece los parámetros de los botones pequeños
    private TableLayout.LayoutParams barParams; //Establece los parámetros de la SeekBar

    public Button deleteBtn, removeBtn;
    public ToggleButton recBtn, muteBtn;
    public SquareTrack square;
    public ImageView compassImage;
    public EditText compass;
    private InputFilter[] filter = new InputFilter[1]; //Limita los caracteres de compass
    public SeekBar volumeBar;

    public PreciseCountdown cdTimer = null;
    private Timer timer;
    private int length; //Duración del track
    private Context context;
    private int squareSide;
    private float volume;
    private MediaPlayer mediaPlayer;
    public boolean wasPlayed = false; //True = El track se estab areproduciendo y debe ser reiniciado
    private File song = null;
    private String track;

    public void createTrack (LinearLayout layout, Context ctx, int x, DisplayMetrics metrics) {
        context = ctx;
        squareSide = ((metrics.widthPixels/2) - 8);
        params.setMargins(4,4,4,4);
        squareParams = new TableLayout.LayoutParams(squareSide, squareSide, 2.0f);
        squareParams.setMargins(4,4,4,4);
        recParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2.0f);
        recParams.setMargins(squareSide/3, 0, squareSide/3, squareSide/5);
        btnParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, squareSide/4, 3.0f);
        btnParams.setMargins(4,4,4,4);
        barParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 400, 2.0f);

        consLyt = new ConstraintLayout(context);
        consLyt.setLayoutParams(squareParams);
        consLyt.setTag(x);
        layout.addView(consLyt);

        square = new SquareTrack(context, metrics);
        square.setPaintColor(color.getColor(x));
        if (x%2 == 0 && x > 4) square.setVisibility(View.INVISIBLE);
        consLyt.addView(square);

        lyt1 = new LinearLayout(context);
        lyt1.setLayoutParams(squareParams);
        lyt1.setOrientation(LinearLayout.VERTICAL);
        consLyt.addView(lyt1);

        lyt2 = new LinearLayout(context);
        lyt2.setLayoutParams(params);
        lyt2.setOrientation(LinearLayout.HORIZONTAL);
        lyt1.addView(lyt2);

        compassImage = new ImageView(context);
        compassImage.setLayoutParams(btnParams);
        compassImage.setImageResource(R.drawable.ic_clear_mtrl_alpha);
        compassImage.setTag(x);
        if (x%2 == 0 && x > 4) compassImage.setVisibility(View.INVISIBLE);
        lyt2.addView(compassImage);

        compass = new EditText(context);
        compass.setLayoutParams(btnParams);
        //compass.setWidth(squareSide/3);
        //compass.setHeight(80);
        compass.setInputType(InputType.TYPE_CLASS_NUMBER);
        filter[0] = new InputFilter.LengthFilter(2);
        compass.setFilters(filter);
        compass.setText("1");
        compass.setTextColor(0xFFAAA9A9);
        compass.setTag(x);
        if (x%2 == 0 && x > 4) compass.setVisibility(View.INVISIBLE);
        lyt2.addView(compass);

        muteBtn = new ToggleButton(context);
        muteBtn.setLayoutParams(btnParams);
        muteBtn.setBackgroundResource(R.drawable.ic_lock_ringer_on_alpha);
        muteBtn.setTextOn("");
        muteBtn.setTextOff("");
        muteBtn.setChecked(true);
        muteBtn.setVisibility(View.INVISIBLE);
        muteBtn.setTag(x);
        lyt2.addView(muteBtn);

        deleteBtn = new Button(context);
        deleteBtn.setLayoutParams(btnParams);
        deleteBtn.setBackgroundResource(R.drawable.ic_menu_delete);
        deleteBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setTag(x);
        lyt2.addView(deleteBtn);

        removeBtn = new Button(context);
        removeBtn.setLayoutParams(
                new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, squareSide/4, 3.0f));
        removeBtn.setBackgroundResource(R.drawable.ic_delete);
        removeBtn.setTag(x);
        if (x < 5 || x%2 == 0 ) removeBtn.setVisibility(View.INVISIBLE);
        lyt2.addView(removeBtn);

        recBtn = new ToggleButton(context);
        recBtn.setLayoutParams(recParams);
        recBtn.setBackgroundResource(R.drawable.ic_voice_search);
        recBtn.setTextOn("");
        recBtn.setTextOff("");
        recBtn.setTextColor(0xFFAAA9A9);
        recBtn.setChecked(false);
        recBtn.setTag(x);
        if (x%2 == 0 && x > 4) recBtn.setVisibility(View.INVISIBLE);
        lyt1.addView(recBtn);

        volumeBar = new SeekBar(context/*, null, R.style.SeekBarColor*/);
        volumeBar.setLayoutParams(barParams);
        volumeBar.setMax(100);
        volumeBar.setProgress(100);
        volumeBar.setBackgroundColor(0x00000000);
        volumeBar.setVisibility(View.INVISIBLE);
        volumeBar.setTag(x);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //volume = (float) progress/100;
                mediaPlayer.setVolume((float) progress/100, (float) progress/100);
                square.setPaintAlpha((progress*2)+ 55);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        lyt1.addView(volumeBar);

        mediaPlayer = null;
        //volume = 1f;
    }

    public void activateTrack() {
        square.setVisibility(View.VISIBLE);
        compassImage.setVisibility(View.VISIBLE);
        compass.setVisibility(View.VISIBLE);
        removeBtn.setVisibility(View.VISIBLE);
        recBtn.setVisibility(View.VISIBLE);
    }

    public void createMP(int x){
        track = Environment.getExternalStorageDirectory()
                + "/audio"+x+".3gp";
        mediaPlayer = new MediaPlayer();
        try{
            mediaPlayer.setDataSource(track);
            mediaPlayer.setLooping(true);
        }catch (IOException e) {}
    }

    public void destroyMP(boolean keepTrack, int x) {
        mediaPlayer.stop();
        mediaPlayer.setVolume(100, 100);
        mediaPlayer.release();
        mediaPlayer = null;
        if (!keepTrack) {
            song = new File(Environment.getExternalStorageDirectory()
                    + "/audio" + x + ".3gp");
            song.delete();
            song = null;
        }
    }

    public void hideTrack(boolean keepTrack, int pos) {
        square.setVisibility(View.INVISIBLE);
        compassImage.setVisibility(View.INVISIBLE);
        compass.setVisibility(View.INVISIBLE);
        muteBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setVisibility(View.INVISIBLE);
        removeBtn.setVisibility(View.INVISIBLE);
        volumeBar.setVisibility(View.INVISIBLE);
        recBtn.setVisibility(View.INVISIBLE);
        if (mediaPlayer != null) destroyMP(keepTrack, pos);
    }

    public void destroyTrack (boolean keepTrack, int pos){
        square.setVisibility(View.GONE);
        compassImage.setVisibility(View.GONE);
        compass.setVisibility(View.GONE);
        muteBtn.setVisibility(View.GONE);
        deleteBtn.setVisibility(View.GONE);
        removeBtn.setVisibility(View.GONE);
        volumeBar.setVisibility(View.GONE);
        recBtn.setVisibility(View.GONE);
        consLyt.setVisibility(View.GONE);
        lyt1.setVisibility(View.GONE);
        lyt2.setVisibility(View.GONE);
        filter[0] = null;
        if (mediaPlayer != null) destroyMP(keepTrack, pos);
    }

    public void startSquare (final Activity activity){
        length = metronome.getBeats()*(Integer.parseInt(compass.getText().toString()))*
                (int)(60000/metronome.getBpm());
        square.setMaxCont((int)Math.floor((float)length/ 5)); // Veces que hay que pintar para llenar el cuadrado completamente
        square.setPaintWidth((float)squareSide / square.getMaxCont()); //Grosor de cada franja = Tamaño del square / (Duración del track / Periodo del thread)
        cdTimer = new PreciseCountdown(length, 5, 0) {
            @Override
            public void onTick(long timeLeft) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        square.draw();
                    }
                });
            }
            @Override
            public void onFinished() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        square.cleanCanvas();
                        //cdTimer.restartParams(length, 5, 0);
                        //cdTimer.start();
                    }
                });
            }
        };
        cdTimer.start();
        /*cdTimer = new CountDownTimer(length, 5) {
            @Override
            public void onTick(long millisUntilFinished) {
                compass.setVisibility(View.VISIBLE);
                compass.setText(""+millisUntilFinished);
            }
            @Override
            public void onFinish() {  }
        }.start();
        /*timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (square.getCont() > square.getMaxCont()) { //Si la línea se dibujaría fuera de los límites del cuadrado
                            square.cleanCanvas(); //Reiniciar el progreso del cuadrado
                            square.draw(); //Dibujar la primera línea
                        } else if (square.getCont() == square.getMaxCont()) {
                            square.draw();
                            try {
                                wait(4);
                            }  catch(InterruptedException e) {
                                System.out.println("got interrupted!");
                            }
                        } else square.draw(); //Dibujar una línea
                    }
                });
            }
        }, 0, 5);*/
    }

    public void stopSquare (Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*timer.cancel();
                timer = null;*/
                //cdTimer.stop();
                cdTimer.destroy();
                cdTimer = null;
                square.cleanCanvas();
            }
        });
    }

    public void restartVolume (){
        volumeBar.setProgress(100);
    }

    public void mute(){
        if (muteBtn.isChecked()) {
            mediaPlayer.setVolume(volume*100, volume*100);
            volumeBar.setProgress(Math.round(volume*100));
            muteBtn.setBackgroundResource(R.drawable.ic_lock_ringer_on_alpha);
        }
        else {
            volume = (float)volumeBar.getProgress()/100;
            mediaPlayer.setVolume(0, 0);
            volumeBar.setProgress(0);
            muteBtn.setBackgroundResource(R.drawable.ic_lock_ringer_off_alpha);
        }
    }

    public ToggleButton getRecBtn() {
        return recBtn;
    }
    public int getCompass() { return Integer.parseInt(compass.getText().toString()); }
    public ToggleButton getMuteBtn() { return muteBtn; }
    public Button getDeleteBtn() { return deleteBtn;}
    public Button getRemoveBtn() { return removeBtn; }
    public void setLength(int l) { length = l; };
    public int getLength() { return length; }

    public void setTag(int x){
        consLyt.setTag(x);
        lyt1.setTag(x);
        lyt2.setTag(x);
        square.setTag(x);
        compassImage.setTag(x);
        compass.setTag(x);
        muteBtn.setTag(x);
        deleteBtn.setTag(x);
        removeBtn.setTag(x);
        volumeBar.setTag(x);
        recBtn.setTag(x);
    }
    public MediaPlayer getMP(){
        if (mediaPlayer != null) return mediaPlayer;
        else return null;
    }
    public void setMP(MediaPlayer mp) {
        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer = mp;
        }
    }

    /*public void squareColor(int x) {
        switch (x) {
            case 1:
                square.setPaintColor(0xFFD54603); //Naranja
                break;
            case 2:
                square.setPaintColor(0xFFD54603); //Violeta
                break;
            case 3:
                square.setPaintColor(0xFF0D9436); //Verde
                break;
            case 4:
                square.setPaintColor(0xFF940D0D); //Magenta
                break;
            case 5:
                square.setPaintColor(0xFFDA2B2B); //Rojo
                break;
            case 6:
                square.setPaintColor(0xFFECC02C); //Amarillo
                break;
            case 7:
                square.setPaintColor(0xFF022F7A); //Azul
                break;
            case 8:
                square.setPaintColor(0xFF048119); //Verde
                break;
        }
    }*/
}