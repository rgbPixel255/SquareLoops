package com.andoid.rgbPixel.looper;

import java.util.Random;

public class ColorManager {
    //Hay que hacer global esta clase
    private static ColorManager instance;

    private int[] colors = new int[15];
    private int auxColor;

    public ColorManager() {
        colors[1] = 0xFFD54603; //Naranja
        colors[2] = 0xFFAC44CD; //Violeta
        colors[3] = 0xFF0D9436; //Verde
        colors[4] = 0xFF940D0D;//Magenta
        colors[5] = 0xFFDA2B2B; //Rojo
        colors[6] = 0xFFECC02C; //Amarillo
        colors[7] = 0xFF022F7A; //Azul
        colors[8] = 0xFF048119; //Verde
    }

    public int getColor(int c) {
        return colors[c];
    }

    public void reorganize(int first, int last) {
        auxColor = colors[first];
        for (int i = first; i < last; i++) colors[i] = colors[i+1];
        colors[last] = auxColor;
    }

    public static synchronized ColorManager getInstance(){
        if(instance==null){
            instance=new ColorManager();
        }
        return instance;
    }
}
