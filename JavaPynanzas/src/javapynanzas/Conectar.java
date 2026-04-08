package javapynanzas;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;
public class Conectar {
    public static final String URL = "jdbc:postgresql://localhost:5432/database";
    public static final String USER = "postgres";
    public static final String CLAVE = "1234"; 
    
    public Connection getConexion(){
        Connection con = null;
        try{
            Class.forName("org.postgresql.Driver");
            //Class.forName("cdata.jdbc.postgresql.PostgreSQLDriver");
            con = DriverManager.getConnection(URL, USER, CLAVE);             
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return con;
        
        
    }	
}
