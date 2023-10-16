package com.example.foodpreferences;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StudentFood {
    private final StringProperty studentName;
    private final StringProperty foodType;

    public StudentFood(String studentName, String foodType) {
        this.studentName = new SimpleStringProperty(studentName);
        this.foodType = new SimpleStringProperty(foodType);
    }

    public StringProperty studentNameProperty() {
        return studentName;
    }

    public String getFoodType() {
        return foodType.get();
    }



    public StringProperty foodTypeProperty() {
        return foodType;
    }
}
