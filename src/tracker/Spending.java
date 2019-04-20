package tracker;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Spending {
    private String amount;
    private Date date;
    private String category;
    private String recordDate;

    // Constructor for querying records
    public Spending(int weekOfMonth){
        date = new Date(weekOfMonth);
    }

    // Constructor used in algorithm for automatically deleting records
    public Spending(String numericDate, int weekOfMonth){
        date = new Date(numericDate, weekOfMonth);
    }

    // Constructor for adding records
    public Spending(String amount, String category){
        this.amount = amount;
        this.category = category;
        date = new Date();
    }

    public void setAmount(String amount){
        this.amount  = amount;
    }

    // getter for tableview display (displaying amount with currency units)
    public String getAmount(){ return "£" + amount; }

    // getter for performing calculations with
    public String getAmount(boolean forCalculation){ return amount; }

    public void setCategory(String category){
        this.category = category;
    }

    public void setRecordDate(String recordDate){
        this.recordDate = recordDate;
    }

    public Date getDate(){
        return date;
    }

    public String getCategory(){
        return category;
    }

    public String getRecordDate(){
        return recordDate;
    }


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

    public static String calculateWeekTotal(ArrayList<Spending> records){
        String currentMonth = new Date().formatMonth();
        String month;
        int weekOfCurrentMonth = new Date().getWeekOfMonth();
        int weekOfMonth;
        Double weekTotal = 0.00;
        for(int i = 0; i < records.size(); i++){
            month = records.get(i).getRecordDate().substring(3, 5);
            weekOfMonth = records.get(i).getDate().getWeekOfMonth();
            if(month.equals(currentMonth) && weekOfMonth == weekOfCurrentMonth){
                weekTotal += Double.valueOf(records.get(i).getAmount(true));
            }
        }
        return String.valueOf(weekTotal);
    }

    public static double[] categoryTotals(ArrayList<Spending> records){
        double[] totals = new double[6];
        String currentMonth = new Date().formatMonth();
        String month;
        for(int i = 0; i < records.size(); i++){
            month = records.get(i).getRecordDate().substring(3, 5);
            if(month.equals(currentMonth)){
                if(records.get(i).getCategory().equals("FOOD")){
                    totals[0] += Double.valueOf(records.get(i).getAmount(true));

                } else if(records.get(i).getCategory().equals("LEISURE")){
                    totals[1] += Double.valueOf(records.get(i).getAmount(true));

                } else if(records.get(i).getCategory().equals("TRANSPORT")){
                    totals[2] += Double.valueOf(records.get(i).getAmount(true));

                } else if(records.get(i).getCategory().equals("CLOTHING")){
                    totals[3] += Double.valueOf(records.get(i).getAmount(true));

                } else if(records.get(i).getCategory().equals("HOUSING")){
                    totals[4] += Double.valueOf(records.get(i).getAmount(true));

                } else if(records.get(i).getCategory().equals("MISC.")){
                    totals[5] += Double.valueOf(records.get(i).getAmount(true));
                }
            }
        }
        return totals;
    }

    public static String[] categoryPercents(double[] categoryTotal, double monthTotal){
        DecimalFormat df = new DecimalFormat("#0");
        String[] result = new String[6];
        for(int i = 0; i < categoryTotal.length ; i++){
            result[i] = df.format((categoryTotal[i]/monthTotal) * 100);
        }
        return result;
    }
}

