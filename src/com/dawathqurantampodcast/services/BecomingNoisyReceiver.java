

package com.dawathqurantampodcast.services;

import static com.dawathqurantampodcast.services.PlayEpisodeService.ACTION_PAUSE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * Our audio becoming noisy receiver. Simply send a pause intent to the service.
 */
public class BecomingNoisyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Only react if this actually is a become noisy event
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction()))
            try {
                context.startService(new Intent(ACTION_PAUSE));
            } catch (SecurityException se) {
                // This might happen if called from the outside since our
                // service is not exported, just do nothing.
            }
    }
}
