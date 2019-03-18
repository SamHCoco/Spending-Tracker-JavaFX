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
    private ContextMenu editTable;

    @FXML
    private MenuItem editOption;

    @FXML
    private MenuItem deleteOption;

    private static int tableRow;

    private static String category;

    private static Datasource data;

    private static ArrayList<Spending> records;


   // Runs when program first initialized
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateDisplay.setText(new Date().stringDate());
        data = new Datasource();
        initializeApp();
    }

    public void initializeApp(){
        records = data.querySpending();
        displaySpendingInfo();
        loadPieChart(Spending.categoryTotals(records));
    }

    /*
    creating observable list which will be passed to list view to display in TableView
     */
    public ObservableList<Spending> fetchRecords(ArrayList<Spending> records){
        ObservableList<Spending> spending = FXCollections.observableArrayList();
        spending.addAll(records);
        return spending;
    }

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

    @FXML
    public void onMenuItemClick(ActionEvent event){
        if(event.getSource() == editOption){
            System.out.println("EDIT IN PROGRESS");
        } else if(event.getSource() == deleteOption){
            System.out.println("DELETE IN PROGRESS"); // FOR DEBUGGING TO BE DELETED
            // gets the row of the currently clicked on entry in table-view
            tableRow = spendingTable.getSelectionModel().selectedIndexProperty().get() + 1;//+1 as IDs begin at 1 in SQL
            System.out.println("tableRow: " + tableRow);
            deleteRecord(tableRow);
            initializeApp();
        }
    }

    public void deleteRecord(int _id){
        data.deleteRecord(_id);
    }

    public void insertRecord(){
        String input = userInput.getText();
        if(!input.equals("")){
            try{
                double inputValue = Double.parseDouble(input);
                if(inputValue > 0){
                    data.insertRecord(new Spending(String.format("%.2f", inputValue), category));
                    displaySpendingInfo();
                    category = null; // to wait for users next choice
                }
            } catch(NumberFormatException e){
                System.out.println("ERROR INSERTING RECORD:");
                e.printStackTrace();
            }
        }
    }

    public void displaySpendingInfo(){
        weekTotal.setText("£" + Spending.calculateWeekTotal(records));
        monthTotal.setText("£" + Spending.calculateMonthTotal(records));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("recordDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        spendingTable.getItems().setAll(fetchRecords(records));
    }

    public void loadPieChart(double[] categoryTotal){
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        String[] category = {"FOOD", "LEISURE", "TRANSPORT", "CLOTHING", "HOUSING", "MISC."};
        double monthTotal = Double.valueOf(Spending.calculateWeekTotal(records));
        String[] categoryPercent = Spending.categoryPercents(categoryTotal, monthTotal);

        for(int i = 0; i < categoryTotal.length; i++){
            if(categoryTotal[i] > 0){
                pieChartData.add(
                        new PieChart.Data(category[i] + " (" + categoryPercent[i] + "%)", categoryTotal[i])
                );
            }
        }
        pieChart.setData(pieChartData);
        pieChart.setLabelsVisible(true);

    }

}
