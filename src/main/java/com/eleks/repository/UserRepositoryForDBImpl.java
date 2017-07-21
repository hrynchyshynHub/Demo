package com.eleks.repository;

import com.eleks.model.Post;
import com.eleks.model.User;
import org.postgresql.jdbc3.Jdbc3PoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by ivan.hrynchyshyn on 10.07.2017.
 */

public class UserRepositoryForDBImpl implements UserRepository{

    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryForDBImpl.class);



    private static String selectUserByNameQuery = "SELECT *  FROM public.\"User\" WHERE username = ?";
    private static String insertUserQuery = "INSERT INTO public.\"User\"(username , password) VALUES (?,?)";
    private static String selectAllUsersQuery = "SELECT * FROM public.\"User\"";
    private static String insertPostQuery = "INSERT INTO public.\"Post\"(description, user_id) VALUES (?,?)";
    private static String selectPostForUserQuery = "SELECT * FROM public.\"Post\" WHERE user_id = ? ";
    private static String selectUserWithPostQuery = "SELECT *  FROM public.\"User\" u LEFT JOIN public.\"Post\" p ON u.id = p.user_id WHERE u.username = ?";


    private String dbUrl;
    private String dbClass;
    private String username;
    private String password;
    private Properties properties;
    private Jdbc3PoolingDataSource dataSource;

    private static UserRepositoryForDBImpl userRepositoryForDB;



    private UserRepositoryForDBImpl(){
        InputStream is = null;
        properties = new Properties();
        String filename = "database.properties";
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(filename));
            dbUrl = properties.getProperty("db.url");
            dbClass = properties.getProperty("db.driver");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");

            dataSource = new Jdbc3PoolingDataSource();
            dataSource.setDataSourceName("datasource");
            dataSource.setUrl(dbUrl);
            dataSource.setUser(username);
            dataSource.setPassword(password);
            dataSource.setMaxConnections(10);

        } catch (IOException e) {
            logger.info("props not load", e);
        }
    }

    public static UserRepositoryForDBImpl getInstance(){
        if(userRepositoryForDB == null){
            synchronized (UserRepositoryForDBImpl.class){
                if(userRepositoryForDB == null) userRepositoryForDB = new UserRepositoryForDBImpl();
            }
        }
        return userRepositoryForDB;
    }


    @Override
    public User findUserByName(String username) throws Exception {

        User user = null;
        try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(selectUserByNameQuery)){
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String dbusername = rs.getString("username");
                String dbpassword = rs.getString("password");
                user = new User(dbusername, dbpassword);
                user.setId(id);
            }
            rs.close();
        }catch (Exception e){
            logger.info("Problem with connection  "  + e );
        }
        return user;
    }

    @Override
    public boolean addUser(User u) {
        String username = u.getUsername();
        String password = u.getPassword();
        try( Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(insertUserQuery))
            {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                logger.info("Error ocured where user was add to database" + e);
            } catch (Exception e) {
            logger.info("no connection" + e);
        }
        return false;
        }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(selectAllUsersQuery);
            while (resultSet.next()){
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                users.add(new User(username,password));
            }
            resultSet.close();
        }catch (SQLException e){
            logger.info("Problem occured by database connection " + e);
        } catch (Exception e) {
            logger.info("no connection" + e);
        }
        return users;
    }

    @Override
    public void addPostToUser(Post p, User u) {
        try (Connection dbConnection = dataSource.getConnection();PreparedStatement preparedStatement = dbConnection.prepareStatement(insertPostQuery)){
            preparedStatement.setString(1,p.getDescription());
            preparedStatement.setInt(2,u.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.info("Error during connected to database " + e );
        } catch (Exception e) {
            logger.info("no connection" + e);
        }
    }

    public List<Post> findUserPosts(int userId){

        List<Post> posts = new ArrayList<>();

        try(Connection connection =dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(selectPostForUserQuery)){
            preparedStatement.setInt(1,userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String description = resultSet.getString("description");
                posts.add(new Post(id,description));
            }
            resultSet.close();
        }catch (SQLException e){
            logger.info("Error during connected to database " + e );
        }
        catch (Exception e) {
            logger.info("no connection" + e);
        }
        return posts;
    }

    public User findUserWithPosts(String username){
        User user = new User();
        List<Post> posts = new ArrayList<>();
        try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(selectUserWithPostQuery)){
            statement.setString(1, username);
            System.out.println(statement.toString());
            ResultSet rs = statement.executeQuery();
            int dbuserid = rs.getInt("id");
            String dbusername = rs.getString("username");
            String dbpassword = rs.getString("password");

            while (rs.next()) {
                Post post = new Post(rs.getString("description"));
                posts.add(post);
            }
            rs.close();
            user.setUsername(dbusername);
            user.setPassword(dbpassword);
            user.setId(dbuserid);
            user.setPosts(posts);

        }catch (Exception e){
            logger.info("Error during connected to database " + e );
        }
        return user;
    }

    private Connection getDbConection() throws Exception{
        Connection connection = null;
        try {
             Class.forName(dbClass);
             connection = DriverManager.getConnection(dbUrl,this.username,this.password);
        } catch (SQLException e){
            logger.info("Error during connected to database " + e );
        }
        if(connection != null) {
            return connection;
        }else throw new Exception();
    }



    @Override
    public void deleteUserByName(String usermame) {

    }
}
