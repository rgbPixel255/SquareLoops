package com.andoid.rgbPixel.looper;

public class Metronome {

    //Hay que hacer global esta clase
    private static Metronome instance;

    private double bpm;
    private int beats;
    private int noteValue;
    private int silence;
    private boolean previous, metronomeSound, isPlaying;

    private double beatSound;
    private double sound;
    private double[] tock = new double[8000];
    public int tick = 1000; // samples of tick

    public boolean play = true;

    public MetronomeSound mSound = new MetronomeSound(8000);

    public Metronome() {
        mSound.createPlayer();
        bpm = 90;
        beats = 4;
        previous = true;
        metronomeSound = true;
        isPlaying = false;
    }

    public void calcSilence() {
        silence = (int)(((60/bpm)*8000)-tick);
    }

    public void play() {
        play = true;
        calcSilence();
        double[] tock =
                mSound.getSineWave(this.tick, 8000, beatSound);
        double[] tick =
                mSound.getSineWave(this.tick, 8000, sound);
        double silence = 0;
        double[] sound = new double[8000];
        int t = 0,s = 0,b = 0;
        do {
            for(int i=0;i<sound.length&&play;i++) {
                if(t<this.tick) {
                    if(b == 0)
                        sound[i] = tick[t];
                    else
                        sound[i] = tock[t];
                    t++;
                } else {
                    sound[i] = silence;
                    s++;
                    if(s >= this.silence) {
                        t = 0;
                        s = 0;
                        b++;
                        if(b > (this.beats-1))
                            b = 0;
                    }
                }
            }
            mSound.writeSound(sound);
        } while(play);
    }

    /*public void createMSound(){
        if (mSound == null) mSound = new MetronomeSound(8000);
        mSound.createPlayer();
    }*/

    public void stop() {
        play = false;
        mSound.destroyAudioTrack();
    }

    public void setBpm(double numBpm) {
        this.bpm = numBpm;
    }
    public double getBpm() {
        return this.bpm;
    }

    public void setBeats(int numBeats) {
        this.beats = numBeats;
    }
    public int getBeats() {
        return this.beats;
    }

    public void setNoteValue(int noteValue) {
        this.noteValue = noteValue;
    }
    public int getNoteValue() {
        return noteValue;
    }

    public void setBeatSound(double beatSound) {
        this.beatSound = beatSound;
    }
    public double getBeatSound() {
        return beatSound;
    }

    public void setSound(double sound) {
        this.sound = sound;
    }
    public double getSound() {
        return sound;
    }

    public void setPrevious (boolean p) { this.previous = p; }
    public boolean getPrevious() { return this.previous; }

    public void setMetronomeSound (boolean m) { this.metronomeSound = m; }
    public boolean getMetronomeSound() { return this.metronomeSound; }

    public void setIsPLaying (boolean i) { this.isPlaying = i; }
    public boolean getIsPlaying() { return this.isPlaying; }

    //copy maker
    public Metronome copyMetronome(){
        Metronome mCopy;
        mCopy = new Metronome();
        mCopy.setSound(this.getSound());
        mCopy.setBeatSound(this.getBeatSound());
        mCopy.setBpm(this.getBpm());
        mCopy.setBeats(this.getBeats());
        return mCopy;
    }

    public Boolean playRes(){
        this.play();
        return Boolean.TRUE;
    }

    public static synchronized Metronome getInstance(){
        if(instance==null){
            instance=new Metronome();
        }
        return instance;
    }
}
