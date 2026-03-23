import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;

/**
 * Resets the local MySQL database used by this project by executing schema.sql + data.sql.
 *
 * Usage (PowerShell):
 *   # Find mysql-connector-j jar in ~/.m2 then:
 *   javac -cp "<path-to-mysql-connector-j.jar>" tools/DbReset.java
 *   java  -cp "<path-to-mysql-connector-j.jar>;tools" DbReset
 */
public class DbReset {
    private static final String DB_NAME = "Web_Truyen";

    public static void main(String[] args) throws Exception {
        String user = envOrDefault("DB_USER", "root");
        String pass = envOrDefault("DB_PASS", "root");
        String host = envOrDefault("DB_HOST", "localhost");
        String port = envOrDefault("DB_PORT", "3306");
        String tz = envOrDefault("DB_TZ", "Asia/Ho_Chi_Minh");

        String rootUrl = "jdbc:mysql://" + host + ":" + port + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=" + tz;
        String dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=" + tz;

        System.out.println("Resetting database: " + DB_NAME + " on " + host + ":" + port);

        try (Connection conn = DriverManager.getConnection(rootUrl, user, pass);
             Statement st = conn.createStatement()) {
            st.execute("DROP DATABASE IF EXISTS " + DB_NAME);
            st.execute("CREATE DATABASE " + DB_NAME + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }

        runSqlFile(dbUrl, user, pass, Path.of("src/main/resources/schema.sql"));
        runSqlFile(dbUrl, user, pass, Path.of("src/main/resources/data.sql"));

        System.out.println("Done.");
    }

    private static void runSqlFile(String dbUrl, String user, String pass, Path path) throws IOException, SQLException {
        System.out.println("Executing: " + path);
        String content = Files.readString(path, StandardCharsets.UTF_8);
        List<String> statements = splitSqlStatements(content);
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass)) {
            conn.setAutoCommit(true);
            try (Statement st = conn.createStatement()) {
                int i = 0;
                for (String sql : statements) {
                    i++;
                    String s = sql.trim();
                    if (s.isEmpty()) continue;
                    try {
                        st.execute(s);
                    } catch (SQLSyntaxErrorException ex) {
                        // Allow idempotent schema.sql patch statements (e.g. duplicate column on rerun).
                        String msg = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
                        if (msg.contains("duplicate column") || msg.contains("already exists")) {
                            continue;
                        }
                        throw ex;
                    }
                }
                System.out.println("  statements: " + i);
            }
        }
    }

    private static List<String> splitSqlStatements(String sqlFile) {
        // Very small parser good enough for our schema/data files (no custom delimiters).
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inSingle = false;
        boolean inDouble = false;

        for (int idx = 0; idx < sqlFile.length(); idx++) {
            char c = sqlFile.charAt(idx);
            char next = (idx + 1) < sqlFile.length() ? sqlFile.charAt(idx + 1) : '\0';

            // Strip "-- ..." comments when not in quotes.
            if (!inSingle && !inDouble && c == '-' && next == '-') {
                // Skip until end of line.
                while (idx < sqlFile.length() && sqlFile.charAt(idx) != '\n') idx++;
                continue;
            }

            if (c == '\'' && !inDouble) {
                inSingle = !inSingle;
                cur.append(c);
                continue;
            }
            if (c == '"' && !inSingle) {
                inDouble = !inDouble;
                cur.append(c);
                continue;
            }

            if (!inSingle && !inDouble && c == ';') {
                out.add(cur.toString());
                cur.setLength(0);
                continue;
            }

            cur.append(c);
        }

        if (!cur.isEmpty()) {
            out.add(cur.toString());
        }
        return out;
    }

    private static String envOrDefault(String key, String def) {
        String v = System.getenv(key);
        return v == null || v.isBlank() ? def : v;
    }
}
