package com.ua.hower.house;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;


public class MessageSpoken implements OnInitListener {

	private Context context;
	private TextToSpeech tts;

	MessageSpoken(Context context) {
		this.context = context;

	}

	public void MessageForSpeak(String caption, String description) {


		final String spoken_text = "Item Name :"+caption +"\n\n\n"+description;

		
		tts = new TextToSpeech(context, new OnInitListener() {
			@Override
			public void onInit(int status) {
				// TODO Auto-generated method stub
				if (status == TextToSpeech.SUCCESS) {
					// isInitTTS = true;
					if (tts.isLanguageAvailable(Locale.US) >= 0) {
						tts.setLanguage(Locale.UK);
						tts.setPitch(1.2f);
						tts.setSpeechRate(0.9f);
						if (tts != null) {
							tts.speak(spoken_text, TextToSpeech.QUEUE_ADD, null);
							
							Toast.makeText(
									context,
									"Attention !!",
									Toast.LENGTH_LONG).show();
						}
					}
				} else {
					// Intent intn = new Intent(Engine.ACTION_INSTALL_TTS_DATA);
					// startActivity(intn);
					Toast.makeText(
							context,
							"Install Google Voice   !!",
							Toast.LENGTH_LONG).show();


									}
			}
		});
		
	

			}

	@Override
	public void onInit(int arg0) {
		

	}
	
	
	public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        
        
    }

}
