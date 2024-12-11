public class Main {
    public static void main(String[] args) {
        int rows = utility.rand(6, 9), cols = utility.rand(12, 15);
        MazeGenerator mg = new MazeGenerator(rows, cols);
        UI ui = new UI(rows, cols);
    }
}