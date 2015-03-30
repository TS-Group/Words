/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ts.games.words.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Lasha
 */
public class GameController {
    
    private static final Logger logger = Logger.getLogger(GameController.class.getName());
    private static GameController current;
    private final Random random = new Random();
    
    public static GameController getCurrentInstance() {
        if (current == null)
            current = new GameController();
        return current;
    }
    
    private GameController() {
        try {
            categories = new HashMap<>();
            loadCategories();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Unable To load categories", ex);
        }
    }
    
    public Board startGame(String userId, int categoryId, int cellCount, int wordCount) throws Exception {
        logger.info(String.format("Starting Game: UserId - %s; Category - %d", userId, categoryId));
        if (!categories.containsKey(categoryId)) {
            logger.info(String.format("Category not found (%d)", categoryId));
            return null;
        }
            
        Category category = categories.get(categoryId);
        if (category.getWords() == null || category.getWords().size() < wordCount) {
            logger.info(String.format("Words not found (%d)", category.getWords().size()));
            return null;
        }
        
        logger.info(String.format("Searching Words : %d", wordCount));
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
        logger.info("Creating Game");
        int gameId = createGame(userId, categoryId);
        logger.info(String.format("Game Created ID: %d", gameId));
        
        List<String> words = wordsList.stream()
                .map(word -> word.getWord()).collect(Collectors.toList());
        return new Board(cellCount, words, gameId);
    }
    
    private void loadCategories() throws Exception {
        logger.info("Start Loading Categories");
        String sqlCommand = 
                "select c.*, w.WordId, w.Word " +
                "from word w inner join " +
                "     category c on w.CategoryId = c.CategoryId";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = DataBaseHelper.getConnection();
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
                    currentCategory = category;
                    categories.put(categoryId, category);
                }
                
                if (currentCategory.getWords() != null)
                    currentCategory.getWords().add(new Word(wordName));
            }
            logger.info(String.format("Loaded Categories %d", categories.size()));
        } finally {
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
            if (connection != null)
                connection.close();
        }
    }

    
    private int createGame(String userId, int categoryId) throws Exception {
        String sqlCommand = 
                "INSERT INTO [Game]\n" +
                "           ([PlayerId]\n" +
                "           ,[CategoryId]" +
                "           ,[StartTime])\n" +
                "     VALUES\n" +
                "           (?\n" +
                "           ,?\n" +
                "           ,?\n);";
        try (
            Connection connection = DataBaseHelper.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlCommand, Statement.RETURN_GENERATED_KEYS);
        ) {
            statement.setString(1, userId);
            statement.setInt(2, categoryId);
            statement.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();
            try (
                ResultSet generatedKeys = statement.getGeneratedKeys()
            ) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
                else {
                    throw new SQLException("Creating game failed, no ID obtained.");
                }
            }
        }
    }
    
    public void endGame(int gameId) throws Exception {
        String sqlCommand = 
                "UPDATE [Game]\n" +
                "   SET [EndTime] = ?\n" +
                " WHERE GameId = ?";
        try (
            Connection connection = DataBaseHelper.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlCommand);
        ) {
            statement.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
            statement.setInt(2, gameId);
            statement.executeUpdate();
        }
    }
    
    
    private Map<Integer, Category> categories;

    public Map<Integer, Category> getCategories() {
        return categories;
    }

    public Random getRandom() {
        return random;
    }

}
