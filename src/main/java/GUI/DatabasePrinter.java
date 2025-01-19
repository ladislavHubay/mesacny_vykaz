package GUI;

import java.sql.*;

public class DatabasePrinter {
    private static final String DATABASE_URL = "jdbc:sqlite:monthly_timesheet.db";

    public static void printTableContents(String tableName) {
        String query = "SELECT * FROM " + tableName;

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("Obsah tabuľky: " + tableName);

            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(resultSet.getMetaData().getColumnName(i) + "\t\t\t\t");
            }
            System.out.println();

            String medzery = "";

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    if(resultSet.getString(i) != null){
                        medzery = medzery(resultSet.getString(i));
                    } else {
                        medzery = "\t\t\t\t\t";
                    }

                    System.out.print(resultSet.getString(i) + " " + medzery);
                }
                System.out.println();
            }

        } catch (SQLException e) {
            System.err.println("Error reading database: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        //printTableContents("meno_vykaz_1_2025"); // Názov tabuľky, ktorú chceš vypísať
        printTableContents("user"); // Názov tabuľky, ktorú chceš vypísať
    }

    public static String medzery(String text){
        String medzera = "";
        for (int i = 0; i < (20 - text.length()); i++) {
            medzera += " ";
        }

        return medzera;
    }
}
