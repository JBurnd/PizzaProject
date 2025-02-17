import java.sql.*;
import java.util.ArrayList;

/**
 * @program JavaTutorial
 * @description:
 * @author: Zong Shi
 * @create 2019-11-01 10:54 AM
 */

public class DataHandler {

  //Field for order
  int Order_ID;
  String Customer_Name;
  String Contact_Number;
  Time Order_Time;
  Time Complete_Time;
  double Total_Price;
  String status;
  String orderDetail;

  //Field for ingredients
  int ingreID;
  String ingreName;
  int ingreNum;
  double ingrePrice;

  //ArrayList for total order list and ingredients
  ArrayList<Orders> totalOrdersList = new ArrayList<>();
  ArrayList<Ingredients> ingredientsList =new ArrayList<>();

  //For connection to Database
  DBconnection dBconnection;
  Statement stm;


  //Run this constructor to connect to DB
  public DataHandler() throws SQLException {
    dBconnection = new DBconnection();
    stm = dBconnection.getStmt();
    readOrder();
    readIngredients();

  }

  //Method to read order information and save oders object to arrayList totalOrderList;
  public ArrayList<Orders> readOrder() throws SQLException {
    String sql = ("Select * from " + "Orders" + " ;");
    ResultSet rs = stm.executeQuery(sql);

    while (rs.next()) {
      Order_ID = rs.getInt("Order_ID");
      Customer_Name = rs.getString("Customer_Name");
      Contact_Number = rs.getString("Contact_Number");
      Order_Time = rs.getTime("Order_Time");
      Complete_Time = rs.getTime("Complete_Time");
      Total_Price = rs.getDouble("Total_Price");
      status = rs.getString("status");
      orderDetail = rs.getString("Order_Details");

      totalOrdersList.add(new Orders(Order_ID, Customer_Name, Contact_Number, Order_Time, Complete_Time, Total_Price, status, orderDetail));
    }
    return totalOrdersList;
  }


  //Method to read ingredients information and save to arrayList ingredientsList
  public ArrayList<Ingredients> readIngredients() throws SQLException {
    String sql = ("Select * from " + "ingredients" + " ;");
    ResultSet rs = stm.executeQuery(sql);

    while (rs.next()) {
      ingreID = rs.getInt("id");
      ingreName = rs.getString("name");
      ingreNum = rs.getInt("Amount");
      ingrePrice = rs.getDouble("Price");
      ingredientsList.add(new Ingredients(ingreID, ingreName, ingreNum, ingrePrice));
    }
    return ingredientsList;
  }

  //Including a method to update the status from "new" to "cooking" and a method to update the ingredients storage on the table "ingredients"
  public void confirmOrder(int order_ID) throws SQLException {
    //Execute SQL to update the order status in the DB
    String orderToConfirm = findOrder(order_ID);
    ArrayList<String> temp = splitOrder(orderToConfirm);

    for(Orders order : totalOrdersList)
    {
      if(order.getOrder_ID() == order_ID && order.getStatus().equalsIgnoreCase("new"))
      {
        String orderSQL = "UPDATE orders set status = " + "'cooking'" + " where order_ID = " + order_ID;
        stm.execute(orderSQL);

        try {
          for(String ingred:temp)
          {
            String ingredSQL = null;
            for(Ingredients ingredients : ingredientsList)
            {
              if(ingredients.getName().equals(ingred))
              {
                if(ingredients.getAmount()>0)
                {
                  ingredSQL = "UPDATE ingredients SET amount = amount - 1 Where name = " + "'" + ingred + "'";
                  break;
                }else {
                  System.out.println("Not enough" + ingredients.getName() +" storage");
                }
              }
            }

            stm.execute(ingredSQL);
          }
          break;
        }catch(SQLException e)
        {
          e.printStackTrace();
          break;
        }

//        break;
      }
    }

//    //Execute SQL to update the ingredients storage in the DB
//
//    try {
//        for(String ingred:temp)
//        {
//          String ingredSQL = null;
//          for(Ingredients ingredients : ingredientsList)
//          {
//            if(ingredients.getName().equals(ingred))
//            {
//              if(ingredients.getAmount()>0)
//              {
//                ingredSQL = "UPDATE ingredients SET amount = amount - 1 Where name = " + "'" + ingred + "'";
//                break;
//              }else {
//                System.out.println("Not enough" + ingredients.getName() +" storage");
//              }
//            }
//          }
//
//          stm.execute(ingredSQL);
//        }
//      return true;
//    }catch(SQLException e)
//    {
//      e.printStackTrace();
//      return false;
//    }
  }


  //Method to change the status of "new" order to status "cooking"
  public void completeOrder(int order_ID) throws SQLException {
    for(Orders order : totalOrdersList)
    {
      if(order.getOrder_ID() == order_ID && order.getStatus().equalsIgnoreCase("cooking"))
      {
        String orderSQL = "UPDATE orders set status = " + "'completed'" + " where order_ID = " + order_ID;
        stm.execute(orderSQL);
        break;
      }
    }

  }

  //Method to cancel the order, and add the ingredients back to ingredients inventory
  public void cancelOrder(int order_ID) throws SQLException {
    for(Orders order : totalOrdersList)
    {
      if(order.getOrder_ID() == order_ID && order.getStatus().equalsIgnoreCase("new"))
      {
        System.out.println("order detail " +  order.toString() + "---------");
        String orderSQL = "UPDATE orders set status = " + "'cancelled'" + " where order_ID = " + order_ID;
        stm.execute(orderSQL);

        String ingredDetail = order.getOrderDetail();
        ArrayList<String> ingredArray = this.splitOrder(ingredDetail);
        for(String ingred : ingredArray)
        {
          for(Ingredients ingredients : ingredientsList)
          {
            if(ingredients.getName().equals(ingred))
            {
              String ingredSQL = "UPDATE ingredients SET amount = amount + 1  Where name = " + "'" + ingred + "'";
              stm.execute(ingredSQL);
            }
          }

        }
        break;
      }
    }
  }

  //Method to update the ingredients to the inventory, need to input the ingredient name and num which you want to add.
  public void updateIngred(String ingredName, int num) throws SQLException {
   int currentNum = 0;
   for(Ingredients ingredient : ingredientsList)
   {
     if(ingredient.getName().equalsIgnoreCase(ingredName))
     {
       currentNum = ingredient.getAmount();
     }
   }
    String reorderSQL = "UPDATE ingredients SET amount = " + (num+currentNum) + " Where name = " + "'" + ingredName + "'";
   stm.execute(reorderSQL);
  }


  //Method to return new order list
  public ArrayList<Orders> getNewOrderList()
  {
    ArrayList<Orders> newOrderList = new ArrayList<>();
    for(Orders order : totalOrdersList)
    {
      if(order.getStatus().equalsIgnoreCase("new"))
        newOrderList.add(order);
    }
    return newOrderList;
  }


  //Method to return cooking order list
  public ArrayList<Orders> getCookingOrderList()
  {
    ArrayList<Orders> cookingOrderList = new ArrayList<>();
    for(Orders order : totalOrdersList)
    {
      if(order.getStatus().equalsIgnoreCase("cooking"))
        cookingOrderList.add(order);
    }
    return cookingOrderList;
  }

  //Method to return completed order list
  public ArrayList<Orders> getCompletedOrderList()
  {
    ArrayList<Orders> completedOrderList = new ArrayList<>();
    for(Orders order : totalOrdersList)
    {
      if(order.getStatus().equalsIgnoreCase("cooking"))
        completedOrderList.add(order);
    }
    return completedOrderList;
  }

  //Method to return cancelled order list
  public ArrayList<Orders> getCancelledOrderList()
  {
    ArrayList<Orders> cancelledOrderList = new ArrayList<>();
    for(Orders order : totalOrdersList)
    {
      if(order.getStatus().equalsIgnoreCase("cooking"))
        cancelledOrderList.add(order);
    }
    return cancelledOrderList;
  }

  //Method to splite order detail into arrayList.
    public ArrayList<String> splitOrder(String orderDetail)
  {
    ArrayList<String> ingredList = new ArrayList<>();
    String[] parts  = orderDetail.split(" ");
    for (int i = 0; i < parts.length; i++) {
      ingredList.add(parts[i]);
    }
    return ingredList;
  }

  //method to find the orderDetail if the order_ID match with order ID in the total order list
  public String findOrder(int order_ID)
  {
    for(Orders order : totalOrdersList)
    {
      if(order.getOrder_ID()==order_ID)
      {
        return order.getOrderDetail();
      }
    }
    return null;
  }


  //Testing code
  public void printOutOrders()
  {
    for(Orders order : totalOrdersList)
    {
      System.out.println(order.toString());
    }
  }

  //Testing code
  public void printOutIngredients()
  {
    System.out.println("ingredientsList size " + ingredientsList.size());
    for(Ingredients ingredient : ingredientsList)
    {
      System.out.println(ingredient.toString());
    }
  }




}
    