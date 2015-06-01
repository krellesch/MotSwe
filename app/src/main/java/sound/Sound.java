package sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.kristianschjonning.motswe.R;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by kristianschjonning on 01/06/15.
 */
public class Sound {
    public static final int S1 = R.raw.lillejavla;
    public static final int S2 = R.raw.anthem;
    public static final int S3 = R.raw.beeropening;
    public static final int S4 = R.raw.neifaen;
    public static final int S5 = R.raw.ohdinidiot;
    public static final int S6 = R.raw.olsen;

    private static SoundPool soundPool;
    private static HashMap soundPoolMap;
    private static MediaPlayer mySound;


    public static void initSounds(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap(5);
        soundPoolMap.put( S1, soundPool.load(context, R.raw.lillejavla, 1) );
        soundPoolMap.put( S2, soundPool.load(context, R.raw.anthem, 2) );
        soundPoolMap.put( S4, soundPool.load(context, R.raw.neifaen, 3) );
        soundPoolMap.put( S5, soundPool.load(context, R.raw.ohdinidiot, 4) );
        soundPoolMap.put( S6, soundPool.load(context, R.raw.olsen, 5));
        soundPoolMap.put( S3, soundPool.load(context, R.raw.beeropening, 6) );
    }

    public static void playSound(Context context) {
        final Random rstart = new Random();
        int counternr = rstart.nextInt(4) + 1;
        if (soundPool == null || soundPoolMap == null) {

            initSounds(context);
        }
        float volume = 1.0f;
        soundPool.play(counternr, volume, volume, 1, 0, 1f);
    }

    public static void playSound(Context context,int soundID) {
        if (soundPool == null || soundPoolMap == null) {

            initSounds(context);
        }
        float volume = 1.0f;
        soundPool.play(soundID, volume, volume, 1, 0, 1f);
    }
    public static void playMusic(Context context)
    {
        mySound = MediaPlayer.create(context,R.raw.opening);
        mySound.setVolume(0.2f,0.2f);
        mySound.start();
        mySound.setLooping(true);
    }

    public static void resumeSound(Context context)
    {
        soundPool.autoResume();
        mySound.start();
    }

    public static void pauseSound(Context context)
    {
            soundPool.autoPause();
            mySound.pause();
            mySound.release();
    }
}
