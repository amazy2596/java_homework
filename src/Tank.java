public class Tank {
    double centerX, centerY;
    int row, col;
    double angle;

    double speed = 2;
    double per = 4;

    int width = 22, height = 28;
    int gunWidth = 6, gunHeight = 12;

    Tank(int cellSize, int rows, int cols) {
        this.row = utility.rand(1, rows);
        this.col = utility.rand(1, cols);
        this.angle = utility.rand(1, 360);

        this.centerX = (col - 1) * cellSize + UI.offsetX + cellSize / 2;
        this.centerY = (row - 1) * cellSize + UI.offsetY + cellSize / 2;
    }

    void moveForward() {
        double dx = Math.cos(Math.toRadians(angle)) * speed;
        double dy = -Math.sin(Math.toRadians(angle)) * speed;
        collision.checkTankAndBlock(this, dx, dy);
    }

    void moveBackward() {
        double dx = -Math.cos(Math.toRadians(angle)) * speed;
        double dy = Math.sin(Math.toRadians(angle)) * speed;
        collision.checkTankAndBlock(this, dx, dy);
    }

    void turnLeft() {
        collision.checkTankRotationAndBlock(this, per);
    }

    void turnRight() {
        collision.checkTankRotationAndBlock(this, -per);
    }

}