package connect;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private Connection connection;
    private final String url;
    private final String user;
    private final String password;
    private final String mydatabase;

    public DatabaseConnection(String url, String user, String password, String mydatabase) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.mydatabase = mydatabase;
    }

    public void connect() throws SQLException {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setUser(user);
        ds.setPassword(password);
        ds.setServerName(url);
        ds.setPortNumber(1433);
        ds.setDatabaseName("descarga");
        connection = ds.getConnection();

        if (connection != null) {
            System.out.println("Conexión a base de datos OK\n");
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void startKeepAliveThread() {
        Thread keepAliveThread = new Thread(() -> {
            while (true) {
                try {
                    getConnection().createStatement().execute("/* ping */ SELECT 1");
                    Thread.sleep(60000); // Sleep for 1 minute
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                    try {
                        disconnect();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        connect();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        keepAliveThread.start();
    }
}
