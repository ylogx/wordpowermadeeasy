package org.developfreedom.wordpowermadeeasy.word;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Shubham Chaudhary on 22/02/16
 */
public class Container {
    @SerializedName("all") Inner all;

    public List<WordPair> getAll() {
        return all.pairs;
    }

    public static class Inner {
        @SerializedName("pair") List<WordPair> pairs;
    }
}
