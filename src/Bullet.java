public class Bullet {
    double x, y;
    double angle;
    int row, col;
    int r = 3;
    double time = 6;
    double speed = 4;
    
    Bullet(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    void move() {
        double dx = Math.cos(Math.toRadians(angle)) * speed;
        double dy = -Math.sin(Math.toRadians(angle)) * speed;
        Collision.checkBulletAndBlock(this, dx, dy);
        update();
    }

    void update() {
        row = (int) ((y - UI.offsetY) / UI.cellSize) + 1;
        col = (int) ((x - UI.offsetX) / UI.cellSize) + 1;
    }

    void reset(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.time = 6;
    }
}
