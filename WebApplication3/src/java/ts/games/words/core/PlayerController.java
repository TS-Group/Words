/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ts.games.words.core;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

/**
 *
 * @author lasha
 */
public class PlayerController {
    
    private static final Logger logger = Logger.getLogger(PlayerController.class.getName());    
    private static PlayerController instance;
    
    public static PlayerController getCurrentInstance() {
        if (instance == null)
            instance = new PlayerController();
        return instance;
    }
    
    public int getPlayerBestTime(String userId) throws Exception {
        logger.info("Start Loading Categories");
        String sqlCommand = 
                "SELECT isnull(min(DATEDIFF(second, Starttime, EndTime)), -1) as BestTime\n" +
                "  FROM [Words].[dbo].[Game] \n" +
                "where [PlayerId] = ?  ";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = DataBaseHelper.getConnection();
        try {
            statement = connection.prepareStatement(sqlCommand);
            statement.setString(1, userId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return -1;
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
    
    public void storePlayerData(Player player) throws Exception {
        logger.info("Start Storing Player Data");
        String sqlCommand = 
                "EXECUTE [StorePlayer] ?, ? ,? ,? ,? ,? ,?";
        try (
            Connection connection = DataBaseHelper.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlCommand);
        ) {
            statement.setString(1, player.getPlayerId());
            statement.setString(2, player.getFirstName());
            statement.setString(3, player.getLastName());
            statement.setString(4, player.getName());
            statement.setString(5, player.getGender());
            statement.setString(6, player.getEmail());
            statement.setString(7, player.getVerified());
            statement.executeUpdate();
        }
    }
    
    
}
