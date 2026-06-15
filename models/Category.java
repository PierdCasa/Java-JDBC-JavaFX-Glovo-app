package models;

public class Category implements Comparable<Category> {
    private static int idCounter = 0;

    private int categoryId;
    private String name;

    public Category(String name) {
        this.categoryId = ++idCounter;
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Category other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return "Category{"+ "categoryId=" + categoryId +
                ", name='" + name + '\'' +'}';
    }
}
