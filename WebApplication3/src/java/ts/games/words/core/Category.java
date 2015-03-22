/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ts.games.words.core;

import java.util.List;

/**
 *
 * @author Lasha
 */
public class Category {

    public Category() {
    }

    public Category(int categoryId, String categoryName, List<Word> words) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.words = words;
    }
    
    private int categoryId;
    private String categoryName;
    private List<Word> words;

    /**
     * @return the categoryId
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName the categoryName to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return the words
     */
    public List<Word> getWords() {
        return words;
    }

    /**
     * @param words the words to set
     */
    public void setWords(List<Word> words) {
        this.words = words;
    }
    
    
            
    
    
}
