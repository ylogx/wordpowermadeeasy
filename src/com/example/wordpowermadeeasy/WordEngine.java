package com.example.wordpowermadeeasy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WordEngine {
	
	Context context;
	Map<String,String> word_map;
	static int count = 0;
    WordEngine(Context ctx){
        this.context = ctx;
        try {
			this.word_map = readXml();
			WordEngine.count += 1;
			System.out.println("Count ");
			System.out.println(WordEngine.count);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public WordPair getRandomWord(){
		Random rand = new Random();
		List<String> keys = new ArrayList<String>(word_map.keySet());
		String randomKey = keys.get( rand.nextInt(keys.size()) );
		String value = word_map.get(randomKey);
		WordPair new_word = new WordPair(randomKey,value);
		return new_word;
	}
	
	public Map<String,String> readXml() throws XmlPullParserException, IOException {
	    XmlResourceParser xrp = context.getResources().getXml(R.xml.word_list);

	    xrp.next();
	    int eventType = xrp.getEventType();
	    String word = null,meaning = null;
	    Map<String,String> word_pair_map = new HashMap<String,String>();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG 
					&& xrp.getName().equalsIgnoreCase("pair")){
				//NOTE: In our xml file meaning comes before word and that matters
				eventType = xrp.next();
				if (eventType == XmlPullParser.START_TAG 
						&& xrp.getName().equalsIgnoreCase("meaning")) {
					eventType = xrp.next();
					if(eventType == XmlPullParser.TEXT) {
						//TODO: Handle situation when either word or meaning is missing
						meaning = xrp.getText();
//						Log.i("M","Meaning: "+meaning);
					}
				}
				eventType = xrp.next();		//End tag meaning
				eventType = xrp.next();		//Start tag word		
				if (eventType == XmlPullParser.START_TAG 
						&& xrp.getName().equalsIgnoreCase("word")) {
					eventType = xrp.next();
					if(eventType == XmlPullParser.TEXT) {
						word = xrp.getText();
//						Log.i("W","Word: "+word);
					}
				}//end if elif
			}//end if START_TAG
			else if (eventType == XmlPullParser.END_TAG && xrp.getName().equalsIgnoreCase("pair")){
				if (word != null && meaning != null && word.length() != 0 && meaning.length() != 0){
					if (word_pair_map.get(word) == null){
//						Log.d("map",word+meaning);
						word_pair_map.put(word,meaning);
					}
				}
			}
			eventType = xrp.next();
		}//end while
		return word_pair_map;
	}//end readXml

}
