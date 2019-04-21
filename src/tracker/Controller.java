package tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Label dateDisplay;

    @FXML
    private Label weekTotal;

    @FXML
    private Label monthTotal;

    @FXML
    private TextField userInput;

    @FXML
    private TableView spendingTable;

    @FXML
    private TableColumn<Spending, String> dateColumn;

    @FXML
    private TableColumn<Spending, String> amountColumn;

    @FXML
    private TableColumn<Spending, String> categoryColumn;

    @FXML
    private Button enterBtn;

    @FXML
    private Button foodBtn;

    @FXML
    private Button leisureBtn;

    @FXML
    private Button transportBtn;

    @FXML
    private Button clothingBtn;

    @FXML
    private Button housingBtn;

    @FXML
    private Button miscBtn;

    @FXML
    private PieChart pieChart;

    @FXML
    private MenuItem deleteOption;

    @FXML
    private Label foodTotalLabel;

    @FXML
    private Label leisureTotalLabel;

    @FXML
    private Label housingTotalLabel;

    @FXML
    private Label transportTotalLabel;

    @FXML
    private Label miscTotalLabel;

    @FXML
    private Label clothingTotalLabel;

    private static String category;

    private static Datasource data;

    private static ArrayList<Spending> records;

    private static double[] categoryTotals;

    /**
     * Initializes app by displaying date and calling initializeApp() method.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateDisplay.setText(new Date().stringDate());
        data = new Datasource();
        initializeApp();
        userInput.setContextMenu(new ContextMenu()); // disables default context menu
    }

    /**
     * Initializes the app to reflect any user created changes.
     * For example, the app will be re-initialized after the user adds a record to the database.
     */
    public void initializeApp(){
        data.deleteOlderRecords();
        records = data.querySpending();
        categoryTotals = Spending.categoryTotals(records);
        displaySpendingInfo();
        loadPieChart(categoryTotals);
    }

    /**
     * Handles user clicking any of the button in the UI.
     * @param event The action event (left-click)
     */
    @FXML
    public void onButtonClick(ActionEvent event){
        String newStyle = "-fx-background-color: green; -fx-text-fill: white";
        if(event.getSource() == foodBtn){
            category = "FOOD";
            revertButtonStyles();
            foodBtn.setStyle(newStyle);
        } else if(event.getSource() == leisureBtn){
            category = "LEISURE";
            revertButtonStyles();
            leisureBtn.setStyle(newStyle);
        } else if(event.getSource() == transportBtn){
            category = "TRANSPORT";
            revertButtonStyles();
            transportBtn.setStyle(newStyle);
        } else if(event.getSource() == clothingBtn){
            category = "CLOTHING";
            revertButtonStyles();
            clothingBtn.setStyle(newStyle);
        } else if(event.getSource() == housingBtn){
            category = "HOUSING";
            revertButtonStyles();
            housingBtn.setStyle(newStyle);
        } else if(event.getSource() == miscBtn){
            category = "MISC.";
            revertButtonStyles();
            miscBtn.setStyle(newStyle);
        }
        // inserts record and updates pie chart display
        if(event.getSource() == enterBtn && category != null){
            insertRecord();
            initializeApp();
            revertButtonStyles();
        }
    }

    /**
     * Reverts category buttons back to their original style: lightgray background and black text fill.
     */
    public void revertButtonStyles(){
        String style = "-fx-background-color: lightgray; -fx-text-fill: black";
        foodBtn.setStyle(style);
        leisureBtn.setStyle(style);
        transportBtn.setStyle(style);
        clothingBtn.setStyle(style);
        housingBtn.setStyle(style);
        miscBtn.setStyle(style);
    }

    /**
     * Handles deleting record when TableView 'Delete' menu item is left-clicked.
     * @param event The action event that triggers the method (clicking 'Delete' menu item)
     */
    @FXML
    public void onMenuItemClick(ActionEvent event){
        if(event.getSource() == deleteOption){
            int tableRow;
            // gets the row of the currently clicked on entry in table-view
            tableRow = spendingTable.getSelectionModel().selectedIndexProperty().get() + 1;//+1 as IDs begin at 1 in BD
            deleteRecord(tableRow);
            initializeApp();
        }
    }

    /**
     * Deletes a single row in database table.
     * @param _id The ID of the record entry to be deleted from database spending table.
     */
    public void deleteRecord(int _id){
        data.deleteRecord(_id);
    }

    /**
     * Inserts a single record into database spending table based on user input into text field.
     * The inputted by use must be below £100,000 or the record will not be added.
     * To be inserted, the amount inputted must also keep total spending below the max of £100,000.
     */
    public void insertRecord(){
        double total = Double.valueOf(Spending.calculateMonthTotal(records));
        String input = userInput.getText();
        if(!input.equals("")){
            try{
                double inputValue = Double.parseDouble(input);
                if(inputValue > 0 && inputValue <= 100_000 && total + inputValue <= 100_000){
                    data.insertRecord(new Spending(String.format("%.2f", inputValue), category));
                    displaySpendingInfo();
                    category = null; // to wait for user's next choice
                    userInput.setText("");
                } else {
                    userInput.setText("INVALID INPUT");
                }
            } catch(NumberFormatException e){
                System.out.println("ERROR INSERTING RECORD: Input '" + input + "' is invalid");
            }
        }
    }

    /**
     * Displays spending records and summary statistics of spending to the user. The summary statistics are:
     * Total week spending, total month spending, total month spending for each category.
     */
    public void displaySpendingInfo(){
        DecimalFormat df = new DecimalFormat("###,##0.00");
        weekTotal.setText("£" + df.format(Double.valueOf(Spending.calculateWeekTotal(records))));
        monthTotal.setText("£" + df.format(Double.valueOf(Spending.calculateMonthTotal(records))));

        foodTotalLabel.setText("FOOD: £" +  df.format(categoryTotals[0]));
        leisureTotalLabel.setText("LEISURE: £" +  df.format(categoryTotals[1]));
        transportTotalLabel.setText("TRANSPORT: £" +  df.format(categoryTotals[2]));
        clothingTotalLabel.setText("CLOTHING: £" +  df.format(categoryTotals[3]));
        housingTotalLabel.setText("HOUSING: £" +  df.format(categoryTotals[4]));
        miscTotalLabel.setText("MISC: £" +  df.format(categoryTotals[5]));

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("recordDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        spendingTable.getItems().setAll(fetchRecords(records));
    }

    /**
     * Creates ObservableList of records which will be passed to TableView for display.
     * @param records Database spending table records
     * @return ObservableList of all spending records
     */
    public ObservableList<Spending> fetchRecords(ArrayList<Spending> records){
        ObservableList<Spending> spending = FXCollections.observableArrayList();
        spending.addAll(records);
        return spending;
    }


    /**
     * Feeds pie chart data into pie chart.
     * @param categoryTotal The total amount spent on each category for the current month
     */
    public void loadPieChart(double[] categoryTotal){
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        String[] category = {"FOOD", "LEISURE", "TRANSPORT", "CLOTHING", "HOUSING", "MISC."};
        double monthTotal = Double.valueOf(Spending.calculateMonthTotal(records));
        String[] categoryPercent = Spending.categoryPercents(categoryTotal, monthTotal);

        for(int i = 0; i < categoryTotal.length; i++){
            if(categoryTotal[i] > 0){
                pieChartData.add(new PieChart.Data(category[i] + " (" + categoryPercent[i] + "%)", categoryTotal[i]));
            }
        }
        pieChart.setData(pieChartData);
        pieChart.setLegendVisible(false);
    }
}

