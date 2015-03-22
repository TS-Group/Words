/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ts.games.words.core;

/**
 *
 * @author Lasha
 */
public class Word {

    public Word() {
    }

    public Word(String word) {
        this.word = word;
    }
    
    private String word;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
    
}
