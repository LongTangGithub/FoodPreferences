package com.example.foodpreferences;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;   // Hold multiple data in several formats
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import java.util.HashMap;
import java.util.Map;

/**
 * This controller is responsible for managing the food preference user interface,
 * including a table of student food preferences and a pie chart visualization of the data.
 */
public class Controller {

    @FXML
    private TableView<StudentFood> tableView;
    @FXML
    private TableColumn<StudentFood, String> studentColumn;
    @FXML
    private TableColumn<StudentFood, String> foodColumn;

    @FXML
    private TextField studentNameField;
    @FXML
    private TextField foodTypeField;

    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private PieChart foodPieChart;

    // Observable list of data for the table
    private final ObservableList<StudentFood> data = FXCollections.observableArrayList();

    // Map to store the count of each food type
    private final Map<String, Integer> foodCount = new HashMap<>();

    public void initialize() {
        // Bind the student and food columns to the appropriate properties of StudentFood
        studentColumn.setCellValueFactory(cell -> cell.getValue().studentNameProperty());
        foodColumn.setCellValueFactory(cell -> cell.getValue().foodTypeProperty());

        // Set the table view data to the observable list
        tableView.setItems(data);

        // Configure drag and drop functionality
        setupDragAndDrop();

        // Set the action for the add button to add data to the table
        addButton.setOnAction(e -> {
            String studentName = studentNameField.getText().trim();
            String foodType = foodTypeField.getText().trim();

            // Add a new StudentFood entry if both fields are not empty
            if (!studentName.isEmpty() && !foodType.isEmpty()) {
                data.add(new StudentFood(studentName, foodType));
                studentNameField.clear();
                foodTypeField.clear();
//                updatePieChart();     ---> Uncomment this and the piechart automatically updates
            }
        });

        // Set the action for the delete button to remove the selected item and update the pie chart
        deleteButton.setOnAction(e -> {
            StudentFood selectedItem = tableView.getSelectionModel().getSelectedItem();
            data.remove(selectedItem);
            updatePieChart();
        });
    }

    /**
     * Configures drag-and-drop functionality for the table and pie chart.
     */
    private void setupDragAndDrop() {
        // Set a row factory for the table view to enable drag detection on each row
        tableView.setRowFactory(tv -> {
            TableRow<StudentFood> row = new TableRow<>();

            // Detect when a row is dragged
            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(row.getItem().getFoodType());
                    db.setContent(cc);
                    event.consume();
                }
            });

            return row;
        });
        // Accept dragged content over the pie chart
        foodPieChart.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        // Handle the dropped content on the pie chart
        foodPieChart.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String foodItem = db.getString();

                // Increase the count of the dragged food item and update the pie chart
                foodCount.put(foodItem, foodCount.getOrDefault(foodItem, 0) + 1);
                updatePieChart();
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Updates the pie chart to reflect the current data in the table.
     */
    private void updatePieChart() {
        // Clear current food counts
        foodCount.clear();
        // Count each food type in the table data
        for (StudentFood entry : data) {
            foodCount.put(entry.getFoodType(), foodCount.getOrDefault(entry.getFoodType(), 0) + 1);
        }

        // Convert the food counts to pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : foodCount.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        // Update pie chart data
        foodPieChart.setData(pieChartData);

        // Set labels visible
        foodPieChart.setLabelsVisible(true);

        int totalItems = data.size();

        // Configure the pie chart to show the percentage and label
        for (PieChart.Data data : foodPieChart.getData()) {
            double percentage = (data.getPieValue() / totalItems) * 100;
            String text = String.format("%s %.1f%%", data.getName(), percentage);
            data.nameProperty().bindBidirectional(new SimpleStringProperty(text));
        }
    }

}


