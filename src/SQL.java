import javafx.util.converter.LocalDateStringConverter;

import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;

public class SQL {
    /**
     * url: dataname
     */
    public String url = "jdbc:mysql://localhost:3307/rtr";
    public String username = "root";
    public String password = "Seokjin0412";
    public static String tableEmployee = "employee";
    public static String tableBill = "bill";
    public static String tableBilldetails = "billdetails";
    public static String tableFood = "food";
    public static String tableDtable = "dtable";
    public static Connection connection;
    public static List<String> wordsList = new ArrayList<>();
    public static int num = 0;

    public SQL() throws SQLException {
        connect();
        mainAccount.logIn();
        getAllEmployees(allEmployees());
        getAllFoodsSQL(allFoods());
        getAllTablesSQL(allTables());
        getAllBillsSQL(allBills());
        getNewBillIdSQL();
        getNewFoodIdSQL();
        getNewTableIdSQL();
        geNewEmployeeIdSQL();
        //getWords(getAllWord());
        //wordList();
    }


    public void connect() {
        try {
            connection = DriverManager.getConnection(url, username, password);
            //num = getNumber();
            System.out.println("Connected to database");
        } catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }


    public static void getNewBillIdSQL() {
        ResultSet rs = null;
        String sqlCommand = "SELECT * FROM " + tableBill + " ORDER BY bID DESC LIMIT 1";
        Statement st;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(sqlCommand);
            if (rs != null) {
                while (rs.next()) {
                    billManagement.newBillId = rs.getInt(1);
                }

            }
        } catch (SQLException e) {
            System.out.println("Select error");
            e.printStackTrace();
        }
    }

    public static void getNewFoodIdSQL() {
        ResultSet rs = null;
        String sqlCommand = "SELECT * FROM " + tableFood + " ORDER BY fID DESC LIMIT 1";
        Statement st;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(sqlCommand);
            if (rs != null) {
                if (rs != null) {
                    while (rs.next()) {
                        foodManagement.newFoodId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Select error");
            e.printStackTrace();
        }
    }

    public static void getNewTableIdSQL() {
        ResultSet rs = null;
        String sqlCommand = "SELECT * FROM " + tableDtable + " ORDER BY tId DESC LIMIT 1";
        Statement st;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(sqlCommand);
            if (rs != null) {
                while (rs.next()) {
                    tableManagement.newTableId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Select error");
            e.printStackTrace();
        }
    }

    public static void geNewEmployeeIdSQL() {
        ResultSet rs = null;
        String sqlCommand = "SELECT * FROM " + tableEmployee + " ORDER BY eID DESC LIMIT 1";
        Statement st;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(sqlCommand);
            rs.next();
            employeeManagement.newEmployeeId = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Select error");
            e.printStackTrace();
        }
    }

    // th??m t???t c??? nh??n vi??n v??o danh s??ch
    public ResultSet allEmployees() {
        ResultSet rs = null;
        String sqlCommand = "select * from " + tableEmployee;
        Statement st;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(sqlCommand);
        } catch (SQLException e) {
            System.out.println("Select error");
            e.printStackTrace();
        }
        return rs;
    }

    public void getAllEmployees(ResultSet rs) throws SQLException {
        while (rs.next()) {
            employeeManagement.addEmployeeToList(new employee(rs.getInt(1)
                    , rs.getString(2)
                    , rs.getString(3)
                    , Instant.ofEpochMilli(rs.getDate(4).getTime()).atZone(ZoneId.systemDefault()).toLocalDate()
                    , rs.getString(5)
                    , rs.getString(6)
                    , Instant.ofEpochMilli(rs.getDate(7).getTime()).atZone(ZoneId.systemDefault()).toLocalDate()));
        }
    }

    // th??m nh??n vi??n m???i g???i t??? employeeManagement.addEmployee
    public static void addEmployeeSQL(employee e) {
        String sqlCommand = "insert into " + tableEmployee + " value( ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlCommand);
            pst.setInt(1, e.employeeId);
            pst.setString(2, e.lastName);
            pst.setString(3, e.firstName);
            pst.setDate(4, java.sql.Date.valueOf(e.birthday));
            pst.setString(5, e.jobTitle);
            pst.setString(6, e.phoneNumber);
            pst.setDate(7, java.sql.Date.valueOf(e.joinDate));
            pst.setString(8, "1234567");
            if (pst.executeUpdate() > 0) {
                employeeManagement.addEmployeeToList(e);
                System.out.println("Th??m nh??n vi??n th??nh c??ng: " + e.employeeId);
            } else {
                System.out.println("Ch??a th??? th??m nh??n vi??n!");
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }
    }

    //x??a nh??n vi??n c??
    public static boolean deleteEmployee(int employeeId) {
        String sqlCommand = "delete from " + tableEmployee + " where eID = ?";
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlCommand);
            pst.setInt(1, employeeId);
            if (pst.executeUpdate() > 0) {
                System.out.println("???? x??a nh??n vi??n" + employeeId);
                employeeManagement.removeEmployee(employeeId);
            } else {
                System.out.println("Kh??ng c?? nh??n vi??n c???n x??a!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean fixInfoEmployee(int id, String lastName, String firstName, LocalDate birth, String jobTitle, String phone) {
        String sqlCommand = "UPDATE " + tableEmployee + " SET lastName = ?, firstName = ?, birthday = ?,jobTitle = ?, numberPhone = ? WHERE eID = " + id;
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlCommand);
            pst.setString(1, lastName);
            pst.setString(2, firstName);
            pst.setDate(3, java.sql.Date.valueOf(birth));
            pst.setString(4, jobTitle);
            pst.setString(5, phone);
            if (pst.executeUpdate() > 0) {
                System.out.println("???? s???a nh??n vi??n" + id);
                return true;
            } else {
                System.out.println("Kh??ng c?? nh??n vi??n c???n s???a!");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Nh???n t???t c??? m??n ??n v??o danh s??ch
    public ResultSet allFoods() {
        ResultSet rs = null;
        String sqlCommand = "select * from " + tableFood;
        Statement st;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(sqlCommand);
        } catch (SQLException e) {
            System.out.println("Select error");
            e.printStackTrace();
        }
        return rs;
    }

    public void getAllFoodsSQL(ResultSet rs) throws SQLException {
        while (rs.next()) {
            foodManagement.addFood(new food(rs.getInt(1), rs.getString(2), rs.getInt(3)));
        }
    }

    //x??a m??n ??n c??
    public static void deleteFood(int maMon) {
        String sqlCommand = "delete from " + tableFood + " where fID = ?";
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlCommand);
            pst.setInt(1, maMon);
            if (pst.executeUpdate() > 0) {
                System.out.println("???? x??a m??n ??n");
            } else {
                System.out.println("Kh??ng c?? m??n ??n c???n x??a!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // s???a m??n ??n
    public static void fixFoodSQL(int id, String name, int price) {
        String sqlCommand = "UPDATE " + tableFood + " SET fName = ?, fPrice = ?  WHERE fID = " + id;
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlCommand);
            pst.setString(1, name);
            pst.setInt(2, price);
            if (pst.executeUpdate() > 0) {
                System.out.println("???? s???a m??n ??n" + id);
            } else {
                System.out.println("Kh??ng c?? m??n ??n c???n s???a!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // th??m m??n ??n
    public static void addNewFoodSQL(food e) {
        String sqlCommand = "insert into " + tableFood + " value( ?, ?, ?)";
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlCommand);
            pst.setInt(1, e.foodId);
            pst.setString(2, e.name);
            pst.setInt(3, e.price);
            if (pst.executeUpdate() > 0) {
                System.out.println("Th??m m??n ??n th??nh c??ng: " + e.foodId);
            } else {
                System.out.println("Ch??a th??? th??m m??n ??n!");
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }
    }
    // Ki???m tra M???t kh???u v?? t??i kho???n
    public static boolean checkID(int id, String pc) throws SQLException {
        ResultSet rs = checkID2(id, pc);
        if (rs == null) return false;
        while (rs.next()) {
            if (Objects.equals(rs.getString(8), pc)) return true;
        }
        return false;
    }

    public static ResultSet checkID2(int id, String pc) {
        ResultSet rs = null;
        String sqlCommand = "select * from " + tableEmployee + " where eID = " + id;
        Statement st;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(sqlCommand);
        } catch (SQLException e) {
            System.out.println("Select error");
            e.printStackTrace();
        }
        return rs;
    }


    public static boolean changePassSQL(int userID, String newPass) {
        ResultSet rs = null;
        String sqlCommand = "UPDATE " + tableEmployee + " SET passCode = ?" + " WHERE eID = ? ";
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlCommand);
            pst.setString(1, newPass);
            pst.setInt(2, userID);
            if (pst.executeUpdate() > 0) {
                return true;
            } else {
                System.out.println("update pass error SQL");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //B???ng
    public ResultSet allTables() {
        ResultSet rs = null;
        String sqlCommand = "select * from " + tableDtable;
        Statement st;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(sqlCommand);
        } catch (SQLException e) {
            System.out.println("Select error");
            e.printStackTrace();
        }
        return rs;
    }

    public void getAllTablesSQL(ResultSet rs) throws SQLException {
        while (rs.next()) {
            List<food> f = new ArrayList<>();
            if (rs.getInt(3) != 0) {
                ResultSet rs1 = getFoodByBillId(rs.getInt(3));
                while (rs1.next()) {
                    f.add(foodManagement.allFood.get(foodManagement.getFoodIdxById(rs1.getInt(1))));
                }
            }
            tableManagement.addTableToList(new table(rs.getInt(1), rs.getInt(3), f));
        }
    }


    public static void addNewTableSQL(int newTableId) {
        ResultSet rs = null;
        String sqlCommand = "INSERT INTO " + tableDtable + " (tId, nChair, note) " +
                "VALUES (?, ?, ?)";
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlCommand);
            pst.setInt(1, newTableId);
            pst.setInt(2, 6);
            pst.setInt(3, 0);
            if (pst.executeUpdate() > 0) {
                System.out.println("th??m b??n: ");
            } else {
                System.out.println("Ch??a th??? th??m b??n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Nh???n v??o t???t c??? h??a ????n
    public ResultSet allBills() {
        ResultSet rs = null;
        String sqlCommand = "select * from " + tableBill;
        Statement st;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(sqlCommand);
        } catch (SQLException e) {
            System.out.println("Select bill error");
            e.printStackTrace();
        }
        return rs;
    }

    public void getAllBillsSQL(ResultSet rs) throws SQLException {
        while (rs.next()) {
            billManagement.addBillToList(SQL.getInfoBill(rs.getInt(1)));
        }
    }


    //th??m h??a ????n m???i v??o b???ng khi t???o h??a ????n m???i ??? billManagement.createNewBill()
    public static void addBilldetails(food x, int bID) {
        ResultSet rs = null;
        String sqlCommand = "INSERT INTO billdetails (fID, fPrice, bID) " +
                "VALUES (?, ?, ?)";
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlCommand);
            pst.setInt(1, x.foodId);
            pst.setInt(2, x.price);
            pst.setInt(3, bID);
            if (pst.executeUpdate() > 0) {
                System.out.println("add success :" + x.getFoodId());
            } else {
                System.out.println("update billdetails error");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addNewBill(bill nBi) {
        ResultSet rs = null;
        String sqlCommand = "INSERT INTO bill (bID, totalMoney, discount, timeIn, eID) " +
                "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlCommand);
            pst.setInt(1, nBi.billId);
            pst.setInt(2, nBi.totalMoney);
            pst.setInt(3, 0);
            pst.setTimestamp(4, java.sql.Timestamp.valueOf(nBi.timeIn));
            pst.setInt(5, nBi.employeeID);
            if (pst.executeUpdate() > 0) {
                System.out.println("update bill success :" + nBi.billId);
            } else {
                System.out.println("update billdetails error");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // C???p nh???t t???i h??a ????n khi g???i th??m m??n

    public static void updateBill(int billId, int sum) {
        ResultSet rs = null;
        String sqlCommand = "UPDATE " + tableBill + " SET totalMoney = ?" + " WHERE bID = ? ";
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlCommand);
            pst.setInt(2, billId);
            pst.setInt(1, sum);
            if (pst.executeUpdate() > 0) {
                System.out.println("???? th??m m??n v??o: " + billId);
            } else {
                System.out.println("Ch??a th??? th??m m??n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Thanh to??n h??a ????n c?? g???i t??? billManagement.payBill
    public static void payBillSQL(int billId, LocalDateTime dateTime) {
        ResultSet rs = null;
        String sqlCommand = "UPDATE " + tableBill + " SET timePayment = ?" + " WHERE bID = ? ";
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlCommand);
            pst.setInt(2, billId);
            pst.setTimestamp(1, java.sql.Timestamp.valueOf(dateTime));
            if (pst.executeUpdate() > 0) {
                System.out.println("pay success :" + billId);
            } else {
                System.out.println("update bill error");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void updateBillOfTableSQL(int tableId, int billID) {
        ResultSet rs = null;
        String sqlCommand = "UPDATE " + tableDtable + " SET note = ? WHERE tId = ? ";
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlCommand);
            pst.setInt(1, billID);
            pst.setInt(2, tableId);
            if (pst.executeUpdate() > 0) {
                System.out.println("update bill of table " + tableId + " : " + billID);
            } else {
                System.out.println("update bill of table error");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // l???y c??c m??n ??n t??? SQl th??ng qua m?? h??a ????n
    public static ResultSet getFoodByBillId(int billId) {
        ResultSet rs = null;
        String sqlCommand = "select * from " + tableBilldetails + " where bID = " + billId;
        Statement st;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(sqlCommand);
        } catch (SQLException e) {
            System.out.println("l???i l???y th??ng tin m??n ??n c???a h??a ????n");
            e.printStackTrace();
        }
        return rs;
    }

    // in th??ng tin h??a ????n qua m?? h??a ????n
    public static ResultSet getInfoBillSQL(int billId) {
        ResultSet rs = null;
        String sqlCommand = "select * from " + tableBill + " where bID = " + billId;
        Statement st;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(sqlCommand);
        } catch (SQLException e) {
            System.out.println("l???i l???y th??ng tin h??a ????n");
            e.printStackTrace();
        }
        return rs;
    }

    public static bill getInfoBill(int billId) throws SQLException {
        ResultSet rs = getInfoBillSQL(billId);
        ResultSet rs1 = getFoodByBillId(billId);
        bill nb = new bill();
        nb.setBillId(billId);
        if (rs == null) return null;
        while (rs.next()) {
            nb.setTotalMoney(rs.getInt(2));
            nb.setTimeIn(rs.getTimestamp(4).toLocalDateTime());
            if (rs.getTimestamp(5) != null)
                nb.setPaymentTime(rs.getTimestamp(5).toLocalDateTime());
            nb.setEmployeeID(rs.getInt(6));
            nb.tableId = rs.getInt(7);
        }
        while (rs1.next()) {
            nb.getFoodToList(rs1.getInt(1));
        }
        return nb;
    }

    //in th??ng tin h??a ????n h??a ????n theo ng??y ???????c ch??? ?????nh
    public static ResultSet getInfoBillByDateSQL(LocalDate date) {
        ResultSet rs = null;
        //Timestamp first = Timestamp.valueOf(date.atStartOfDay());
        //Timestamp second = Timestamp.valueOf((date.plus(Period.ofDays(1))).atStartOfDay());
        String sqlCommand = "select * from " + tableBill + " WHERE date(timeIn) = " + date;//+ " and date(timeIn) <= " + date.plus(Period.ofDays(1));
        Statement st;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(sqlCommand);
        } catch (SQLException e) {
            System.out.println("l???i l???y th??ng tin h??a ????n");
            e.printStackTrace();
        }
        return rs;
    }

    public static List<bill> getInfoBillByDate(LocalDate date) throws SQLException {
        ResultSet rs = getInfoBillByDateSQL(date);
        List<bill> bs = new ArrayList<>();
        System.out.println(rs);
        while (rs.next()) {
            int x = rs.getInt(1);
            System.out.println(x);
            bs.add(getInfoBill(x));
        }
        return bs;
    }


}

class test {
    public static void main(String[] args) throws SQLException {
        SQL myconnect = new SQL();
        //employeeManagement.fixInfoEmployee(1, "Ng?? Lan", "Anh", LocalDate.parse("2001-12-05"),"Qu???n l??", "0937387384");
        //billManagement.createNewBill();
        //billManagement.payBill(8);
        //System.out.println(LocalDate.now());
        //billManagement.printInfoBillByDate(LocalDate.of(2021,12,8));
//        employeeManagement.addEmployee("Mai Chi", "Kim", LocalDate.of(2001, 12, 5)
//        , "qu???n l??", "0946785432");

//        employeeManagement.removeEmployee(4);
        //tableManagement.addNewTable();

    }
}


