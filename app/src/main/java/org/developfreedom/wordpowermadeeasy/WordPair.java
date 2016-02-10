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

public class WordPair {
    //private variables
    private String word;
    private String meaning;
    private int index;
    private int score;

    public WordPair(String word, String meaning) {
        setWord(word); //XXX need new?
        setMeaning(meaning);
        setIndex(-1);  //index only used for retrieving,
        setScore(0);
    }

    // getter & setter Word
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
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
