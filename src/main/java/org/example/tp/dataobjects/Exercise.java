package org.example.tp.dataobjects;

import javafx.scene.image.Image;

import javax.persistence.*;

@Entity
public class Exercise {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private Category category;
    private String imagePath;

    public enum Category {
        ABS,
        BACK,
        BICEPS,
        TRICEPS,
        CHEST,
        SHOULDERS,
        LEGS
    }

    public Exercise() {
    }

    public Exercise(int id, String name, Category category, String imagePath) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imagePath = imagePath;
    }

    public Exercise(int id, String name, String category, String imagePath) {
        this(id, name, Category.valueOf(category), imagePath);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public Image getImage() {
        return new Image("file:src/main/resources/org/example/tp/" + imagePath);
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", image='" + imagePath + '\'' +
                '}';
    }
}
