package com.example.wordpowermadeeasy;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	///Class Variables
	TextView textview_word, textview_meaning;
	Button button_next;

	/* Engines */
	WordEngine wordEngine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		wordEngine = new WordEngine(this);

		textview_word = (TextView)findViewById(R.id.textview_word);
		textview_meaning = (TextView)findViewById(R.id.textview_meaning);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void nextRandom(View v) throws  XmlPullParserException, IOException {
		wordEngine.readXml();
		WordPair wordPair = nextRandom();
		textview_word.setText(wordPair.word());
		textview_meaning.setText(wordPair.meaning());
	}
	
	public WordPair nextRandom(){
		return wordEngine.getRandomWord();
	}

}
