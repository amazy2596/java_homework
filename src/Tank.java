public class Tank {
    double centerX, centerY;
    int row, col;
    double angle;

    double speed = 4;
    double per = 8;

    int maxBullets = 6;
    int countBullets = 0;

    boolean isForward = false;
    boolean isBackward = false;
    boolean isLeft = false;
    boolean isRight = false;

    boolean isAlive = true;
    boolean isFire = false;
    boolean isCollisionWithBlock = false;
    int id;

    int width = 22, height = 28;
    int gunWidth = 6, gunHeight = 12;
    int maxDimension = (int) Math.sqrt(width * width + (height + 6) * (height + 6)) * 2;

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
        if (isCollisionWithBlock) {
            shake();
        }
        update();
    }

    void moveBackward() {
        double dx = -Math.cos(Math.toRadians(angle)) * speed;
        double dy = Math.sin(Math.toRadians(angle)) * speed;
        Collision.checkTankAndBlock(this, dx, dy);
        if (isCollisionWithBlock) {
            shake();
        }
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

    void shake() {
        double dx = utility.rand(-1, 1);
        double dy = utility.rand(-1, 1);
        double rad = utility.rand(-2, 2);
        Collision.checkTankAndBlock(this, dx, dy);
        Collision.checkTankRotationAndBlock(this, rad);
    }

    void fire() {
        if (countBullets < maxBullets) {
            countBullets++;
            double bulletX = centerX + Math.cos(Math.toRadians(angle)) * ((width - 6) / 2 + gunHeight + 1);
            double bulletY = centerY - Math.sin(Math.toRadians(angle)) * ((width - 6) / 2 + gunHeight + 1);

            Bullet bullet = new Bullet(bulletX, bulletY, angle, id);
            Controller.bulletController.addBullet(bullet, id);
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