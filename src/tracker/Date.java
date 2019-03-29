package tracker;

import java.util.Calendar;

public class Date {
    private int day;
    private int date;
    private int month;
    private int year;
    private int weekOfMonth;
    private String numericDate;

    public Date(int weekOfMonth){
        this.weekOfMonth = weekOfMonth;
    }

    // constructor used in algorithm for automatically deleting records
    public Date(String numericDate, int weekOfMonth){
        this.numericDate = numericDate;
        this.weekOfMonth = weekOfMonth;
    }

    public Date(){
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_WEEK);
        date = calendar.get(Calendar.DATE);
        month = calendar.get(Calendar.MONTH) + 1; // Jan = 0
        year = calendar.get(Calendar.YEAR);
        weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
    }


    /**
     * Get the week of the current month.
     * @return week (1 - 4) of current month
     */
    public int getWeekOfMonth(){
        return weekOfMonth;
    }

    /**
     * Ensures single digit months have 0# format.
     * @return a single digit month in 0# format, e.g. 08
     */
    public String formatMonth(){
        String month = String.valueOf(this.month);
        if(month.length() == 1){
            month = "0" + month;
        }
        return month;
    }

    /**
     * Ensures single digit dates have 0# format.
     * @return  a single digit date in 0# format, e.g. 03 etc
     */
    public String formatDate(){
        String date = String.valueOf(this.date);
        if(date.length() == 1){
            date = "0" + date;
        }
        return date;
    }

    /**
     * Get the current date in a numerical format.
     * @return current date as a string, in numerical format, e.g 03-04-2019
     */
    public String numericDate(){
        return date + "-" +  formatMonth() + "-" + year;
    }

    /**
     * String slices numeric date (e.g. 20-06-17) to extract the month
     * @return Month of a numeric date (e.g. 6 if numericDate = 20-06-17)
     */
    public Integer numericDateMonth(){
        if(numericDate.substring(3).equals("0")){
            return Integer.valueOf(numericDate.substring(4));
        } else {
            return Integer.valueOf(numericDate.substring(3,5));
        }
    }

    /**
     * Get the current date in format:  Day of week/ date/ month/ year.
     * @return current date in non-numeric format e.g. Monday 25 March 2018
     */
    public String stringDate(){
        String day = ""; // empty string to avoid initialization error
        String month = ""; // empty string to avoid initialization error
        switch(this.day){
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }

        // sets month as string
        switch (this.month){
            case 1:
                month = "January";
                break;
            case 2:
                month = "February";
                break;
            case 3:
                month = "March";
                break;
            case 4:
                month = "April";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "June";
                break;
            case 7:
                month = "July";
                break;
            case 8:
                month = "August";
                break;
            case 9:
                month = "September";
                break;
            case 10:
                month = "October";
                break;
            case 11:
                month = "November";
                break;
            case 12:
                month = "December";
                break;
        }
        return day + " " + formatDate() + " " + month + " " + String.valueOf(year);
    }
}
