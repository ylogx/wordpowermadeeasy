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
import android.support.annotation.ColorInt;
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
    public static final String PREF_WELCOME_SCREEN_SHOWN = "welcomeScreenShown";
    public static final String PREF_TEXT_COLOR = "textColor";
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int MILLIS_DELAY_IN_SHOWING_MEANING = 1000;
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
        changeColorAndSaveInPrefs(prefs.getInt(PREF_TEXT_COLOR, colorGreen));

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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_color_blue) {
            changeColorAndSaveInPrefs(colorBlue);
        } else if (id == R.id.action_color_green) {
            changeColorAndSaveInPrefs(colorGreen);
        } else if (id == R.id.action_color_orange) {
            changeColorAndSaveInPrefs(colorOrange);
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

    @OnClick({
            R.id.button_blue,
            R.id.button_red,
            R.id.button_green,
            R.id.button_orange,
            R.id.button_purple,
    }) void changeColor(View v) {
        int id = v.getId();
        int color = wordTv.getCurrentTextColor();
        if (id == R.id.button_blue) {
            color = colorBlue;
        } else if (id == R.id.button_green) {
            color = colorGreen;
        } else if (id == R.id.button_orange) {
            color = colorOrange;
        } else if (id == R.id.button_red) {
            color = colorRed;
        } else if (id == R.id.button_purple) {
            color = colorPurple;
        }
        changeColorAndSaveInPrefs(color);
    }

    private void changeColorAndSaveInPrefs(@ColorInt int color) {
        wordTv.setTextColor(color);
        meaningTv.setTextColor(color);
        //Store in preferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_TEXT_COLOR, color);
        editor.commit(); // Very important to save the preference
    }

    private static class TextChangeRunnable implements Runnable {
        private TextView textView;
        private String text;

        public TextChangeRunnable(TextView textView, String text) {
            this.textView = textView;
            this.text = text;
        }

        @Override public void run() {
            if (textView != null) {
                textView.setText(text);
            }
        }
    }
}
// vim: set ts=4 sw=4 tw=0 et :
