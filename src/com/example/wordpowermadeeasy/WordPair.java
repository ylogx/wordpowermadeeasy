package com.example.wordpowermadeeasy;

public class WordPair {
	private String word, meaning;

	public WordPair() {
		word = new String();
		meaning = new String();
	}

	public WordPair(String word,String meaning) {
		word = new String(word);
		meaning = new String(meaning);
	}

	public String word(){
		return word;
	}
	
	public String meaning(){
		return meaning;
	}

}
