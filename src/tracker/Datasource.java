package tracker;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class Datasource {
    private Connection connection;

    private static final String DB_NAME = "spending_record.db";
    private static final String CONNECTION = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\" + DB_NAME;
    private static final String TABLE_NAME = "spending";
    private static final String ID_COLUMN = "_id";
    private static final String DATE_COLUMN = "date";
    private static final String MONTH_WEEK_COLUMN = "month_week";
    private static final String AMOUNT_COLUMN = "amount";
    private static final String CATEGORY_COLUMN = "category";

    /**
     * Constructor for datasource. Creates 'spending' table.
     */
    public Datasource(){
        createTable();
    }


    /**
     * Creates a connection to the database in the directory specified
     * in the 'CONNECTION' variable, which is set to the working directory
     * of the program.
     */
    public void openDatabase(){
        try{
            connection = DriverManager.getConnection(CONNECTION);
        } catch(SQLException e){
            System.out.println("ERROR OPENING DATABASE: " + e.getMessage());
        }
    }

    /**
     * Creates a 'spending' table in the database if one does not exist, with five columns.
     *  Table columns: ID, Date (the current date), Week of the Month, Amount (the amount, in £, spent)
     *  and Category (the category the spending falls under).
     */
    public void createTable(){
        openDatabase();
        try(Statement statement = connection.createStatement()){
            // CREATE TABLE IF NOT EXISTS
            // spending(_id INTEGER PRIMARY KEY, date TEXT, month_week INTEGER, amount TEXT, category TEXT)
            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + ID_COLUMN +
                    " INTEGER PRIMARY KEY, " + DATE_COLUMN + " TEXT, " + MONTH_WEEK_COLUMN + " INTEGER, " + AMOUNT_COLUMN
                    + " TEXT, " + CATEGORY_COLUMN + " TEXT)");
        } catch(SQLException e){
            System.out.println("ERROR CREATING TABLE " + e.getMessage());
        }
    }

    /**
     * Inserts a single record into the database spending table.
     * @param record The record to be inserted, which is represented as a single Spending object.
     */
    public void insertRecord(Spending record){
        openDatabase();
        try(Statement statement = connection.createStatement()){
            // INSERT INTO spending(date, month_week, amount, category) VALUES('date', month_week, "£#.##", 'category')
            statement.execute("INSERT INTO " + TABLE_NAME + "(" + DATE_COLUMN + ", " + MONTH_WEEK_COLUMN + ", "
                + AMOUNT_COLUMN + ", " + CATEGORY_COLUMN + ") VALUES('" + record.getDate().numericDate() + "', " +
                record.getDate().getWeekOfMonth() + ", '" + record.getAmount(true) + "', '" + record.getCategory() + "')");
        } catch(SQLException e){
            System.out.println("ERROR INSERTING RECORD: " + e.getMessage());
        }
    }

    /**
     * Deletes a single record in the database spending table, specified by the _id parameter
     * @param _id The ID of the record to be deleted.
     */
    public void deleteRecord(int _id){
        openDatabase();
        try(Statement statement = connection.createStatement()){
            statement.execute("DELETE FROM " + TABLE_NAME + " WHERE " + ID_COLUMN + "=" + _id);

            // updates records so all IDs are +1 from their preceding ID (important for deleting records in TableView)
            ResultSet records = statement.executeQuery("SELECT * " + " FROM " + TABLE_NAME);
            ArrayList<Integer> oldIDs = new ArrayList<>();
            while(records.next()){
                oldIDs.add(records.getInt(ID_COLUMN));
            }
            for(int i=1; i < oldIDs.size()+1; i++) {
                statement.execute("UPDATE " + TABLE_NAME + " SET " + ID_COLUMN + "=" + i
                                    + " WHERE " + ID_COLUMN + "=" + oldIDs.get(i-1));
            }

        } catch(SQLException e){
            System.out.println("ERROR DELETING RECORD: SQL EXCEPTION");
        }
    }

    /**
     * Queries the database for records and returns all records as an ArrayList of Spending objects.
     * Each spending object is a single entry of the database.
     * @return An ArrayList of all records in the database.
     */
    public ArrayList<Spending> querySpending(){
        ArrayList<Spending> spendings = new ArrayList<>();
        openDatabase();

        try(Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM " + TABLE_NAME)){

            while(result.next()){
                Spending spent = new Spending(result.getInt(MONTH_WEEK_COLUMN));
                spent.setRecordDate(result.getString(DATE_COLUMN));
                spent.setAmount(result.getString(AMOUNT_COLUMN));
                spent.setCategory(result.getString(CATEGORY_COLUMN));
                spendings.add(spent);
            }
            result.close();
            return spendings;
        } catch(SQLException e){
            System.out.println("ERROR QUERYING RECORDS" + e.getMessage());
            return null;
        }
    }

    /**
     * Deletes all spending records up to any records created in the last week of
     * the previous month. For example, if the current month is August the method will
     * deletes all records prior to the last week of July.
     */
    public void deleteOlderRecords(){
        openDatabase();
        try(Statement statement = connection.createStatement()){
            ArrayList<Integer> deleteIDs = new ArrayList<>(); // IDs of records to be deleted
            ResultSet records = statement.executeQuery("SELECT * FROM " + TABLE_NAME);
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH) + 1; // + 1 since Jan = 0
            int recordMonth;
            int recordWeekOfMonth;

            // stores the IDs of records to be deleted from database
            while(records.next()){
              Spending spent = new Spending(records.getString(DATE_COLUMN), records.getInt(MONTH_WEEK_COLUMN));
              recordMonth =  spent.getDate().numericDateMonth();
              recordWeekOfMonth = spent.getDate().getWeekOfMonth();
              if(recordMonth < currentMonth && recordWeekOfMonth <= 3){
                  deleteIDs.add(records.getInt(ID_COLUMN));
              }
            }
            // deletes old records
            for(int i = 0; i < deleteIDs.size(); i++){
                deleteRecord(deleteIDs.get(i));
            }
        } catch(SQLException e){
            System.out.println("ERROR DELETING OLD RECORDS");
            e.getMessage();
        }
    }
}
