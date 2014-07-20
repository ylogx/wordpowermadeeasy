package com.example.wordpowermadeeasy;

public class WordPair {
	private String word, meaning;

	public WordPair() {
		word = new String();
		meaning = new String();
	}

	public WordPair(String word,String meaning) {
		this.word = word;	//XXX need new?
		this.meaning = meaning;
	}

	public String word(){
		return word;
	}
	
	public String meaning(){
		return meaning;
	}

}
