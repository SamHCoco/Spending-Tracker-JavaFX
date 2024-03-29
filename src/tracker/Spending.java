package tracker;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Spending {
    private String amount;
    private Date date;
    private String category;
    private String recordDate;

    /**
     * Constructor for querying records.
     * @param weekOfMonth The week of the month the record entry was created
     */
    public Spending(int weekOfMonth){
        date = new Date(weekOfMonth);
    }

    /**
     * Constructor used in algorithm for automatically deleting records.
     * @param numericDate The date of a spending table entry in numeric format: ##-##-##
     * @param weekOfMonth The week of the month the record entry was created
     */
    public Spending(String numericDate, int weekOfMonth){
        date = new Date(numericDate, weekOfMonth);
    }

    /**
     * Constructor for adding records
     * @param amount The amount spent
     * @param category One of the six categories the spending falls under
     */
    public Spending(String amount, String category){
        this.amount = amount;
        this.category = category;
        date = new Date();
    }

    /**
     * Setter for querying records in database spending table.
     * @param amount the amount spent in a single spending table entry
     */
    public void setAmount(String amount){
        this.amount  = amount;
    }

    /**
     * Getter for displaying amount, with currency £ unit, in table-view.
     * @return The amount spent in a single record entry, with £ currency unit
     */
    public String getAmount(){ return "£" + amount; }

    /**
     * Getter for performing numerical calculations and inserting records.
     * @param forCalculation Overloading parameter, set to true for calculations
     * @return The amount spent as a string
     */
    public String getAmount(boolean forCalculation){ return amount; }

    /**
     * Setter for setting category of spending whe inserting records
     * @param category The category the spending falls under
     */
    public void setCategory(String category){
        this.category = category;
    }

    /**
     * Setter for setting record date to display in table-view via ObserveableList
     * @param recordDate the numeric date of the record entry, in format: DD-MM-YY
     */
    public void setRecordDate(String recordDate){
        this.recordDate = recordDate;
    }

    /**
     * Getter for date of a record entry. Used for performing calculations and
     * inserting and deleting records into database spending table.
     * @return The date of the record entry
     */
    public Date getDate(){
        return date;
    }

    /**
     * Getter for category of a spending record entry.
     * @return One of the six categories the spending falls under
     */
    public String getCategory(){
        return category;
    }

    /**
     * Getter for date of a record entry. Used to display records in table-view.
     * @return The date of the record entry
     */
    public String getRecordDate(){
        return recordDate;
    }


    /**
     * Calculates the total amount spent in the current month.
     * @param records Database spending table records
     * @return The total amount spent in the current month
     */
    public static String calculateMonthTotal(ArrayList<Spending> records){
        String currentMonth = new Date().formatMonth();
        String month;
        Double monthSpendingTotal = 0.00;
        for(int i = 0; i < records.size(); i++){
            month = records.get(i).getRecordDate().substring(3, 5);
            if(currentMonth.equals(month)){
                monthSpendingTotal += Double.valueOf(records.get(i).getAmount(true));
            }
        }
        return String.valueOf(monthSpendingTotal);
    }

    /**
     * Calculates the total amount spent in the current week.
     * @param records Database spending table records
     * @return The total amount spent in the current week
     */
    public static String calculateWeekTotal(ArrayList<Spending> records){
        int recordMonth;
        int recordWeekOfMonth;
        int weekOfCurrentMonth = new Date().getWeekOfMonth();
        int priorMonth;
        int currentMonth = Integer.valueOf(new Date().formatMonth());
        if(currentMonth == 1){
            priorMonth = 12;
        } else {
            priorMonth = currentMonth - 1;
        }
        Double weekTotal = 0.00;
        for(int i = 0; i < records.size(); i++){
            recordMonth = Integer.valueOf(records.get(i).getRecordDate().substring(3, 5));
            recordWeekOfMonth = records.get(i).getDate().getWeekOfMonth();
            if(recordMonth == currentMonth){
                if(recordWeekOfMonth == weekOfCurrentMonth){
                    weekTotal += Double.valueOf(records.get(i).getAmount(true));
                }
            }else if(recordMonth == priorMonth){
                if(weekOfCurrentMonth == 1 && recordWeekOfMonth == 5){
                    weekTotal += Double.valueOf(records.get(i).getAmount(true));
                }
            }
        }
        return String.valueOf(weekTotal);
    }

    /**
     * Calculates the total amount spent on each of the 6 spending categories.
     * @param records Database spending table records
     * @return The total amount spent on each spending category
     */
    public static double[] categoryTotals(ArrayList<Spending> records){
        double[] totals = new double[6];
        String currentMonth = new Date().formatMonth();
        String month;
        for(int i = 0; i < records.size(); i++){
            month = records.get(i).getRecordDate().substring(3, 5);
            if(month.equals(currentMonth)){
                String category = records.get(i).getCategory();
                double amount = Double.valueOf(records.get(i).getAmount(true));

                if(category.equals("FOOD")){
                    totals[0] += amount;

                } else if(category.equals("LEISURE")){
                    totals[1] += amount;

                } else if(category.equals("TRANSPORT")){
                    totals[2] += amount;

                } else if(category.equals("CLOTHING")){
                    totals[3] += amount;

                } else if(category.equals("HOUSING")){
                    totals[4] += amount;

                } else if(category.equals("MISC.")){
                    totals[5] += amount;
                }
            }
        }
        return totals;
    }

    /**
     * Calculates how much was spent on each category, in the current month, as a percentage
     * of total spending in the current month.
     * @param categoryTotals The total amount spent on each category for the current month.
     * @param monthTotal The aggregate total spending (all categories) for the month
     * @return The amounts spent on each category, as a percentage of total spending
     */
    public static String[] categoryPercents(double[] categoryTotals, double monthTotal){
        DecimalFormat df1 = new DecimalFormat("#0.0");
        DecimalFormat df2 = new DecimalFormat("#0");
        df1.setRoundingMode(RoundingMode.HALF_UP);
        String formattedResult;
        double percent;
        String[] result = new String[6];
        for(int i = 0; i < categoryTotals.length ; i++){
            percent = (categoryTotals[i]/monthTotal) * 100;
            formattedResult = df2.format(Double.valueOf(df1.format(percent)));
            result[i] = formattedResult;
        }
        return result;
    }
}
