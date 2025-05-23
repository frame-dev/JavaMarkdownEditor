package ch.framedev;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 24.05.2025 00:01
 */

import ch.framedev.javasqliteutils.SQLite;

@SuppressWarnings("unused")
public class SQLiteManager implements IDatabase {

    public SQLiteManager() {
        String path = Main.config.getString("database.sqlite.path", "path");
        String database = Main.config.getString("database.sqlite.database", "database");
        //noinspection InstantiationOfUtilityClass
        new SQLite(path, database);
    }
}
