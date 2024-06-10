package com.guessdraw.app.handlers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class Database {

    private static final String username;
    private static final String password;
    private static final String url;

    static{
        try{
            username = Files.readString(Path.of("database_username"));
            password = Files.readString(Path.of("database_password"));
            url = Files.readString(Path.of("database_url"));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static int add_drawing(int topic_id, int user_id) { // user_id = 1 stands for guest (unlogged user)
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "INSERT INTO drawings(user_id, topic_id) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, user_id);
            preparedStatement.setInt(2, topic_id);
            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println(rowsInserted + " row(s) inserted.");
            ResultSet rs = preparedStatement.getGeneratedKeys();
            rs.next();
            System.out.println("Drawing added with id: " + rs.getInt(1));
            int id = rs.getInt(1);
            preparedStatement.close();
            connection.close();
            return id;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public static class Topic {
        public int topic_id;
        public String topic_name;
        public Topic(int topic_id, String topic_name) {
            this.topic_id = topic_id;
            this.topic_name = topic_name;
        }
    }

    public static Topic get_random_topic() {
        Topic topic = null;
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "SELECT topic_id, name FROM topics ORDER BY RANDOM() LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                topic = new Topic(rs.getInt("topic_id"), rs.getString("name"));
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return topic;
    }

    public static Topic get_topic(int topic_id) {
        Topic topic = null;
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "SELECT topic_id, name FROM topics WHERE topic_id = (?) LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, topic_id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                topic = new Topic(rs.getInt("topic_id"), rs.getString("name"));
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return topic;
    }

    public static class Drawing {
        public int drawing_id;
        public int user_id;
        public Topic topic;
        public Drawing(int drawing_id, int user_id, int topic_id) {
            this.drawing_id = drawing_id;
            this.user_id = user_id;
            this.topic = get_topic(topic_id);
        }
    }

    public static Drawing get_drawing(int drawing_id) {
        Drawing drawing = null;
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "SELECT drawing_id, user_id, topic_id FROM drawings WHERE drawing_id = (?) LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, drawing_id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                drawing = new Drawing(rs.getInt("drawing_id"), rs.getInt("user_id"), rs.getInt("topic_id"));
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return drawing;
    }

    public static Drawing get_random_drawing() {
        Drawing drawing = null;
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "SELECT drawing_id, user_id, topic_id FROM drawings ORDER BY RANDOM() LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                drawing = new Drawing(rs.getInt("drawing_id"), rs.getInt("user_id"), rs.getInt("topic_id"));
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return drawing;
    }

    public static int drawing_count() {
        int count = 1;
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "SELECT COUNT(*) FROM drawings";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return count;
    }

    public static class User {
        public int user_id;
        public String login;
        public String password;
        public User(){
            this.user_id = 1;
            this.login = "unlogged";
            this.password = "";
        }
        public User(int user_id, String login, String password) {
            this.user_id = user_id;
            this.login = login;
            this.password = password;
        }
    }

    public static User login(String login, String password) {
        int user_id = -1;
        try {
            Connection connection = DriverManager.getConnection(url, Database.username, Database.password);
            String sql = "SELECT user_id FROM users WHERE login = (?) AND password = (?) LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                user_id = rs.getInt("user_id");
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (user_id == -1) {
            return new User();
        }
        return new User(user_id, login, password);
    }

    private static void addUser(String login, String password) {
        try {
            Connection connection = DriverManager.getConnection(url, Database.username, Database.password);
            String sql = "INSERT INTO users(login, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println(rowsInserted + " row(s) inserted.");
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static User register(String login, String password) {
        int user_id = -1;
        try {
            Connection connection = DriverManager.getConnection(url, Database.username, Database.password);
            String sql = "SELECT user_id FROM users WHERE login = (?) LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, login);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                user_id = rs.getInt("user_id");
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (user_id != -1) {
            return new User();
        }
        addUser(login, password);
        try {
            Connection connection = DriverManager.getConnection(url, Database.username, Database.password);
            String sql = "SELECT user_id FROM users WHERE login = (?) LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, login);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                user_id = rs.getInt("user_id");
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new User(user_id, login, password);
    }

    public static String getUsername(int user_id) {
        String user = "unlogged";
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "SELECT login FROM users WHERE user_id = (?) LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, user_id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                user = rs.getString("login");
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }
}
