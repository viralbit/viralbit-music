package tech.viralbit.music;

import com.getcapacitor.BridgeActivity;
import android.os.Bundle;

import tech.viralbit.music.MediaStorePlugin;

public class MainActivity extends BridgeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerPlugin(MediaStorePlugin.class);
    }
}
