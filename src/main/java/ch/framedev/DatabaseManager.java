package ch.framedev;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 23.05.2025 23:58
 */

public class DatabaseManager {

    public enum DatabaseType {
        MYSQL,
        SQLITE
    }

    private DatabaseType databaseType;
    private IDatabase iDatabase;

    public DatabaseManager(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public IDatabase getIDatabase() {
        return iDatabase;
    }

    public boolean isDatabaseSupported() {
        return Main.config.getBoolean("useDatabase", false);
    }
}
