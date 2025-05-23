package ch.framedev;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 23.05.2025 23:59
 */

import ch.framedev.javamysqlutils.MySQLV2;

public class MySQLManager implements IDatabase {

    private MySQLV2 mySQLV2;

    public MySQLManager() {
        String host = Main.config.getString("database.mysql.host", "localhost");
        int port = Main.config.getInt("database.mysql.port", 3306);
        String database = Main.config.getString("database.mysql.database", "database");
        String username = Main.config.getString("database.mysql.username", "username");
        String password = Main.config.getString("database.mysql.password", "password");
        mySQLV2 = new MySQLV2.Builder()
                .host(host)
                .port(port)
                .user(username)
                .password(password)
                .database(database)
                .build();
    }
}
