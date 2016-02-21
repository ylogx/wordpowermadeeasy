/**
 * Word Power Made Easy - A vocabulary building application
 * <p/>
 * Copyright (c) 2014 Shubham Chaudhary <me@shubhamchaudhary.in>
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
package org.developfreedom.wordpowermadeeasy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String COLOR_BLUE = "blue";
    public static final String COLOR_RED = "red";
    public static final String COLOR_GREEN = "green";
    public static final String COLOR_ORANGE = "orange";
    public static final String COLOR_PURPLE = "purple";
    private static final int MILLIS_DELAY_IN_SHOWING_MEANING = 1000;
    private static final String PREF_WELCOME_SCREEN_SHOWN = "welcomeScreenShown";
    private final String PREF_TEXT_COLOR = "textColor";
    @Bind(R.id.textview_word) TextView wordTv;
    @Bind(R.id.textview_meaning) TextView meaningTv;
    private AsyncTask meaningDelayedTask;
    /** Engines */
    private WordEngine wordEngine;
    private SharedPreferences prefs;
    private int colorBlue;
    private int colorGreen;
    private int colorOrange;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        wordEngine = new WordEngine(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        changeColor(prefs.getString(PREF_TEXT_COLOR, COLOR_GREEN));

        showWelcomeScreenIfNotShownYet();
    }

    private void showWelcomeScreenIfNotShownYet() {
        Boolean welcomeScreenShown = prefs.getBoolean(PREF_WELCOME_SCREEN_SHOWN, false);

        if (!welcomeScreenShown) {
            showWelcomeScreen();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_WELCOME_SCREEN_SHOWN, true);
            editor.commit();
        }
    }

    private void showWelcomeScreen() {
        String welcomeTitle = getResources().getString(R.string.welcomeTitle);
        String welcomeText = getResources().getString(R.string.welcomeText);
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_menu_help)
                .setTitle(welcomeTitle)
                .setMessage(welcomeText)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_color_blue) {
            changeColor(COLOR_BLUE);
        } else if (id == R.id.action_color_green) {
            changeColor(COLOR_GREEN);
        } else if (id == R.id.action_color_orange) {
            changeColor(COLOR_ORANGE);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * My Functions
     */

    public void searchWord(View v) {
        String searchQuery = wordTv.getText().toString();
        searchQuery = searchQuery.replace(' ', '+');
        Uri uri = Uri.parse("http://www.google.com/#q=define:" + searchQuery);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void nextRandom(View v) {
        if (meaningDelayedTask != null &&
                meaningDelayedTask.getStatus() == AsyncTask.Status.RUNNING)
            meaningDelayedTask.cancel(true);
        WordPair wordPair = nextRandom();
        wordTv.setText(wordPair.getWord());
        meaningTv.setText("");   //Empty, if user waits then show else move on
        String meaning = wordPair.getMeaning(); //should be passed to task probably

        //Run on thread with a delay of MILLIS_DELAY_IN_SHOWING_MEANING
        meaningDelayedTask = new LongRunningTask().execute(meaning);
    } //end nextRandom

    private WordPair nextRandom() {
        return wordEngine.getRandomWord();
    }

    private void changeColor(String color) {
        int wordTvCurrentTextColor = wordTv.getCurrentTextColor();
        int meaningTvCurrentTextColor = meaningTv.getCurrentTextColor();
        if (color.equals(COLOR_BLUE)) {
            colorBlue = getResources().getColor(R.color.holo_blue_light);
            wordTvCurrentTextColor = colorBlue;
            meaningTvCurrentTextColor = colorBlue;
        } else if (color.equals(COLOR_GREEN)) {
            colorGreen = getResources().getColor(R.color.holo_green_light);
            wordTvCurrentTextColor = colorGreen;
            meaningTvCurrentTextColor = colorGreen;
        } else if (color.equals(COLOR_ORANGE)) {
            colorOrange = getResources().getColor(R.color.holo_orange_light);
            wordTvCurrentTextColor = colorOrange;
            meaningTvCurrentTextColor = colorOrange;
        } else if (color.equals(COLOR_RED)) {
            wordTvCurrentTextColor = getResources().getColor(R.color.holo_red_light);
            meaningTvCurrentTextColor = getResources().getColor(R.color.holo_red_light);
        } else if (color.equals(COLOR_PURPLE) || color.equals("voilet")) {
            wordTvCurrentTextColor = meaningTvCurrentTextColor = getResources().getColor(R.color.holo_purple);
        }
        wordTv.setTextColor(wordTvCurrentTextColor);
        meaningTv.setTextColor(meaningTvCurrentTextColor);
        //Store in preferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_TEXT_COLOR, color);
        editor.commit(); // Very important to save the preference
    }

    public void colorRed(View v) {
        changeColor(COLOR_RED);
    }

    public void colorBlue(View v) {
        changeColor(COLOR_BLUE);
    }

    public void colorGreen(View v) {
        changeColor(COLOR_GREEN);
    }

    public void colorOrange(View v) {
        changeColor(COLOR_ORANGE);
    }

    public void colorPurple(View v) {
        changeColor(COLOR_PURPLE);
    }

    private class LongRunningTask extends AsyncTask<String, Void, Boolean> {
        String meaning;

        @Override
        protected Boolean doInBackground(String... incomingStrings) {
            meaning = incomingStrings[0]; //FIXME: Probably not best
            try {
                Thread.sleep(MILLIS_DELAY_IN_SHOWING_MEANING);
            } catch (InterruptedException e) {
                /* No need of stacktrace here. The exception is intentional. */
                //Log.i("Sleep","Skipping this meaning: "+meaning);
                //e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            meaningTv.setText(meaning);
        }
    }
}
// vim: set ts=4 sw=4 tw=0 et :
