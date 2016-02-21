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

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class WordPair {
    //private variables
    @SerializedName("word") String word = "";
    @SerializedName("meaning") String meaning = "";
    private int index;
    private int score;

    public WordPair() {
    }

    public WordPair(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;
        this.index = -1;
        this.score = 0;
    }

    // getter & setter Word
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaningForDisplay() {
        return StringUtils.capitalize(meaning);
    }

    // getter & setter Meaning
    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    // getter & setter Index
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    // getter & setter Score
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
// vim: set ts=4 sw=4 tw=0 et :
