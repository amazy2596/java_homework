import java.util.ArrayList;
import java.awt.geom.Path2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class createPolygon {
    static ArrayList<ArrayList<ArrayList<Pair<Path2D.Double, Integer>>>> blocks = createPolygon.createBlockPolygons();

    static Path2D.Double fillRect(double x1, double y1, double x2, double y2) {
        Path2D.Double polygon = new Path2D.Double();
        polygon.moveTo(x1, y1);
        polygon.lineTo(x2, y1);
        polygon.lineTo(x2, y2);
        polygon.lineTo(x1, y2);
        polygon.closePath();
        return polygon;
    }

    static ArrayList<Pair<Path2D.Double, Integer>> createBlockPolygon(int i, int j, int w) {
        ArrayList<Pair<Path2D.Double, Integer>> block = new ArrayList<>();

        double x = (j - 1) * UI.cellSize + UI.offsetX;
        double y = (i - 1) * UI.cellSize + UI.offsetY;
        double wallThickness = 0.5;

        if (w == 0) {
            block.add(new Pair<>(fillRect(x, y, x + UI.cellSize + UI.BlockWidth, y + wallThickness), 0));
            block.add(new Pair<>(fillRect(x + UI.cellSize + UI.BlockWidth - wallThickness, y, x + UI.cellSize + UI.BlockWidth, y + UI.BlockWidth), 1));
            block.add(new Pair<>(fillRect(x, y + UI.BlockWidth - wallThickness, x + UI.cellSize + UI.BlockWidth, y + UI.BlockWidth), 2));
            block.add(new Pair<>(fillRect(x, y, x + wallThickness, y + UI.BlockWidth), 3));
        }
    
        if (w == 1) {
            block.add(new Pair<>(fillRect(x + UI.cellSize, y, x + UI.cellSize + UI.BlockWidth, y + wallThickness), 0));
            block.add(new Pair<>(fillRect(x + UI.cellSize + UI.BlockWidth, y, x + UI.cellSize + UI.BlockWidth - wallThickness, y + UI.cellSize + UI.BlockWidth), 1));
            block.add(new Pair<>(fillRect(x + UI.cellSize, y + UI.cellSize + UI.BlockWidth - wallThickness, x + UI.cellSize + UI.BlockWidth, y + UI.cellSize + UI.BlockWidth), 2));
            block.add(new Pair<>(fillRect(x + UI.cellSize, y, x + UI.cellSize + wallThickness, y + UI.cellSize + UI.BlockWidth), 3));
        }
    
        if (w == 2) {
            block.add(new Pair<>(fillRect(x, y + UI.cellSize, x + UI.cellSize + UI.BlockWidth, y + UI.cellSize + wallThickness), 0));
            block.add(new Pair<>(fillRect(x + UI.cellSize + UI.BlockWidth - wallThickness, y + UI.cellSize, x + UI.cellSize + UI.BlockWidth, y + UI.cellSize + UI.BlockWidth), 1));
            block.add(new Pair<>(fillRect(x, y + UI.cellSize + UI.BlockWidth - wallThickness, x + UI.cellSize + UI.BlockWidth, y + UI.cellSize + UI.BlockWidth), 2));
            block.add(new Pair<>(fillRect(x, y + UI.cellSize, x + wallThickness, y + UI.cellSize + UI.BlockWidth), 3));
        }
    
        if (w == 3) {
            block.add(new Pair<>(fillRect(x, y, x + UI.BlockWidth, y + wallThickness), 0));
            block.add(new Pair<>(fillRect(x + UI.BlockWidth, y, x + UI.BlockWidth - wallThickness, y + UI.cellSize + UI.BlockWidth), 1));
            block.add(new Pair<>(fillRect(x, y + UI.cellSize + UI.BlockWidth - wallThickness, x + UI.BlockWidth, y + UI.cellSize + UI.BlockWidth), 2));
            block.add(new Pair<>(fillRect(x, y, x + wallThickness, y + UI.cellSize), 3));
        }

        return block;
    }

    static ArrayList<ArrayList<ArrayList<Pair<Path2D.Double, Integer>>>> createBlockPolygons() {
        ArrayList<ArrayList<ArrayList<Pair<Path2D.Double, Integer>>>> blocks = new ArrayList<>();
        for (int row = 0; row <= MazeGenerator.rows; row++) {
            ArrayList<ArrayList<Pair<Path2D.Double, Integer>>> polygonRowList = new ArrayList<>();
            for (int col = 0; col <= MazeGenerator.cols; col++) {
                ArrayList<Pair<Path2D.Double, Integer>> vertexList = new ArrayList<>();

                for (int i = Math.max(1, row - 1); i <= Math.min(MazeGenerator.rows, row + 1); i++) {
                    for (int j = Math.max(1, col - 1); j <= Math.min(MazeGenerator.cols, col + 1); j++) {
                        Point p = MazeGenerator.maze.get(i).get(j);
                        for (int w = 0; w < 4; w++) {
                            if (p.block[w] == 1) {
                                vertexList.addAll(createBlockPolygon(i, j, w));
                            }
                        }
                    }
                }
                
                polygonRowList.add(vertexList);
            }
            blocks.add(polygonRowList);
        }

        return blocks;
    }

    static Path2D.Double createTankPolygon(Tank tank, double centerX, double centerY, double angle) {
        double halfW = tank.width / 2.0;
        double halfH = tank.height / 2.0;

        Path2D.Double polygon = new Path2D.Double();
        polygon.moveTo(-halfW, -halfH);
        polygon.lineTo(-(tank.width - tank.gunWidth) / 2.0, -halfH);
        polygon.lineTo(-(tank.width - tank.gunWidth) / 2.0, -(tank.width - 6) / 2 - tank.gunHeight);
        polygon.lineTo((tank.width - tank.gunWidth) / 2.0, -(tank.width - 6) / 2 - tank.gunHeight);
        polygon.lineTo((tank.width - tank.gunWidth) / 2.0, -halfH);
        polygon.lineTo(halfW, -halfH);
        polygon.lineTo(halfW, halfH);
        polygon.lineTo(-halfW, halfH);
        polygon.closePath();

        AffineTransform transform = new AffineTransform();
        transform.translate(centerX, centerY);
        transform.rotate(-Math.toRadians(angle - 90));

        Path2D.Double tankPoly = new Path2D.Double();
        tankPoly.append(polygon.getPathIterator(transform), false);

        return tankPoly;
    }

    static Path2D.Double createBulletPolygon(Bullet bullet, double x, double y) {
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(-Math.toRadians(bullet.angle + 45));

        Path2D.Double bulletPoly = new Path2D.Double();
        bulletPoly.append(new Ellipse2D.Double(0, 0, 2 * bullet.r, 2 * bullet.r).getPathIterator(transform), false);

        return bulletPoly;
    }

    static ArrayList<Path2D.Double> createBulletPolygons() {
        ArrayList<Path2D.Double> bullets = new ArrayList<>();
        for (int i = 0; i < Controller.bulletController.activeBullets.size(); i++) {
            Bullet bullet = Controller.bulletController.activeBullets.get(i);
            Path2D.Double bulletPoly = createBulletPolygon(bullet, bullet.x, bullet.y);
            bullets.add(bulletPoly);
        }
        return bullets;
    }
}
