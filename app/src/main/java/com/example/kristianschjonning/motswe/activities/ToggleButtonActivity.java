package com.example.kristianschjonning.motswe.activities;


        import android.app.Activity;
        import android.content.Context;
        import android.media.AudioManager;
        import android.os.Bundle;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Toast;
        import android.widget.ToggleButton;

        import com.example.kristianschjonning.motswe.R;

        import sound.Sound;
/**
 * Kristian
 */
public class ToggleButtonActivity extends Activity {
    /** Called when the activity is first created. */

    ToggleButton tgbutton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        tgbutton = (ToggleButton) findViewById(R.id.toggleButton1);
        tgbutton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (tgbutton.isChecked()) {

                    tgbutton.setChecked(true);
                } else {

                    Sound.pauseSound(getApplicationContext());
                    tgbutton.setChecked(false);
                }
            }
        });
        /*
         * To set text on toggle button whenever it is having
            a state either ON or OFF
         */
        tgbutton.setTextOn("SOUND ON");
        tgbutton.setTextOff("SOUND OFF");
        tgbutton.setChecked(true);
    }
}