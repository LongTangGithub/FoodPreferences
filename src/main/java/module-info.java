module com.example.foodpreferences {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.foodpreferences to javafx.fxml;
    exports com.example.foodpreferences;
}