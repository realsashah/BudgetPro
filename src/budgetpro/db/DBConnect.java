package budgetpro.db;

import budgetpro.businesslogic.Expense;
import budgetpro.businesslogic.ExpenseType;

import java.sql.*;
import java.util.ArrayList;

public class DBConnect {

    private final String path;
    private Connection connection;
    public DBConnect(String path) {
        this.path = "jdbc:sqlite:"+path;
    }

    public void connect() {
        connection = null;
        try {
            connection = DriverManager.getConnection(path);
            System.out.println("Connection to SQLite has been established.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addExpenseType(String id, String name) {
        if (isPresentExpenseType(id))
            return;
        String query = "INSERT INTO expenseType(id,name) VALUES(?,?);";

        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addExpense(String id,double amount,ExpenseType expenseType,String detail,String dated) {
        if (isPresentExpense(id))
            return;
        String query = "INSERT INTO expense(amount,expenseType,detail,dated,id) VALUES(?,?,?,?,?);";

        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setDouble(1, amount);
            pstmt.setString(2, expenseType.getId());
            pstmt.setString(3, detail);
            pstmt.setString(4, dated);
            pstmt.setString(5, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void readExpenseType(ArrayList<ExpenseType> expenseTypes){
        if(expenseTypes==null)
            return;
        ExpenseType temp;
        String query = "SELECT * FROM expenseType;";
        try {

            Statement statement = connection.createStatement();

            ResultSet resultSet=statement.executeQuery(query);

            while (resultSet.next()){
                temp=new ExpenseType(resultSet.getString("id"),resultSet.getString("name"));
                expenseTypes.add(temp);
            }

        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    public void readExpenses(ArrayList<Expense> expenses,ArrayList<ExpenseType> expenseTypes){
        if(expenses==null)
            return;
        Expense temp;
        ExpenseType expenseType = null;
        String query = "SELECT * FROM expense;";
        String id;
        String expenseTypeId;
        double amount;
        String details;
        String expenseDate;
        try {

            Statement statement = connection.createStatement();

            ResultSet resultSet=statement.executeQuery(query);


            boolean found;
            while (resultSet.next()) {
                found = false;
                id = resultSet.getString("id");
                expenseTypeId = resultSet.getString("expenseType");
                amount = resultSet.getFloat("amount");
                details = resultSet.getString("detail");
                expenseDate = resultSet.getString("dated");
                for (ExpenseType type : expenseTypes) {
                    if (type.getId().equals(id)) {
                        expenseType = new ExpenseType(type);
                        found = true;
                    }
                }

                if (!found)
                    expenseType = new ExpenseType(expenseTypes.get(0));
                temp = new Expense(id, expenseType, details, amount, expenseDate);

                expenses.add(temp);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isPresentExpenseType(String id) {
        boolean isPresent = false;
        String query = "SELECT * FROM expenseType WHERE id=\"" + id + "\";";
        try {

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                isPresent = true;
                System.out.println(id + " expenseType found");
                return isPresent;
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return isPresent;
    }

    public boolean isPresentExpense(String id) {
        boolean isPresent = false;
        String query = "SELECT * FROM expense WHERE id=\"" + id + "\";";
        try {

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                isPresent = true;
                System.out.println(id + " expense found");
                return isPresent;
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return isPresent;
    }
}
