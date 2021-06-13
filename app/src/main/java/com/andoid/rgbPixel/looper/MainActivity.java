package com.andoid.rgbPixel.looper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    private Metronome copyMetronome;
    Metronome metronome = Metronome.getInstance();
    ColorManager color = ColorManager.getInstance();

    private static final String LOG_TAG = "Looper";
    private MediaPlayer mediaPlayer;
    private Thread mThread, sThread, mBarThread;

    private TextView contTextView; //Metrónomo visual
    private Timer timer = null, timah = null/*, mBarTimer = null*/;
    private PreciseCountdown cdMetronome = null, cdRec = null, mBarTimer = null;
    private MetronomeBar mBar;
    private boolean soundMetronome = false; //True = Metrómo suena; Fales = Metrónomo no suena
    private boolean isPlaying = false; //True = Existe alguna grabación de tracks; False = No existe ninguna grabación de tracks;
    private boolean isRecording = false; //True = Algún track está sonando; False = Ningún track está sonando
    private boolean remove = false;//True = Un track está pendiente de ser eliminado;
    private int posRecording; //Referencia a la pos del track que se está grabando
    private int recDelay = 0; //Retardo de la grabadora
    private int timeBeat; //Tiempo entre cada pulso del metrónomo
    private int beatCont = 1; //Para saber cuando empieza el compás
    private int compassCont = 0; //Indica cuántos compases lleva grabados

    private float percent = 0;

    //Gestión de tracks
    private Tracks[] tracks = new Tracks[15];
    private Tracks auxTrack;
    private MediaRecorder[] records = new MediaRecorder[25]; //Guarda los MediaRecords de los tracks grabados
    private int trackToRemove = 0; //Señala el track que está preparado para eliminarse
    private Context baseContext;
    private Button auxBtn; //auxBtn guarda el btn que está siendo usado
    private Button generateBtn, settingsBtn;
    private ToggleButton auxTglBtn;
    //private SquareTrack auxsquare;
    private LinearLayout mainLayout, lyt1, lyt2, lyt3, lyt4;
    private ConstraintLayout consLyt1, consLyt2, consLyt3, consLyt4;
    private ScrollView scrollV;
    private int x = 5; //Indica el siguiente al último track del array tracks[]
    private File song, renamedSong;
    private DisplayMetrics metrics;

    private boolean traza = false;

    private static String track;
    //private static String metronomeSound = Environment.getExternalStorageDirectory().getAbsolutePath()+"/beep.wav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.mainLayout);
        lyt1 = findViewById(R.id.lyt1);
        lyt2 = findViewById(R.id.lyt2);
        lyt3 = findViewById(R.id.lyt3);
        lyt4 = findViewById(R.id.lyt4);
        /*consLyt1 = findViewById(R.id.consLyt1);
        consLyt2 = findViewById(R.id.consLyt2);
        consLyt3 = findViewById(R.id.consLyt3);
        consLyt4 = findViewById(R.id.consLyt4);*/
        generateBtn = findViewById(R.id.generateBtn);
        settingsBtn = findViewById(R.id.settings);
        mBar = findViewById(R.id.mBar);

        mThread = null;
        metrics= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        metronome.setBeatSound(1000);
        metronome.setSound(10000);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //Generar los seis primeros tracks por defecto
        for (int i = 1; i < 5; i++) {
            tracks[i] = new Tracks();
            baseContext = getBaseContext();
            //--------------ORDENAR LOS CUADRADOS------------------------------
            switch (i) {
                case 1:
                case 2:
                    tracks[i].createTrack(lyt1, baseContext, i, metrics);
                    break;
                case 3:
                case 4:
                    tracks[i].createTrack(lyt2, baseContext, i, metrics);
                    break;
            }
            //-----------------------------------------------------------------
            auxBtn = tracks[i].getRecBtn();
            auxBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkRecBtn((Integer) v.getTag());
                }
            });
            auxTglBtn = tracks[i].getMuteBtn();
            auxTglBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    muteTrack((Integer) v.getTag());
                }
            });
            auxBtn = tracks[i].getDeleteBtn();
            auxBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteSong((Integer) v.getTag());
                }
            });
        }

        /*consLyt = new ConstraintLayout(getBaseContext());
        consLyt.setLayoutParams(params);
        lyt1.addView(consLyt);
        s = new SquareTrack(getBaseContext());
        s.setLayoutParams(params);
        consLyt.addView(s);*/
    }

    public void generateTrack(final View view) {
        baseContext = getBaseContext();
        if (x%2 == 0) tracks[x].activateTrack();
        else {
            tracks[x] = new Tracks();
            tracks[x+1] = new Tracks();
            switch (x) {
                case 5:
                    tracks[x].createTrack(lyt3, baseContext, x, metrics);
                    tracks[x+1].createTrack(lyt3, baseContext, x+1, metrics);
                    break;
                case 7:
                    tracks[x].createTrack(lyt4, baseContext, x, metrics);
                    tracks[x + 1].createTrack(lyt4, baseContext, x + 1, metrics);
                    break;
            }
        }
        auxBtn = tracks[x].getRecBtn();
        auxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRecBtn((Integer) v.getTag());
            }
        });
        auxTglBtn = tracks[x].getMuteBtn();
        auxTglBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muteTrack((Integer) v.getTag());
            }
        });
        auxBtn = tracks[x].getDeleteBtn();
        auxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSong((Integer) v.getTag());
            }
        });
        auxBtn = tracks[x].getRemoveBtn();
        auxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preRemoveTrack((Integer) v.getTag());
            }
        });
        x++;
        if (x == 9) generateBtn.setEnabled(false);
    }

    public void checkRecBtn(int pos){
        if (tracks[pos].getRecBtn().isChecked()){
            prepareRec(pos);
            //----------- APARIENCIA------------------
            tracks[pos].compassImage.setVisibility(View.INVISIBLE);
            tracks[pos].compass.setVisibility(View.INVISIBLE);
            tracks[pos].recBtn.setBackgroundResource(R.drawable.ic_clear_mtrl_alpha);
            changeBtnsState(pos, false); //Desactivar los demás recBtn
            //----------------------------------------
            //tracks[pos].startSquare(this);
            posRecording = pos;
            soundMetronome = true;
            if (cdMetronome == null) {
                if (metronome.getPrevious()) compassCont = 0;
                else compassCont = 1;
                playMetronome(this);
                startMBar(this);
            } else if (metronome.getPrevious()) compassCont = -1;
                    else compassCont = 0;
        } else {
            tracks[pos].recBtn.setBackgroundResource(R.drawable.ic_voice_search);
            interruptRec(pos);
        }
    }
    //----------------SINCRONIZAR----------------
    //public void calibrate (View v){
        //track = Environment.getExternalStorageDirectory()/*.getAbsolutePath()*/ + "/test"
        //        + ".3gp";
        /*records[21] = new MediaRecorder();
        records[21].setOutputFile(track);
        records[21].setAudioSource(MediaRecorder.AudioSource.MIC);
        records[21].setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        records[21].setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            records[21].prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Fallo en grabación");
        }
        records[21].start();
    }

    public void countDown() {
        new CountDownTimer(5000,1000){
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                records[21].start();
                records[21].release();
                try {
                    mediaPlayer.setDataSource(Environment.getExternalStorageDirectory()
                            + "/test" + ".3gp");
                    recDelay = 5000 - mediaPlayer.getDuration();
                    contTextView.setText(recDelay);
                }catch (IOException e) {}
            }
        }.start();
    }*/
    //-------------------------------------------

    public void prepareRec(int pos) {
        track = Environment.getExternalStorageDirectory()/*.getAbsolutePath()*/ + "/audio"
                + pos + ".3gp";
        records[pos] = new MediaRecorder();
        records[pos].setOutputFile(track);
        records[pos].setAudioSource(MediaRecorder.AudioSource.MIC);
        records[pos].setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        records[pos].setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            records[pos].prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Fallo en grabación");
        }
        //compassCont = 1;
        //beatCont = 1;
        //isPlaying = true; //No es true hasta que empiece la grabación
    }

    public void startRec(final Activity activity, int pos) {
        records[pos].start();
        cdRec = new PreciseCountdown(timeBeat*metronome.getBeats()*tracks[posRecording].getCompass(),
                timeBeat*metronome.getBeats()*tracks[posRecording].getCompass(),0) {
            @Override
            public void onTick(long timeLeft) { }
            @Override
            public void onFinished() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopRec(posRecording);
                    }
                });
            }
        };
        cdRec.start();
        /*cdRec = new CountDownTimer(timeBeat*metronome.getBeats()*tracks[posRecording].getCompass(),
                timeBeat*metronome.getBeats()*tracks[posRecording].getCompass()) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                        stopRec(posRecording);
                     }
        }.start();*/
    }

    public void record(int pos) {
        track = Environment.getExternalStorageDirectory()/*.getAbsolutePath()*/ + "/audio"
                + pos + ".3gp";
        records[pos] = new MediaRecorder();
        records[pos].setOutputFile(track);
        records[pos].setAudioSource(MediaRecorder.AudioSource.MIC);
        records[pos].setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        records[pos].setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            records[pos].prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Fallo en grabación");
        }
        //compassCont = 1;
        //beatCont = 1;
        isPlaying = true;
        metronome.setIsPLaying(true);
        records[pos].start();
        //playMetronome(pos);
    }

    public void changeBtnsState(int pos, boolean state) {
        generateBtn.setEnabled(state);
        settingsBtn.setEnabled(state);
        for (int i=1;i<x;i++) {
            if (i!= pos) { //Cambia el estado de todos los recBtn menos del que está siendo utilizado
                if (state) tracks[i].square.setBackgroundColor(0xFF373636); //Gris oscuro de activo
                else tracks[i].square.setBackgroundColor(0xFF929191); //Gris claro de inactivo
                tracks[i].compass.setEnabled(state);
                if (i > 4) tracks[i].removeBtn.setEnabled(state);
                tracks[i].recBtn.setEnabled(state);
            }
        }
    }

    public void changeTrackState(int pos, boolean state) {
        if (state) tracks[pos].square.setBackgroundColor(0xFF373636); //Gris oscuro de activo
        else tracks[pos].square.setBackgroundColor(0xFF929191); //Gris claro de inactivo
        tracks[pos].compass.setEnabled(state);
        tracks[pos].muteBtn.setEnabled(state);
        tracks[pos].deleteBtn.setEnabled(state);
        tracks[pos].removeBtn.setEnabled(state);
        tracks[pos].volumeBar.setEnabled(state);
        tracks[pos].recBtn.setEnabled(state);
    }

    public void transferTrack(int pos) {
        //----------------COPIAR APARIENCIA DEL SIGUIENTE TRACK----------------------------
        tracks[pos].square.setVisibility(tracks[pos+1].square.getVisibility());
        tracks[pos].compassImage.setVisibility(tracks[pos+1].compassImage.getVisibility());
        tracks[pos].compass.setVisibility(tracks[pos+1].compass.getVisibility());
        tracks[pos].compass.setText(tracks[pos+1].compass.getText());
        tracks[pos].muteBtn.setVisibility(tracks[pos+1].muteBtn.getVisibility());
        tracks[pos].deleteBtn.setVisibility(tracks[pos+1].deleteBtn.getVisibility());
        tracks[pos].removeBtn.setVisibility(tracks[pos+1].removeBtn.getVisibility());
        tracks[pos].volumeBar.setVisibility(tracks[pos+1].volumeBar.getVisibility());
        tracks[pos].recBtn.setVisibility(tracks[pos+1].recBtn.getVisibility());
        tracks[pos].square.setPaintColor(color.getColor(pos+1));
        tracks[pos+1].square.setVisibility(View.VISIBLE);
        //----------------RESETEAR APARIENCIA DEL SIGUIENTE TRACK----------------------------
        if ((pos+1) == (x-1)) { //Si el siguiente track es el último
            tracks[pos + 1].compassImage.setVisibility(View.VISIBLE);
            tracks[pos + 1].compass.setVisibility(View.VISIBLE);
            tracks[pos + 1].muteBtn.setVisibility(View.INVISIBLE);
            tracks[pos + 1].muteBtn.setChecked(true);
            tracks[pos + 1].muteBtn.setBackgroundResource(R.drawable.ic_lock_ringer_on_alpha);
            tracks[pos + 1].deleteBtn.setVisibility(View.INVISIBLE);
            tracks[pos + 1].removeBtn.setVisibility(View.INVISIBLE);
            tracks[pos + 1].volumeBar.setVisibility(View.INVISIBLE);
            tracks[pos + 1].recBtn.setVisibility(View.INVISIBLE);
            tracks[pos + 1].recBtn.setChecked(false);
            tracks[pos + 1].recBtn.setBackgroundResource(R.drawable.ic_voice_search);
            if ((x-1)%2 == 0) tracks[x-1].hideTrack(true, pos+1);
            else {
                tracks[x-1].destroyTrack(true, x-2); //Último track
                tracks[x].destroyTrack(false, x-1); //Siguiente al último track, se encuentra oculto
            }
        }
        //----------------TRANSFERIR AUDIO DEL SIGUIENTE TRACK----------------------------
        song = new File(Environment.getExternalStorageDirectory()+
                "/audio"+(pos+1)+".3gp");
        renamedSong = new File(Environment.getExternalStorageDirectory()+
                "/audio"+pos+".3gp");
        song.renameTo(renamedSong);
        song.delete();
        song = null;
        renamedSong = null;
        track = Environment.getExternalStorageDirectory()
                + "/audio"+pos+".3gp";
        mediaPlayer = new MediaPlayer();
        //tracks[pos].createMP(pos);
        //mediaPlayer = tracks[pos].getMP();
        try{
            mediaPlayer.setDataSource(track);
            mediaPlayer.setLooping(true);
            //tracks[pos].setMP(mediaPlayer);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {}
    }

    public void checkLastTrack(){
        for (int i=1;i<x;i++){ //Comprueba si algún track tiene un record existente
            if (records[i] != null) isPlaying = true;
        }
    }

    public void interruptRec(int pos) {
        if (mThread != null) stopMetronome();
        else soundMetronome = false; //Esto ya lo hace stopMetronome()
        if (compassCont > 0) tracks[pos].stopSquare(this);
        changeBtnsState(pos, true); //Reactivar los demás recBtn
        tracks[pos].compassImage.setVisibility(View.VISIBLE);
        tracks[pos].compass.setVisibility(View.VISIBLE);
        if (isRecording) records[pos].stop();
        records[pos].release();
        records[pos] = null;
        if (isRecording) {
            isRecording = false;
            song = new File(Environment.getExternalStorageDirectory()
                    + "/audio" + pos + ".3gp");
            song.delete();
            song = null;
            cdRec.stop();
            cdRec = null;
        }
        isPlaying = false;
        checkLastTrack(); //Comprobar si algún track con algo grabado
        metronome.setIsPLaying(isPlaying);
        if (!isPlaying) { //Si ningún track tiene nada grabado
            /*timer.cancel();
            timer = null;*/
            cdMetronome.destroy();
            cdMetronome = null;
            beatCont = 1;
            compassCont = 0;
            stopMBar();
        }
    }

    public void stopRec(int pos) {
        records[pos].stop();
        records[pos].release();
        isRecording = false;
        if (mThread != null) stopMetronome();
        else soundMetronome = false;
        //----------- APARIENCIA------------------
        changeBtnsState(pos, true); //Reactivar los demás recBtn
        tracks[pos].recBtn.setVisibility(View.INVISIBLE);
        tracks[pos].muteBtn.setEnabled(true);
        tracks[pos].deleteBtn.setEnabled(true);
        tracks[pos].muteBtn.setVisibility(View.VISIBLE);
        tracks[pos].deleteBtn.setVisibility(View.VISIBLE);
        tracks[pos].volumeBar.setVisibility(View.VISIBLE);
        //----------------------------------------
        /*song = new File(Environment.getExternalStorageDirectory()
                + "/audio" + pos + ".3gp");
        byte[] b = new byte[3];
        song = null;
        song = new File(Environment.getExternalStorageDirectory()
                + "/audio1.3gp");*/
        /*----------------------------------------------------------------------------
        PREPARE TRACK
        ------------------------------------------------------------------------------*/
        tracks[pos].createMP(pos);
        mediaPlayer = tracks[pos].getMP();
        try {
            mediaPlayer.prepare();
            //mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Fallo en reproducción");
        }
    }

    public void playTrack(int pos) { mediaPlayer.start(); }

    public void muteTrack(int pos) {
        if (tracks[pos].getMP() != null)
            tracks[pos].mute();
    }

    public void deleteSong(int pos){
        tracks[pos].stopSquare(this);
        records[pos] = null;
        tracks[pos].destroyMP(false, pos);
        tracks[pos].volumeBar.setProgress(100);
        tracks[pos].compassImage.setVisibility(View.VISIBLE);
        tracks[pos].compass.setVisibility(View.VISIBLE);
        tracks[pos].muteBtn.setVisibility(View.INVISIBLE);
        tracks[pos].deleteBtn.setVisibility(View.INVISIBLE);
        tracks[pos].volumeBar.setVisibility(View.INVISIBLE);
        tracks[pos].recBtn.setVisibility(View.VISIBLE);
        tracks[pos].recBtn.setChecked(false);
        tracks[pos].recBtn.setBackgroundResource(R.drawable.ic_voice_search);
        isPlaying = false;
        checkLastTrack();
        metronome.setIsPLaying(isPlaying);
        if (!isPlaying){ //Si ningún track tiene un record, el metrónomo se para y se prepara para su reinicio
            /*timer.cancel();
            timer = null;*/
            cdMetronome.destroy();
            cdMetronome = null;
            soundMetronome = false;
            beatCont = 1;
            compassCont = 0;
            stopMBar();
            if (remove) removeTrack(pos);
        }
    }

    /*public void playTrack(int pos) {
        tracks[pos].createMP(pos);
        mediaPlayer = tracks[pos].getMP();
        tracks[pos].muteBtn.setEnabled(true);
        tracks[pos].muteBtn.setVisibility(View.VISIBLE);
        tracks[pos].deleteBtn.setVisibility(View.VISIBLE);
        tracks[pos].volumeBar.setEnabled(true);
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
            File track = new File(Environment.getExternalStorageDirectory()
                    + "/audio" + pos + ".3gp");
            track.delete();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Fallo en reproducción");
        }
    }*/

    public void preRemoveTrack(int pos) {
        if (cdMetronome != null) {
            changeTrackState(pos, false);
            trackToRemove = pos;
            remove = true;
            if (records[pos] != null) {
                deleteSong(pos);
            }
        } else removeTrack(pos);
    }

    public void removeTrack(int pos) {
        /*if (records[pos] != null) {
            deleteSong(pos);
        }
            /*records[pos] = null;
            mediaPlayer = tracks[pos].getMP();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            song = new File(Environment.getExternalStorageDirectory()
                    + "/audio" + pos + ".3gp");
            song.delete();
            song = null;
        }
        tracks[pos].volumeBar.setProgress(100);*/
        remove =false;
        changeTrackState(pos, true);
        if (pos == x-1) { //Si el track borrado es el último
            if (pos % 2 == 0) {
                tracks[pos].hideTrack(false, pos);
            } else {
                tracks[pos].destroyTrack(false, pos);
                tracks[pos + 1].destroyTrack(false, pos + 1);
                tracks[pos] = null;
                tracks[pos+1] = null;
            }
        } else {
             for (int i=pos;i<(x-1);i++) transferTrack(pos);
            //tracks[pos+1].getMP().stop();
            //tracks[pos+1] = null;
            color.reorganize(pos, x-1);
        }
        /*if (pos < x-1) {
            for (int j = pos; j < x-1; j++) {
                tracks[j].compassImage.setVisibility(tracks[j+1].compassImage.getVisibility());
                tracks[j].compass.setVisibility(tracks[j+1].compass.getVisibility());
                tracks[j].square.setVisibility(tracks[j+1].square.getVisibility());
                tracks[j].muteBtn.setVisibility(tracks[j+1].muteBtn.getVisibility());
                tracks[j].deleteBtn.setVisibility(tracks[j+1].deleteBtn.getVisibility());
                tracks[j].removeBtn.setVisibility(tracks[j+1].removeBtn.getVisibility());
                tracks[j].recBtn.setVisibility(tracks[j+1].recBtn.getVisibility());
            }
        }
        if (((x-1)%2 == 0) || (pos != x-1)) { //Si el track eliminado no ha sido destruído, reactivar el track
            changeBtnsState(pos,true);
        }
        tracks[pos] = null;
        if (pos < x-1) { //x = una posición más dle último track creado => Si pos sigue siendo más pequeño que x-1, es que este track no es el último creado
            for (int i = pos; i < x-1; i++) {//Recolocar los tracks con pos > que el track borrado
                tracks[i] = tracks[i+1];
                tracks[i].setTag(i);
                tracks[i].square.setPaintColor(color.getColor(i+1)); //Pintar el square del color antiguo
                tracks[i+1] = null;
            }
            color.reorganize(pos, (x-1));
        }
        if ((x-1)%2 == 0) tracks[x-1].hideTrack();//Ocultar el último track
        else {
            try {
                tracks[x-1].destroyTrack(x-1); //Destruir el último track
                tracks[x].destroyTrack(x);
                if (pos != x-1 && tracks[x-1].getMP() != null) { //Si el track borrado no es el último
                    tracks[x - 2].getMP().prepare();
                    tracks[x - 2].getMP().start();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Fallo en reproducción");
            }
        }*/
        x--;
        if (x == 8) generateBtn.setEnabled(true);
    }

    public void syncTracks() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBarTimer.stop();
                mBarTimer.restartParams(metronome.getBeats()*(int)(60000/metronome.getBpm()),
                        5, 0);
                mBarTimer.start();
                for (int i = 1; i < x; i++) {
                    if (tracks[i].getMP() != null){
                        tracks[i].getMP().seekTo(0);
                        tracks[i].cdTimer.stop();
                        tracks[i].cdTimer.restartParams(tracks[i].getLength(), 5, 0);
                        tracks[i].cdTimer.start();
                    }
                }
            }
        });
    }

    public void openSettings(View view) {
        startActivity(new Intent(MainActivity.this, MetronomePopUp.class));
    }

    public void playMetronome(final Activity activity) {
        timeBeat = (int)(60000/metronome.getBpm());//Calcular cuanto tiempo dura un beat
        cdMetronome = new PreciseCountdown(timeBeat*metronome.getBeats(), timeBeat*metronome.getBeats(), 0) {
            @Override
            public void onTick(long timeLeft) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (remove) removeTrack(trackToRemove);
                        if (soundMetronome) {
                            if (mThread == null && (metronome.getMetronomeSound() || metronome.getPrevious())) {
                                copyMetronome = metronome.copyMetronome();
                                mThread = new mThread();
                                mThread.start();
                            }
                            if (compassCont == 0) {
                                tracks[posRecording].recBtn.setTextOff("1");
                            }
                            if (compassCont == 1) {
                                isRecording = true;
                                isPlaying = true;
                                metronome.setIsPLaying(true);
                                if (mThread != null && metronome.getPrevious() &&
                                        !metronome.getMetronomeSound()) {
                                    stopMetronome();
                                    soundMetronome = true;
                                }
                                startRec(activity, posRecording);
                                tracks[posRecording].startSquare(activity);
                            }
                            /*if (compassCont > tracks[posRecording].getCompass() && isRecording){
                                compassCont = 1;
                            }*/
                        }
                        syncTracks();
                    }
                });
            }
            @Override
            public void onFinished() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        compassCont++;
                        cdMetronome.restartParams(timeBeat*metronome.getBeats(),
                                timeBeat*metronome.getBeats(), 0);
                        cdMetronome.start();
                    }
                });
            }
        };
        cdMetronome.start();
        /*timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (beatCont > metronome.getBeats()){
                            beatCont = 1;
                            compassCont++;
                        }
                        if (beatCont == 1) {
                            /*if (preparedTrack) {
                                preparedTrack = false;
                                playTrack(trackToPlay);
                            }*/
                            /*if (soundMetronome) {
                                if (mThread == null) {
                                    copyMetronome = metronome.copyMetronome();
                                    mThread = new mThread();
                                    mThread.start();
                                }
                                if (compassCont == 1) {
                                    isRecording = true;
                                    isPlaying = true;
                                    startRec(activity, posRecording);
                                }
                                if (compassCont > tracks[posRecording].getCompass() && isRecording){
                                    //stopRec(posRecording);
                                    compassCont = 1;
                                }
                            }
                            syncTracks();
                        }
                        beatCont++;
                    }
                });
            }
        },0,timeBeat);*/
    }

    /*public void playMetronome() {
        timeBeat = 60000/((int) metronome.getBpm());//Calcular cuanto tiempo dura un beat
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //soundBeat(beatCont);
                        if (beatCont > 1 &&
                                beatCont <= metronome.getBeats()) { //Si el beat actual es menor que el max de un compás
                            beatCont++;
                            if (beatCont > metronome.getBeats()) {
                                beatCont = 1;
                                compassCont++;
                            }
                        } else {
                            if (compassCont > (tracks[posRecording].getCompass())) {
                                if (isRecording){
                                    stopMetronome();
                                    stopRec(posRecording);
                                    //playTrack(posRecording);
                                }
                                compassCont = 1;
                                beatCont = 1;
                                //timer.cancel();
                            } else {
                                if(soundMetronome) {
                                    if (mThread == null) {
                                        copyMetronome = metronome.copyMetronome();
                                        mThread = new mThread();
                                        mThread.start();
                                    }
                                    if (compassCont == 1) {
                                        isRecording = true;
                                        //record(posRecording);
                                        startRec(posRecording);
                                    }
                                }
                                if (preparedTrack){
                                    preparedTrack = false;
                                    playTrack(trackToPlay);
                                }
                                beatCont = 2;
                            }
                        }
                    }
                });
            }
        }, 0, timeBeat);
    }*/

    public void stopMetronome (){
        soundMetronome = false;
        copyMetronome.stop();
        copyMetronome = null;
        mThread.interrupt();
        mThread = null;
        copyMetronome = metronome.copyMetronome();
    }

    public void stopSquare(int pos) {
        timah.cancel();
        timah = null;
        sThread = new sThread();
        sThread.start();
    }

    public void initMBar (){
        mBar.setMaxCont(metronome.getBeats()*(int)(60000/metronome.getBpm())/5);
        mBar.setPaintWidth(mBar.getWidth()/mBar.getMaxCont());
        mBarThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mBar.draw();
            }
        });
        mBarThread.start();
    }

    public void startMBar (final Activity activity){
        mBar.setMaxCont((int)Math.floor(metronome.getBeats()*(int)(60000/metronome.getBpm())/ 5));
        mBar.setPaintWidth((float)mBar.getWidth() / mBar.getMaxCont());
        mBarTimer = new PreciseCountdown(metronome.getBeats()*(int)(60000/metronome.getBpm()),
                5, 0) {
            @Override
            public void onTick(long timeLeft) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBar.draw();
                    }
                });
            }
            @Override
            public void onFinished() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBar.cleanCanvas();
                        /*mBarTimer.restartParams(metronome.getBeats()*(int)(60000/metronome.getBpm()),
                        5, 0);
                        mBarTimer.start();*/
                    }
                });
            }
        };
        //mBarTimer.start();
        /*mBarTimer = new Timer();
        mBarTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mBar.getCont() > mBar.getMaxCont()) { //Si la línea se dibujaría fuera de los límites del cuadrado
                            mBar.cleanCanvas(); //Reiniciar el progreso del cuadrado
                            mBar.draw(); //Dibujar la primera línea
                        } else mBar.draw(); //Dibujar una línea
                    }
                });
            }
        }, 0, 5);*/
    }

    public void stopMBar() {
        //mBarTimer.cancel();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBarTimer.destroy();
                mBarTimer = null;
                mBar.cleanCanvas();
            }
        });
        /*mBarThread = new mBarThread();
        mBarThread.start();*/
    }

    public void test1 (){
    }

    private class mThread extends Thread{
        @Override
        public void run(){
            copyMetronome.play();
        }
    }

    public class sThread extends Thread{
        @Override
        public void run() {
            tracks[1].square.cleanCanvas();
            sThread.interrupt();
            sThread = null;
        }
    }

    public class mBarThread extends Thread{
        @Override
        public void run() {
            mBar.cleanCanvas();
            mBarThread.interrupt();
            mBarThread = null;
        }
    }
}
