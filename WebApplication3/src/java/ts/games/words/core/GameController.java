/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ts.games.words.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author Lasha
 */
public class GameController {
    
    private static GameController current;
    private final Random random = new Random();
    
    public static GameController getCurrentInstance() {
        if (current == null)
            current = new GameController();
        return current;
    }
    
    private GameController() {
        try {
            LoadCategories();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public Board startGame(String userId, int categoryId, int cellCount, int wordCount) {
        if (!categories.containsKey(categoryId))
            return null;
        Category category = categories.get(categoryId);
        if (category.getWords() == null || category.getWords().size() < wordCount)
            return null;
        
        List<Word> wordsList = new ArrayList<>();
        boolean[] usedWords = new boolean[category.getWords().size()];
        for (int index = 0; index < wordCount; index++) {
            int wordIndex;
            do {
                wordIndex = random.nextInt(category.getWords().size());
            } while (usedWords[wordIndex]);
            usedWords[wordIndex] = true;
            wordsList.add(category.getWords().get(wordIndex));
        }
        
        int gameId = createGame(userId, categoryId);
        
        List<String> words = wordsList.stream()
                .map(word -> word.getWord()).collect(Collectors.toList());
        return new Board(cellCount, words, gameId);
    }
    
    
    private void LoadCategories() throws Exception {
        InitialContext context = new InitialContext();
        DataSource dataSource = (DataSource)context.lookup("jdbc/SQLServer");
        Connection connection = dataSource.getConnection();
        
        String sqlCommand = 
                "select c.*, w.WordId w.Word\n" +
                "from word w inner join \n" +
                "     category c on w.CategoryId = c.CategoryId";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sqlCommand);
            resultSet = statement.executeQuery();
            int currentCategoryId = -1;
            Category currentCategory = null;
            while (resultSet.next()) {
                int categoryId = resultSet.getInt("CategoryId");
                String categoryName = resultSet.getString("CategoryName");
                //int wordId = resultSet.getInt("WordId");
                String wordName = resultSet.getString("Word");
                
                if (currentCategoryId != categoryId || currentCategory == null) {
                    Category category = new Category(categoryId, categoryName, new ArrayList<>());
                    currentCategoryId = categoryId;
                    categories.put(categoryId, category);
                }
                
                if (currentCategory != null && currentCategory.getWords() != null)
                    currentCategory.getWords().add(new Word(wordName));
            }
                    
        } finally {
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
            if (connection != null)
                connection.close();
        }
    }
    
    private int createGame(String userId, int categoryId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void endGame(String userId, int gameId) {
        //
    }
    
    
    private Map<Integer, Category> categories;

    public Map<Integer, Category> getCategories() {
        return categories;
    }

    public Random getRandom() {
        return random;
    }

}
