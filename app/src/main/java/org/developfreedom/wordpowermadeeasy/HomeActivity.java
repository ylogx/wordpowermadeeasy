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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.developfreedom.wordpowermadeeasy.word.WordEngine;
import org.developfreedom.wordpowermadeeasy.word.WordPair;

public class HomeActivity extends Activity {
    public static final String TAG = HomeActivity.class.getSimpleName();
    public static final String COLOR_BLUE = "blue";
    public static final String COLOR_RED = "red";
    public static final String COLOR_GREEN = "green";
    public static final String COLOR_ORANGE = "orange";
    public static final String COLOR_PURPLE = "purple";
    private static final int MILLIS_DELAY_IN_SHOWING_MEANING = 1000;
    private static final String PREF_WELCOME_SCREEN_SHOWN = "welcomeScreenShown";
    private static final String PREF_TEXT_COLOR = "textColor";
    @Bind(R.id.textview_word) TextView wordTv;
    @Bind(R.id.textview_meaning) TextView meaningTv;
    @BindColor(R.color.holo_blue_light) int colorBlue;
    @BindColor(R.color.holo_green_light) int colorGreen;
    @BindColor(R.color.holo_orange_light) int colorOrange;
    @BindColor(R.color.holo_red_light) int colorRed;
    @BindColor(R.color.holo_purple) int colorPurple;
    /** Engines */
    private WordEngine wordEngine;
    private SharedPreferences prefs;
    private Runnable meaningChangeRunnable;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_menu_help)
                .setTitle(R.string.welcomeTitle)
                .setMessage(R.string.welcomeText)
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

    @OnClick(R.id.button_search) void searchWord() {
        String searchQuery = wordTv.getText().toString();
        searchQuery = searchQuery.replace(' ', '+');
        Uri uri = Uri.parse("http://www.google.com/#q=define:" + searchQuery);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.textview_meaning) void nextRandom() {
        meaningTv.removeCallbacks(meaningChangeRunnable);

        WordPair wordPair = wordEngine.getRandomWord();
        wordTv.setText(wordPair.getWord());
        meaningTv.setText("");   //Empty, if user waits then show else move on

        //Run on thread with a delay of MILLIS_DELAY_IN_SHOWING_MEANING
        meaningChangeRunnable = new TextChangeRunnable(meaningTv, wordPair.getMeaning());
        meaningTv.postDelayed(meaningChangeRunnable, MILLIS_DELAY_IN_SHOWING_MEANING);
    }

    private void changeColor(String color) {
        int wordTvCurrentTextColor = wordTv.getCurrentTextColor();
        int meaningTvCurrentTextColor = meaningTv.getCurrentTextColor();
        if (color.equals(COLOR_BLUE)) {
            wordTvCurrentTextColor = colorBlue;
            meaningTvCurrentTextColor = colorBlue;
        } else if (color.equals(COLOR_GREEN)) {
            wordTvCurrentTextColor = colorGreen;
            meaningTvCurrentTextColor = colorGreen;
        } else if (color.equals(COLOR_ORANGE)) {
            wordTvCurrentTextColor = colorOrange;
            meaningTvCurrentTextColor = colorOrange;
        } else if (color.equals(COLOR_RED)) {
            wordTvCurrentTextColor = colorRed;
            meaningTvCurrentTextColor = colorRed;
        } else if (color.equals(COLOR_PURPLE) || color.equals("voilet")) {
            wordTvCurrentTextColor = meaningTvCurrentTextColor = colorPurple;
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

    private static class TextChangeRunnable implements Runnable {
        private TextView textView;
        private String text;

        public TextChangeRunnable(TextView textView, String text) {
            this.textView = textView;
            this.text = text;
        }

        @Override public void run() {
            textView.setText(text);
        }
    }
}
// vim: set ts=4 sw=4 tw=0 et :
