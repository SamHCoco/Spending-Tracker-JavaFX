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


   // runs when program first initialized
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateDisplay.setText(new Date().stringDate());
        data = new Datasource();
        initializeApp();
    }

    // re-initializes the application to reflect user created changes
    public void initializeApp(){
        data.deleteOlderRecords();
        records = data.querySpending();
        categoryTotals = Spending.categoryTotals(records);
        displaySpendingInfo();
        loadPieChart(categoryTotals);
    }

    // creates observable list which will be passed to list view to display in TableView
    public ObservableList<Spending> fetchRecords(ArrayList<Spending> records){
        ObservableList<Spending> spending = FXCollections.observableArrayList();
        spending.addAll(records);
        return spending;
    }

    // handles clicking buttons of user interface
    @FXML
    public void onButtonClick(ActionEvent event){
        if(event.getSource() == foodBtn){
            category = "FOOD";
        } else if(event.getSource() == leisureBtn){
            category = "LEISURE";
        } else if(event.getSource() == transportBtn){
            category = "TRANSPORT";
        } else if(event.getSource() == clothingBtn){
            category = "CLOTHING";
        } else if(event.getSource() == housingBtn){
            category = "HOUSING";
        } else if(event.getSource() == miscBtn){
            category = "MISC.";
        }
        // inserts record and updates pie chart display
        if(event.getSource() == enterBtn && category != null){
            insertRecord();
            initializeApp();
        }
    }

    // handles deleting record when clicking TableView 'Delete' menu item
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

    // deletes a single row in database table
    public void deleteRecord(int _id){
        data.deleteRecord(_id);
    }

    public void insertRecord(){
        double total = Double.valueOf(Spending.calculateMonthTotal(records));
        String input = userInput.getText();
        if(!input.equals("")){
            try{
                double inputValue = Double.parseDouble(input);
                if(inputValue > 0 && inputValue <= 100_000 && total + inputValue <= 100_000){
                    data.insertRecord(new Spending(String.format("%.2f", inputValue), category));
                    displaySpendingInfo();
                    category = null; // to wait for users next choice
                } else {
                    userInput.setText("INVALID INPUT");
                }
            } catch(NumberFormatException e){
                System.out.println("ERROR INSERTING RECORD:");
                e.printStackTrace();
            }
        }
    }

    // displays summary statistics of spending to the user
    public void displaySpendingInfo(){
        DecimalFormat df = new DecimalFormat("###,##0.00");
        weekTotal.setText("£" + df.format(Double.valueOf(Spending.calculateWeekTotal(records))));
        monthTotal.setText("£" + df.format(Double.valueOf(Spending.calculateWeekTotal(records))));

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

    // feeds pie chart data into pie chart
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
