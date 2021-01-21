
package com.mobileaders.dto;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.log4j.Logger;

 
public class DButil {
	static Logger log = Logger.getLogger(DButil.class.getName());

    static Statement sta;
    static String driver = "com.mysql.jdbc.Driver";
    static String url = "jdbc:mysql://localhost:3306/";
    static String dbname = "NewsDB";
    static String dbuser = "root";
    static String dbpass = "";

    public static Statement getStatement() {
        try {

            Class.forName(driver);
            Connection con = DriverManager.getConnection(url + dbname, dbuser, dbpass);
            sta = con.createStatement();

        } catch (Exception e) {
        	log.error(e.getMessage());
        }
        return sta;
    }
    public static Connection getConn() {
    	 Connection con=null;
    	try {

            Class.forName(driver);
           con = DriverManager.getConnection(url + dbname, dbuser, dbpass);

        } catch (Exception e) {
        	log.error(e.getMessage());
        }
        return con;
    }
}