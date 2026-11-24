package com.parking.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

public class DBConnectionTest {

    public static void main(String[] args) {

        try (Connection connection = DBConnection.getConnection()) {

            DatabaseMetaData metadata = connection.getMetaData();

            System.out.println("==================================");
            System.out.println("  DATABASE CONNECTION SUCCESSFUL");
            System.out.println("==================================");
            System.out.println("Database: " + metadata.getDatabaseProductName());
            System.out.println("Version: " + metadata.getDatabaseProductVersion());
            System.out.println("Connection: " + connection.getCatalog());
            System.out.println("Status: Connected");

        } catch (Exception e) {
            System.out.println("DATABASE CONNECTION FAILED!");
            e.printStackTrace();
        }
    }
}