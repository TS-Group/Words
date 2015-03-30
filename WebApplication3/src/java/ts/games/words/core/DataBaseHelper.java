/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ts.games.words.core;

import java.sql.Connection;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author lasha
 */
public class DataBaseHelper {
    
    public static Connection getConnection() throws Exception {
        InitialContext context = new InitialContext();
        DataSource dataSource = (DataSource)context.lookup("jdbc/SQLServer");
        return dataSource.getConnection();
    }
    
}
