/**
 * Word Power Made Easy - A vocabulary building application
 * <p/>
 * Copyright (c) 2016 Shubham Chaudhary <me@shubhamchaudhary.in>
 * <p/>
 * This file is part of WordPowerMadeEasy.
 * <p/>
 * WordPowerMadeEasy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * WordPowerMadeEasy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with WordPowerMadeEasy.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.developfreedom.wordpowermadeeasy.word;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.developfreedom.wordpowermadeeasy.R;
import org.developfreedom.wordpowermadeeasy.storage.DatabaseOpenHelper;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.*;

public class XmlWordEngine implements WordEngine {
    public static final int DEFAULT_SCORE = 50;
    private final Context context;
    private final SQLiteOpenHelper myDatabaseHelper;
    private SQLiteDatabase database;
    private Map<String, String> wordMap;

    public XmlWordEngine(Context ctx) {
        this.context = ctx;
        this.myDatabaseHelper = new DatabaseOpenHelper(context);
        try {
            readXmlIntoMap(); //XXX do this once ever
            populateDatabase();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    private void populateDatabase() {
        try {
            database = myDatabaseHelper.getWritableDatabase();
            for (Map.Entry<String, String> wordMeaningEntry : wordMap.entrySet()) {
                try {
                    ContentValues values = new ContentValues();
                    values.put(DatabaseOpenHelper.KEY_WORD, wordMeaningEntry.getKey());
                    values.put(DatabaseOpenHelper.KEY_MEANING, wordMeaningEntry.getValue());
                    values.put(DatabaseOpenHelper.KEY_SCORE, DEFAULT_SCORE);

                    database.insert(DatabaseOpenHelper.TABLE_WORDLIST, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                try {
                    database.close();
                } catch (Exception ignored) {
                }
            }
        }
    }


    @Override public WordPair getRandomWord() {
        Random rand = new Random();
        List<String> keys = new ArrayList<>(wordMap.keySet());
        String randomKey = keys.get(rand.nextInt(keys.size()));
        String value = wordMap.get(randomKey);
        return new WordPair(randomKey, value);
    }

    private void readXmlIntoMap() throws XmlPullParserException, IOException {
        XmlResourceParser xrp = context.getResources().getXml(R.xml.word_list);
        xrp.next();

        wordMap = new HashMap<>();
        String word = null;
        String meaning = null;
        int eventType = xrp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if ((eventType == XmlPullParser.START_TAG)
                    && "pair".equalsIgnoreCase(xrp.getName())) {
                //NOTE: In our xml file meaning comes before word and that matters
                eventType = xrp.next();
                if ((eventType == XmlPullParser.START_TAG)
                        && "meaning".equalsIgnoreCase(xrp.getName())) {
                    eventType = xrp.next();
                    if (eventType == XmlPullParser.TEXT) {
                        //TODO: Handle situation when either word or meaning is missing
                        meaning = xrp.getText();
                    }
                }
                xrp.next();                 //End tag meaning
                eventType = xrp.next();     //Start tag word
                if ((eventType == XmlPullParser.START_TAG)
                        && "word".equalsIgnoreCase(xrp.getName())) {
                    eventType = xrp.next();
                    if (eventType == XmlPullParser.TEXT) {
                        word = xrp.getText();
                    }
                }
            } else if ((eventType == XmlPullParser.END_TAG)
                    && "pair".equalsIgnoreCase(xrp.getName())) {
                if ((word != null) && (meaning != null) && (word.length() != 0) && (meaning.length() != 0)) {
                    if (wordMap.get(word) == null) {
                        wordMap.put(word, meaning);
                    }
                }
            }
            eventType = xrp.next();
        }
    }
}
// vim: set ts=4 sw=4 tw=0 et :
