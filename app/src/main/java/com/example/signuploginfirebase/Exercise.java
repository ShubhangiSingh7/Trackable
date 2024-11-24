package com.example.signuploginfirebase;

import java.util.List;

public class Exercise {
    private String name;
    private String type;
    private List<String> muscles;  // Use List<String> instead of String[]
    private String equipment;
    private String difficulty;
    private String instructions;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getMuscles() {
        return (List<String>) muscles;
    }

    public void setMuscles(List<String> muscles) {
        this.muscles = muscles;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
