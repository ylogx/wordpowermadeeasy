package com.developfreedom.wordpowermadeeasy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.developfreedom.wordpowermadeeasy.util.SystemUiHider;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private final int DELAY_MEANING = 1000;   //Delay in showing meaning (millisec)
    private final String welcomeScreenShownPref = "welcomeScreenShown";
    //Class Variables
    private TextView textview_word;
    private TextView textview_meaning;
    private AsyncTask meaningDelayedTask;
    /* Engines */
    private WordEngine wordEngine;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_btton).setOnTouchListener(mDelayHideTouchListener);


        wordEngine = new WordEngine(this);
//      databaseEngine = new DatabaseEngine(this);
//      databaseEngine.populateDatabase(wordEngine.getMapFromXml());

        textview_word = (TextView) findViewById(R.id.textview_word);
        textview_meaning = (TextView) findViewById(R.id.textview_meaning);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // second argument is the default to use if the preference can't be found
        Boolean welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false);

        if (!welcomeScreenShown) {
            showWelcomeScreen();
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean(welcomeScreenShownPref, true);
            editor.commit(); // Very important to save the preference
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    /**
     * My Functions
     */

    //TODO: Make sure no need of throw now.
    public void nextRandom(View v) throws XmlPullParserException, IOException {
        if (meaningDelayedTask != null &&
                meaningDelayedTask.getStatus() == AsyncTask.Status.RUNNING)
            meaningDelayedTask.cancel(true);
        WordPair wordPair = nextRandom();
        textview_word.setText(wordPair.getWord());
        textview_meaning.setText("");   //Empty, if user waits then show else move on
        String meaning; //should be passed to task probably
        meaning = wordPair.getMeaning();

        //Run on thread with a delay of DELAY_MEANING
        meaningDelayedTask = new LongRunningTask().execute(meaning);
    } //end nextRandom

    public WordPair nextRandom() {
        return wordEngine.getRandomWord();
    }

    private void showWelcomeScreen() {
        // here you can launch another activity if you like
        // the code below will display a popup
        String welcomeTitle = getResources().getString(R.string.welcomeTitle);
        String welcomeText = getResources().getString(R.string.welcomeText);
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_menu_help).setTitle(welcomeTitle).setMessage(welcomeText).setPositiveButton(
                R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    private class LongRunningTask extends AsyncTask<String, Void, Boolean> {
        String meaning;

        @Override
        protected Boolean doInBackground(String... incomingStrings) {
            meaning = incomingStrings[0]; //FIXME: Probably not best
            try {
                Thread.sleep(DELAY_MEANING);
            } catch (InterruptedException e) {
                /* No need of stacktrace here. The exception is intentional. */
                //Log.i("Sleep","Skipping this meaning: "+meaning);
                //e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            textview_meaning.setText(meaning);
        }
    }
}
// vim: set ts=4 sw=4 tw=0 et :
