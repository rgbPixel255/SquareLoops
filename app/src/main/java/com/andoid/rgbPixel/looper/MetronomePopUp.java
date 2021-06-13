package com.andoid.rgbPixel.looper;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;

public class MetronomePopUp extends Activity{

    private EditText bpm, beats;
    private Button dBpm,iBpm, dBeats, iBeats;
    private Switch previous, mSound;
    private int aux; //Usado para sumar o restar cantidades
    private int auxBpm;
    private MetronomeBar mBar;
    Metronome metronome = Metronome.getInstance();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metronome_popup);

        DisplayMetrics dM = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dM);
        getWindow().setLayout((int) (dM.widthPixels*0.8),
                (int) (dM.heightPixels*0.6)); //Así se puede cambiar el tamaño del pop up con porcentajes
        //Creando un nuevo estilo en "style", los bordes el pop up son invisibles

        bpm = findViewById(R.id.bpmEditText);
        beats = findViewById(R.id.beatsEditText);
        dBpm = findViewById(R.id.bpmMinus);
        iBpm = findViewById(R.id.bpmPlus);
        dBeats = findViewById(R.id.beatsMinus);
        iBeats = findViewById(R.id.beatsPlus);
        mBar = findViewById(R.id.mBar);
        previous = findViewById(R.id.previousSw);
        mSound = findViewById(R.id.metronomeSw);

        auxBpm = (int)metronome.getBpm();
        bpm.setText(""+auxBpm);
        beats.setText(""+metronome.getBeats());
        previous.setChecked(metronome.getPrevious());
        mSound.setChecked(metronome.getMetronomeSound());

        if (metronome.getIsPlaying()) {
            dBpm.setEnabled(false);
            bpm.setEnabled(false);
            iBpm.setEnabled(false);
            dBeats.setEnabled(false);
            beats.setEnabled(false);
            iBeats.setEnabled(false);
        }
    }

    //public MetronomePopUp(MetronomeTest metronome){ mTest = metronome;}

    /*BPM
    ------------------------------------------------------------------------------*/
    public void reduceBpm (View v) { //Reduce los bpm en una unidad
        aux = Integer.parseInt(bpm.getText().toString());
        if (aux > 1) {
            aux--;
            bpm.setText("" + aux);
        }
    }
    public void increaseBpm (View v) { //Aumenta los bpm en una unidad
        aux = Integer.parseInt(bpm.getText().toString());
        if (aux < 999) {
            aux++;
            bpm.setText("" + aux);
        }
    }
    /*----------------------------------------------------------------------------*/
    /*BEATS
    ------------------------------------------------------------------------------*/
    public void reduceBeats (View v) { //Divide a la mitad el denominador del compás
        aux = Integer.parseInt(beats.getText().toString());
        if (aux > 1) {
            aux = aux - 1;
            beats.setText("" + aux);
        }
    }
    public void increaseBeats (View v) { //Duplica el denominador del compás
        aux = Integer.parseInt(beats.getText().toString());
        if (aux < 999) {
            aux = aux + 1;
            beats.setText("" + aux);
        }
    }
    /*----------------------------------------------------------------------------*/
    public void accept (View view) {
        metronome.setBpm(Integer.parseInt(bpm.getText().toString()));
        metronome.setBeats(Integer.parseInt(beats.getText().toString()));
        metronome.setPrevious(previous.isChecked());
        metronome.setMetronomeSound(mSound.isChecked());
        //mBar.drawBeats();

        finish();//Cierra el pop up
    }

    public void cancel (View view) { finish(); }
}
