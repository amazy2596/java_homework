import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.awt.geom.Area;

public class Collision {

    static Pair<Path2D.Double, Integer> isCollidingWithBlock(Path2D.Double obj, ArrayList<Pair<Path2D.Double, Integer>> blocks) {
        Area area = new Area(obj);

        for (Pair<Path2D.Double, Integer> block : blocks) {
            Area blockArea = new Area(block.getKey());
            Area testArea = (Area) area.clone();
            testArea.intersect(blockArea);
            if (!testArea.isEmpty()) {
                return block;
            }
        }
        return null;
    }

    static boolean isCollidingWithBullet(Path2D.Double obj, ArrayList<Bullet> bullets) {
        Area area = new Area(obj);

        for (Bullet bullet : bullets) {
            Path2D.Double bulletPoly = createPolygon.createBulletPolygon(bullet, bullet.x, bullet.y);
            Area bulletArea = new Area(bulletPoly);
            Area testArea = (Area) area.clone();
            testArea.intersect(bulletArea);
            if (!testArea.isEmpty()) {
                bullet.time = 0;
                return true;
            }
        }
        return false;
    }

    static void checkTankAndBlock(Tank tank, double dx, double dy) {
        ArrayList<Pair<Path2D.Double, Integer>> blocks = createPolygon.blocks.get(tank.row).get(tank.col);
        int steps = 4;
        double stepX = dx / steps;
        double stepY = dy / steps;

        double tempX = tank.centerX;
        double tempY = tank.centerY;

        for (int i = 0; i < steps; i++) {
            double newX = tempX + stepX;
            double newY = tempY + stepY;

            Path2D.Double tankPoly = createPolygon.createTankPolygon(tank, newX, newY, tank.angle);
            if (isCollidingWithBlock(tankPoly, blocks) == null) {
                tempX = newX;
                tempY = newY;
                continue;
            }

            tankPoly = createPolygon.createTankPolygon(tank, tempX, newY, tank.angle);
            if (isCollidingWithBlock(tankPoly, blocks) == null) {
                tempY = newY;
                continue;
            }

            tankPoly = createPolygon.createTankPolygon(tank, newX, tempY, tank.angle);
            if (isCollidingWithBlock(tankPoly, blocks) == null) {
                tempX = newX;
                continue;
            }
        }

        tank.centerX = tempX;
        tank.centerY = tempY;
    }

    static void checkTankRotationAndBlock(Tank tank, double angleDiff) {
        int steps = 10;
        double stepAngle = angleDiff / steps;
    
        double tempAngle = tank.angle;
    
        for (int i = 0; i < steps; i++) {
            double newAngle = tempAngle + stepAngle;
            
            Path2D.Double tankPoly = createPolygon.createTankPolygon(tank, tank.centerX, tank.centerY, newAngle);
            Pair<Path2D.Double, Integer> block = isCollidingWithBlock(tankPoly, createPolygon.blocks.get(tank.row).get(tank.col));
            if (block == null) {
                tempAngle = newAngle;
            } else {

                boolean resolved = false;
                double pushDistance = 0.5;
                int pushAttempts = 60;
                for (int attempt = 0; attempt < pushAttempts; attempt++) {
                    double rad = Math.toRadians(tempAngle);
                    
                    double dx = -Math.cos(rad) * pushDistance;
                    double dy = Math.sin(rad) * pushDistance;
    
                    double backupX = tank.centerX;
                    double backupY = tank.centerY;
    
                    tank.centerX += dx;
                    tank.centerY += dy;
    
                    tankPoly = createPolygon.createTankPolygon(tank, tank.centerX, tank.centerY, newAngle);
                    if (isCollidingWithBlock(tankPoly, createPolygon.blocks.get(tank.row).get(tank.col)) == null) {
                        tempAngle = newAngle;
                        resolved = true;
                        break;
                    } else {
                        tank.centerX = backupX;
                        tank.centerY = backupY;
                    }
                }

                if (!resolved) {
                    break;
                }
            }
        }
    
        tank.angle = (tempAngle + 360) % 360;
    }

    static void checkTankAndBullet(Tank tank, ArrayList<Bullet> bullets) {
        Path2D.Double tankPoly = createPolygon.createTankPolygon(tank, tank.centerX, tank.centerY, tank.angle);
        if (isCollidingWithBullet(tankPoly, bullets)) {
            tank.isAlive = false;
        }
    }

    static void checkBulletAndBlock(Bullet bullet, double dx, double dy) {
        double newX = bullet.x + dx;
        double newY = bullet.y + dy;

        Path2D.Double bulletPoly = createPolygon.createBulletPolygon(bullet, newX, newY);
        Pair<Path2D.Double, Integer> block = isCollidingWithBlock(bulletPoly, createPolygon.blocks.get(bullet.row).get(bullet.col));

        if (block == null) {
            bullet.x = newX;
            bullet.y = newY;
            return;
        }

        if (block.getValue() == 0 || block.getValue() == 2) {
            bullet.angle = (-bullet.angle + 360) % 360;
        } else if (block.getValue() == 1 || block.getValue() == 3) {
            bullet.angle = (180 - bullet.angle + 360) % 360;
        }
    }
}