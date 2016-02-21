package org.developfreedom.wordpowermadeeasy.word;

import android.content.Context;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

/**
 * @author Shubham Chaudhary on 22/02/16
 */
public class GsonWordEngine implements WordEngine {
    private List<WordPair> words;

    public GsonWordEngine(Context context) {
        try {
            InputStream is = context.getAssets().open("word_list.json");
            Container container = new Gson().fromJson(new InputStreamReader(is), Container.class);
            words = container.getAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public WordPair getRandomWord() {
        return (words == null) ? new WordPair() : words.get(new Random().nextInt(words.size()));
    }
}
