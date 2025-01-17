package GUI.service;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService {
    static String url = "jdbc:sqlite:monthly_timesheet.db";
    public static String userName;
    public static DateTimeFormatter formatter;
    public static DateTimeFormatter formatterForDB;

    /**
     * Konstruktor
     */
    public DatabaseService() {
        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        formatterForDB = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    /**
     * Vytvara tabulku na ukladanie dat potrebne do mesacneho vykazu s pozadovanymi stlpcami
     * a presnym nazvom na zaklade datumu a priezvyska
     */
    public void createTable(String date) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + getTableNameManager(date) + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date DATE, " +
                "hours REAL, " +
                "activity TEXT, " +
                "vacation BOOLEAN, " +
                "holiday BOOLEAN, " +
                "doctor BOOLEAN," + 
                "pn BOOLEAN," +
                "week BOOLEAN" +
                ")";

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Metoda na spustenie prikazu 'INSERT INTO', ktora prida data do tabulky pre mesacny vykaz zo vstupu
     */
    public void insertData(String date, double hours, String activity, boolean vacation,
                           boolean holiday, boolean doctor, boolean pn, boolean week) {
        String insertSQL = "INSERT INTO " + getTableNameManager(date) + " (" +
                "date, hours, activity, vacation, holiday, doctor, pn, week" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        date = LocalDate.parse(date, formatter).format(formatterForDB);

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, date);
            preparedStatement.setDouble(2, hours);
            preparedStatement.setString(3, activity);
            preparedStatement.setBoolean(4, vacation);
            preparedStatement.setBoolean(5, holiday);
            preparedStatement.setBoolean(6, doctor);
            preparedStatement.setBoolean(7, pn);
            preparedStatement.setBoolean(8, week);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Metoda na spustenie prikazy 'DELETE', ktora vymaze z databazy pre mesacny vykaz pozadovany riadok
     */
    public static void deleteData(int ID, String date) {
        String deleteSQL = "DELETE FROM " + getTableNameManager(date) + " WHERE id = ?";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, ID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Metoda zistuje max ID v stlpci ID v tabulke pre mesacny vykaz aby sa pri pridavani pokracovalo od tohoto cisla.
     * Kedze stlpec ID je PRIMARY KEY AUTOINCREMENT
     */
    public int maxIDFromDB(String date) {
        String query = "SELECT MAX(ID) AS max_id FROM " + getTableNameManager(date);
        int maxID = 0;

        try (Connection connection = connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                maxID = rs.getInt("max_id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return maxID;
    }

    /**
     * Metoda sluzi na pripojenie k databazy pre mesacny vykaz na zaklade presnej URL / url - adreska k DB
     */
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /**
     * Vytvara nazov tabulky pre mesacny vykaz podla pozadovaneho datumu a priezviska.
     */
    public static TableNameManager getTableNameManager(String date) {
        return new TableNameManager(LocalDate.parse(date, formatter), userName);
    }

    /**
     * Metoda nacitava priezvisko uzivatela z tabulky user aby sa mohol vytvorit presny nazov pre mesacny vykaz
     */
    public String getUserData(String column) {
        String selectSQL = "SELECT " + column + " FROM user LIMIT 1";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            if (resultSet.next()) {
                return resultSet.getString(column);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Vytvara tabulku na uchovanie priezviska uzivatela.
     */
    public void createTableForLastName() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS user (" +
                "lastName TEXT," +
                "vacation TEXT," +
                "salary TEXT)";

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Uklada priezvisko, pocet dni dovolenky a plat uzivatela do tabulky user.
     */
    public void insertDataToUserTable(String userLastName, String vacation, String salary) {
        String insertSQL = "INSERT INTO user (lastName, vacation, salary) VALUES (?, ?, ?)";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, userLastName);
            preparedStatement.setString(2, vacation);
            preparedStatement.setString(3, salary);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Zmeni hodnotu value v stlpci column.
     */
    public void updateDataToUserTable(String column, String value) {
        String sql = "UPDATE user SET " + column + " = ?";

        try (Connection connection = connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, value);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Nacita zoznam vsetkych tabuliek v DB.
     */
    public List<String> listAllTables() {
        String query = "SELECT name FROM sqlite_master " +
                        "WHERE type='table' " +
                        "AND name NOT LIKE 'sqlite_%' " +
                        "AND name NOT LIKE 'user' ";
        List<String> listOfTableNames = new ArrayList<>();

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String tableName = resultSet.getString("name");
                if (!isTableEmpty(tableName)){
                    listOfTableNames.add(tableName);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return listOfTableNames;
    }

    /**
     * Metoda kontroluje ci je konkretna tabulka prazdna. Ak je prazdna vrati true. Ak nie je prazdna vrati false.
     */
    private boolean isTableEmpty(String tableName) {
        String countQuery = "SELECT COUNT(*) FROM " + tableName;

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(countQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    /**
     * Metoda nacitava data z konkretnej tabulky na zaklade nazvu tabulky a zapisuje do List-u.
     */
    public List<Map<String, Object>> getAllRows(String tableName) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String query = "SELECT * FROM " + tableName + " ORDER BY date ASC";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                ResultSetMetaData metaData = resultSet.getMetaData();

                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);

                    if ("date".equalsIgnoreCase(columnName)) {
                        row.put(columnName, LocalDate.parse(value.toString(), formatterForDB).format(formatter));
                    } else {
                        row.put(columnName, value);
                    }
                }
                rows.add(row);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return rows;
    }

    /**
     * Metoda kontroluje ci existuje konkretna tabulka. Ak ano vrati true. Ak neexistuje vrati false.
     */
    public boolean doesTableExist(String tableName) {
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name = ?";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, tableName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    // mMetoda je uz nepotrebna. Moznost vratit funkciu  na vymazanie konkretnej tabulky z DB.
    /**
     * Vymaze konkretnu tabulku z databazy.
     */
    private void deleteTable(String tableName) {
        String dropTableSQL = "DROP TABLE IF EXISTS " + tableName;

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute(dropTableSQL);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
