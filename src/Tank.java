public class Tank {
    double centerX, centerY;
    int row, col;
    double angle;

    double speed = 2.75;
    double per = 5;

    int maxBullets = 6;
    int countBullets = 0;

    boolean isAlive = true;
    int id;

    int width = 22, height = 28;
    int gunWidth = 6, gunHeight = 12;

    Tank(int rows, int cols, int id) {
        this.row = utility.rand(1, rows);
        this.col = utility.rand(1, cols);
        this.angle = utility.rand(1, 360);
        this.id = id;

        this.centerX = (col - 1) * UI.cellSize + UI.offsetX + UI.cellSize / 2;
        this.centerY = (row - 1) * UI.cellSize + UI.offsetY + UI.cellSize / 2;
    }

    void moveForward() {
        double dx = Math.cos(Math.toRadians(angle)) * speed;
        double dy = -Math.sin(Math.toRadians(angle)) * speed;
        Collision.checkTankAndBlock(this, dx, dy);
        update();
    }

    void moveBackward() {
        double dx = -Math.cos(Math.toRadians(angle)) * speed;
        double dy = Math.sin(Math.toRadians(angle)) * speed;
        Collision.checkTankAndBlock(this, dx, dy);
        update();
    }

    void turnLeft() {
        Collision.checkTankRotationAndBlock(this, per);
        update();
    }

    void turnRight() {
        Collision.checkTankRotationAndBlock(this, -per);
        update();
    }

    void fire() {
        if (countBullets < maxBullets) {
            // countBullets++;
            double bulletX = centerX + Math.cos(Math.toRadians(angle)) * ((width - 6) / 2 + gunHeight + 1);
            double bulletY = centerY - Math.sin(Math.toRadians(angle)) * ((width - 6) / 2 + gunHeight + 1);

            Bullet bullet = new Bullet(bulletX, bulletY, angle);
            Controller.bulletController.addBullet(bullet);
        }
    }

    void update() {
        row = (int) ((centerY - UI.offsetY) / UI.cellSize) + 1;
        col = (int) ((centerX - UI.offsetX) / UI.cellSize) + 1;
    }

    void getBullet() {
        if (countBullets > 0) {
            countBullets--;
        }
    }

    void reset(int rows, int cols, int id) {
        this.row = utility.rand(1, rows);
        this.col = utility.rand(1, cols);
        this.angle = utility.rand(1, 360);
        this.id = id;
        isAlive = true;
        countBullets = 0;

        this.centerX = (col - 1) * UI.cellSize + UI.offsetX + UI.cellSize / 2;
        this.centerY = (row - 1) * UI.cellSize + UI.offsetY + UI.cellSize / 2;
    }
}