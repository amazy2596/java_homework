public class Main {
    public static void main(String[] args) {
        int rows = utility.rand(10, 15), cols = utility.rand(15, 20);
        MazeGenerator mg = new MazeGenerator(rows, cols);
        ui u = new ui(rows, cols);
    }
}