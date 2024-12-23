public class Main {
    public static void main(String[] args) {
        int rows = utility.rand(6, 9), cols = utility.rand(12, 15);
        MazeGenerator mg = new MazeGenerator(rows, cols);

        Controller.bulletController = new BulletController();
        Controller.tank1 = new TankController(rows, cols, 1);
        // Controller.tank2 = new TankController(rows, cols, 2);

        UI ui = new UI(rows, cols);
    }
}