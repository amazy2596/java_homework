package java_homework.src;

public class Main {
    public static void main(String[] args) {
        int rows = utility.rand(5, 10), cols = utility.rand(5, 10);
        MazeGenerator mg = new MazeGenerator(rows, cols);
        ui u = new ui(rows, cols);
    }
}