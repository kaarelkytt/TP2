package org.example.tp.dataobjects;

import javafx.scene.image.Image;

public class Exercise {
    private final int id;
    private final String name;
    private final Category category;
    private final Image image;

    public enum Category {
        ABS,
        BACK,
        BICEPS,
        TRICEPS,
        CHEST,
        SHOULDERS,
        LEGS
    }

    // TODO correct image path in file

    public Exercise(int id, String name, Category category, String imagePath) {
        this.id = id;
        this.name = name;
        this.category = category;
        System.out.println("src\\main\\resources\\org\\example\\tp\\" + imagePath);
        this.image = new Image("org/example/tp/" + imagePath);
        //src/main/resources/org/example/tp/pics
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
        return image;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", image='" + image + '\'' +
                '}';
    }
}
