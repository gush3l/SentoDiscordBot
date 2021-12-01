package me.gushel.sentodiscordbot.MySQL;

import me.gushel.sentodiscordbot.SentoDiscordBot;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.logging.Logger;

public class MySQL {

    public static FileConfiguration config = SentoDiscordBot.getInstance().getConfig();

    public static String host = config.getString("Bot.mysql.host");
    public static String port = config.getString("Bot.mysql.port");
    public static String database = config.getString("Bot.mysql.database");
    public static String username = config.getString("Bot.mysql.username");
    public static String password = config.getString("Bot.mysql.password");
    public static Connection con;

    static Logger log = SentoDiscordBot.getInstance().getLogger();

    // connect
    public static void connect() {
        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                log.info("Connected to MySQL database successfully!");
            } catch (SQLException e) {
                log.info("An error occurred while trying to connect to MySQL database!");
                e.printStackTrace();
            }
        }
    }

    // disconnect
    public static void disconnect() {
        if (isConnected()) {
            try {
                con.close();
                log.info("Disconnected from MySQL database successfully!");
            } catch (SQLException e) {
                log.info("An error occurred while trying to disconnect from MySQL database!");
                e.printStackTrace();
            }
        }
    }

    // isConnected
    public static boolean isConnected() {
        return (con != null);
    }

    // getConnection
    public static Connection getConnection() {
        return con;
    }

    public static void createTable(){
        try{
            PreparedStatement ps = MySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS linkedAccounts (UUID TEXT(100),DiscordID TEXT(100),VerifyCode TEXT(100))");
            ps.executeUpdate();
        }catch(SQLException e){
            log.severe("Failed to create the MySQL table in the database!");
        }
    }

    public static void addUserToDatabase(String UUID,String DiscordID,String VerifyCode){
        try{
            PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT IGNORE INTO linkedAccounts (UUID,DiscordID,VerifyCode) VALUES (?,?,?)");
            ps.setString(1, UUID);
            ps.setString(2, DiscordID);
            ps.setString(3, VerifyCode);
            ps.executeUpdate();
        }catch (SQLException e){
            log.severe("Failed to add the user "+UUID+" to the database!");
        }
    }

    public static void removeUserFromDatabase(String UUID){
        try{
            PreparedStatement ps = MySQL.getConnection().prepareStatement("DELETE FROM linkedAccounts WHERE UUID = ?");
            ps.setString(1, UUID);
            ps.executeUpdate();
        }catch (SQLException e){
            log.severe("Failed to remove the user "+UUID+" from the database!");
        }
    }

    public static void removeUserFromDatabaseID(String DiscordID){
        try{
            PreparedStatement ps = MySQL.getConnection().prepareStatement("DELETE FROM linkedAccounts WHERE DiscordID = ?");
            ps.setString(1, DiscordID);
            ps.executeUpdate();
        }catch (SQLException e){
            log.severe("Failed to remove the user with the Discord ID "+DiscordID+" from the database!");
        }
    }

    public static String getVerifyCodeFromUUID(String UUID){
        try{
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT VerifyCode FROM linkedAccounts WHERE UUID = ?");
            ps.setString(1, UUID);
            ResultSet rs = ps.executeQuery();
            String verifyCode = null;
            if (rs.next() == true) {
                verifyCode = rs.getString("VerifyCode");
            }
            return verifyCode;
        }catch (SQLException e){
            log.severe("Failed to get the Verify Code with UUID "+UUID+" from the database!");
        }
        return null;
    }

    public static String getVerifyCodeFromDiscordID(String DiscordID){
        try{
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT VerifyCode FROM linkedAccounts WHERE DiscordID = ?");
            ps.setString(1, DiscordID);
            ResultSet rs = ps.executeQuery();
            String verifyCode = null;
            if (rs.next() == true) {
                verifyCode = rs.getString("VerifyCode");
            }
            return verifyCode;
        }catch (SQLException e){
            log.severe("Failed to get the Verify Code with DiscordID "+DiscordID+" from the database!");
        }
        return null;
    }

    public static String getUUIDfromDiscordID(String DiscordID){
        try{
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT UUID FROM linkedAccounts WHERE DiscordID = ?");
            ps.setString(1, DiscordID);
            ResultSet rs = ps.executeQuery();
            String pUUID = null;
            if (rs.next() == true) {
                pUUID = rs.getString("UUID");
            }
            return pUUID;
        }catch (SQLException e){
            log.severe("Failed to get the UUID with DiscordID "+DiscordID+" from the database!");
        }
        return null;
    }

}
