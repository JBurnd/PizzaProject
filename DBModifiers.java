import java.sql.*;

public class DBModifiers {
    Connection con = null;

    //need to work out when in the process stock is being removed from stock database
    //if is being removed before order is sent through, need to allow the cancellation to replenish stock
    //Use reader and counter of pizza methods done by Jason, add counted amount to current amount, then
    // use my method to update stock

    public DBModifiers() {

        String string = "alola : Cheese, Ham, Pineapple. johto : Beef, BBQ, Onion. kanto : Mozzarella, Basil, Tomato. alola : Cheese, Ham, Pineapple. custom : Beef,Cheese,Pepperoni,. custom : Basil,Ham,Pineapple,Vegetables,. ";
        JasonSplit(string);
        System.out.println(converter("summary", string));
        try {
            String dbUser = "pizza";
            String usrPass = "pizza";
            Class.forName("org.mariadb.jdbc.Driver");
            String url = "jdbc:mariadb://10.140.154.154:3306/pizza";
            con = DriverManager.getConnection(url, dbUser, usrPass);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //tests
        changeOrderStat(1, "Cooking");
        int stkID = 1;
        newStock(stkID,"Capsicum", 50, 1);
        stkID++;
        newStock(stkID, "Chorizo", 50, 2.50);
        updateStock("Capsicum", 25);
        updateStock("Chorizo", 1.25);
    }

    //implement a mode if necessary - pass through int parameter which sets mode

    public void newStock(int id, String name, int amount, double price) {
        //need to look into about auto incr
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from ingredients;");
            if (stmt.executeQuery("select * from ingredients where name = \"" + name + "\";").next()) { //checks to see if this ingredient exists already
                System.out.println("haha nah"); //could just remove id and instead make name primary key
            }
            else {
                if (rs.next()) {
                    rs.last();
                    id = rs.getInt("id") + 1;
                }
                stmt.execute("insert into ingredients (id, name, amount, price) " +
                        "values (" + id + ", \"" + name + "\", " + amount + ", " + price + ");");
            }
            stmt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStock(String name, double price) {
        try {
            Statement stmt = con.createStatement();
            String command = "update ingredients set Price = " + price + "where name = \"" + name + "\";";
            stmt.execute(command);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStock(String name, int amount) {
        try {
            Statement stmt = con.createStatement();
//                String getOrder = "select * from orders where Order_ID = " + orderID + ";";
//                ResultSet rs = stmt.executeQuery(getOrder);
            String command = "update ingredients set Amount = " + amount + " where name = \"" + name + "\";";
            stmt.execute(command);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void changeOrderStat(int orderID, String status) {
        /*buttons needed: Drop down with submit to change to any of the four statuses
         * Confirm order - sends through order ID and "cooking"
         * Decline order - sends through order ID and "cancelled"
         * Should have clause that confirm/decline buttons only selectable if status = new*/
        try {
            Statement stmt = con.createStatement();
//                String getOrder = "select * from orders where Order_ID = " + orderID + ";";
//                ResultSet rs = stmt.executeQuery(getOrder);
            String command = "update orders set Status = \"" + status + "\" where Order_ID = " + orderID + ";";
            stmt.execute(command);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String JasonSplit(String string) {
        String forCounter = ""; //Cheese Ham Pineapple Beef BBQ Onion
        string = string.replace(':', ',');
        String[] pizzas = string.split("\\.");
        for (String s : pizzas) {
            s.trim();
            String[] ingrs = s.split(",");
            String type = ingrs[0].trim();
            if (type.trim().equalsIgnoreCase("custom")) {
                for (int i = 1; i < ingrs.length; i++)
                    forCounter = forCounter + " " + ingrs[i].trim();
            }
        }
        System.out.println(forCounter);
        return forCounter;
    }

    public String converter(String mode, String string) {
        String forCounter = ""; //Cheese Ham Pineapple Beef BBQ Onion
        String type = "";
        string = string.replace(':', ',');
        String[] pizzas = string.split("\\.");
        for (String s : pizzas) {
            s.trim();
            String[] ingrs = s.split(",");
            type = ingrs[0].trim();
            if (type.trim().equalsIgnoreCase("custom")) {
                type = type + ":";
                for (int i = 1; i < ingrs.length; i++) {
                    forCounter = forCounter + " " + ingrs[i].trim();
                    type = type + " " + ingrs[i].trim();
                }
            }
            System.out.print(type + ", ");

        }
        System.out.println();
        System.out.println(forCounter);
        System.out.println(type);
        if (mode.equalsIgnoreCase("counter"))
            return forCounter;
        else if (mode.equalsIgnoreCase("Summary"))
            return type;
        else
            return null;
    }

    public static void main (String[] args) {
        new DBModifiers();
    }
}