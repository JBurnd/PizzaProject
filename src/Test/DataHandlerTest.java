package Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DataHandlerTest {
    DataHandler dh = new DataHandler();
    Statement stmt;

    DataHandlerTest() throws SQLException {
        stmt = dh.getStatement();
    }

    @org.junit.jupiter.api.Test
    void completeOrder() {
    }

    @org.junit.jupiter.api.Test
    void cancelOrder() {
    }

    @org.junit.jupiter.api.Test
    void updateIngred() {
        try {
            String query = "select * from ingredients";
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            int testVal = 20;
            System.out.println(rs.getString("name") + " " + rs.getInt("Amount"));
            dh.updateIngred(rs.getString("Name"), testVal);
            rs = stmt.executeQuery(query);
            rs.next();
            System.out.println(rs.getString("name") + " " + rs.getInt("Amount"));
            dh.updateIngred(rs.getString("Name"), -testVal);
            rs = stmt.executeQuery(query);
            rs.next();
            System.out.println(rs.getString("name") + " " + rs.getInt("Amount"));
        }
        catch (Exception e ) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void orderDetailProcess() {
        String test = "alola : Cheese, Ham, Pineapple. johto : Beef, BBQ, Onion. " +
                "kanto : Mozzarella, Basil, Tomato. alola : Cheese, Ham, Pineapple. custom : " +
                "Beef,Cheese,Pepperoni,. custom : Basil,Ham,Pineapple,Vegetables,. ";
        String result = dh.orderDetailProcess(test);
        String expected = "Cheese Ham Pineapple Beef BBQ Onion Mozzarella Basil Tomato Cheese Ham Pineapple" +
                " Beef Cheese Pepperoni Basil Ham Pineapple Vegetables";
        assertTrue(result.equalsIgnoreCase(expected));
    }

    @org.junit.jupiter.api.Test
    void chrissyString() {
            String test = "alola : Cheese, Ham, Pineapple. johto : Beef, BBQ, Onion. " +
                    "kanto : Mozzarella, Basil, Tomato. alola : Cheese, Ham, Pineapple. custom : " +
                    "Beef,Cheese,Pepperoni,. custom : Basil,Ham,Pineapple,Vegetables,. ";
            String expected = "alola, johto, kanto, alola, custom: Beef Cheese Pepperoni, " +
                    "custom: Basil Ham Pineapple Vegetables,";

        System.out.println(dh.ChrissyString(test));
        System.out.println(expected);
            assertTrue(dh.ChrissyString(test).equalsIgnoreCase(expected));
    }

    @org.junit.jupiter.api.Test
    void splitOrder() {
        String test = "alola : Cheese, Ham, Pineapple. johto : Beef, BBQ, Onion. " +
                "kanto : Mozzarella, Basil, Tomato. alola : Cheese, Ham, Pineapple. custom : " +
                "Beef,Cheese,Pepperoni,. custom : Basil,Ham,Pineapple,Vegetables,. ";
        String result = dh.orderDetailProcess(test);
        ArrayList<String> result2 = dh.splitOrder(result);
        String expected = "Cheese Ham Pineapple Beef BBQ Onion Mozzarella Basil Tomato Cheese Ham Pineapple" +
                " Beef Cheese Pepperoni Basil Ham Pineapple Vegetables";
        String[] exp = expected.split(" ");
        assertArrayEquals(exp, result2.toArray());
    }

    @org.junit.jupiter.api.Test
    void findOrder() {
        try {
            String query = "select * from orders";
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            String result = dh.findOrder(rs.getInt("Order_ID"));
            assertTrue(result.equalsIgnoreCase(rs.getString("Order_details")));
        }
        catch (Exception e ) {
            e.printStackTrace();
        }
    }
}