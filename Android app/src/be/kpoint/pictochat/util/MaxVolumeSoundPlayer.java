package be.kpoint.pictochat.util;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MaxVolumeSoundPlayer
{
	private WeakReference<Context> weakContext;

	private AudioManager audioManager;
	private MediaPlayer mediaPlayer;

	public MaxVolumeSoundPlayer(Context context) {
		this.weakContext = new WeakReference<Context>(context);
		this.audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	}

	public void playSound(int source) {
		final int stream = AudioManager.STREAM_MUSIC;

		Context context = this.weakContext.get();
		if (context != null) {
			final int originalVolume = this.audioManager.getStreamVolume(stream);

			this.audioManager.setStreamVolume(stream, this.audioManager.getStreamMaxVolume(stream), 0);

			this.mediaPlayer = MediaPlayer.create(context, source);
			if (this.mediaPlayer != null)
			{
				this.mediaPlayer.setAudioStreamType(stream);
				this.mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
	                @Override
	                public void onCompletion(MediaPlayer mp) {
	                	MaxVolumeSoundPlayer.this.audioManager.setStreamVolume(stream, originalVolume, 0);
	                	MaxVolumeSoundPlayer.this.mediaPlayer.release();
	                }
	            });
				this.mediaPlayer.start();
			}
		}
	}
}
