public class Tank {
    double x, y;
    int row, col;
    int rows, cols;
    double angle;

    double speed = 2;
    double per = 4;

    int width = 22, height = 28;
    int gunWidth = 6, gunHeight = 12;
    double centerX, centerY;
    int offsetX = 70;
    int offsetY = 50;

    Tank(int cellSize, int rows, int cols) {
        this.row = utility.rand(1, rows);
        this.col = utility.rand(1, cols);
        this.rows = rows;
        this.cols = cols;
        this.angle = utility.rand(1, 360);

        this.x = (col - 1) * cellSize + offsetX + cellSize / 2 - width / 2;
        this.y = (row - 1) * cellSize + offsetY + cellSize / 2 - height / 2;
        update();
    }

    void update() {
        centerX = x + width / 2;
        centerY = y + height / 2;

        col = (int) ((centerX - offsetX) / 75) + 1;
        row = (int) ((centerY - offsetY) / 75) + 1;
    }

    void check(double dx, double dy) {
        if (x + dx < offsetX) {
            x = offsetX;
        } else if (x + dx > offsetX + 75 * cols - width) {
            x = offsetX + 75 * cols - width;
        } else {
            x += dx;
        }

        if (y + dy < offsetY) {
            y = offsetY;
        } else if (y + dy > offsetY + 75 * rows - height) {
            y = offsetY + 75 * rows - height;
        } else {
            y += dy;
        }

        update();
    }

    void moveForward() {
        double dx = -Math.cos(Math.toRadians(angle)) * speed;
        double dy = Math.sin(Math.toRadians(angle)) * speed;
        check(dx, dy);
    }

    void moveBackward() {
        double dx = Math.cos(Math.toRadians(angle)) * speed;
        double dy = -Math.sin(Math.toRadians(angle)) * speed;
        check(dx, dy);
    }

    void turnLeft() {
        angle = (angle + per) % 360; // 规范化角度
        update();
    }
    
    void turnRight() {
        angle = (angle - per + 360) % 360; // 规范化角度
        update();
    }
    
}