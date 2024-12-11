import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.awt.geom.Area;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

public class collision {

    /**
    * 打印多边形的所有顶点坐标信息。
    * @param poly 要打印的多边形
    */
    public static void debugPrintPolygon(Path2D.Double poly) {
        System.out.println("Polygon vertices:");
        PathIterator pi = poly.getPathIterator(null);
        double[] coords = new double[6];
        
        while (!pi.isDone()) {
            int segType = pi.currentSegment(coords);
            switch (segType) {
                case PathIterator.SEG_MOVETO:
                    System.out.printf("  MoveTo: (%.2f, %.2f)%n", coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    System.out.printf("  LineTo: (%.2f, %.2f)%n", coords[0], coords[1]);
                    break;
                case PathIterator.SEG_CLOSE:
                    // System.out.println("  ClosePath");
                    break;
                default:
                    System.out.println("  Other segment type: " + segType);
                    break;
            }
            pi.next();
        }
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

    static Path2D.Double createBlockPolygon(int i, int j, int w) {
        Path2D.Double polygon2 = new Path2D.Double();

        double x = (j - 1) * UI.cellSize + UI.offsetX;
        double y = (i - 1) * UI.cellSize + UI.offsetY;
        boolean hasBlock = false;

        if (w == 0) {
            polygon2.moveTo(x, y);
            polygon2.lineTo(x + UI.cellSize, y);
            polygon2.lineTo(x + UI.cellSize, y + UI.BlockWidth);
            polygon2.lineTo(x, y + UI.BlockWidth);
            polygon2.lineTo(x, y);
            polygon2.closePath();
            hasBlock = true;
        }
        if (w == 1) {
            polygon2.moveTo(x + UI.cellSize, y);
            polygon2.lineTo(x + UI.cellSize + UI.BlockWidth, y);
            polygon2.lineTo(x + UI.cellSize + UI.BlockWidth, y + UI.cellSize + UI.BlockWidth);
            polygon2.lineTo(x + UI.cellSize, y + UI.cellSize + UI.BlockWidth);
            polygon2.lineTo(x + UI.cellSize, y);
            polygon2.closePath();
            hasBlock = true;
        }
        if (w == 2) {
            polygon2.moveTo(x, y + UI.cellSize);
            polygon2.lineTo(x + UI.cellSize + UI.BlockWidth, y + UI.cellSize);
            polygon2.lineTo(x + UI.cellSize + UI.BlockWidth, y + UI.cellSize + UI.BlockWidth);
            polygon2.lineTo(x, y + UI.cellSize + UI.BlockWidth);
            polygon2.lineTo(x, y + UI.cellSize);
            polygon2.closePath();
            hasBlock = true;
        }
        if (w == 3) {
            polygon2.moveTo(x, y);
            polygon2.lineTo(x + UI.BlockWidth, y);
            polygon2.lineTo(x + UI.BlockWidth, y + UI.cellSize + UI.BlockWidth);
            polygon2.lineTo(x, y + UI.cellSize + UI.BlockWidth);
            polygon2.lineTo(x, y);
            polygon2.closePath();
            hasBlock = true;
        }

        return hasBlock ? polygon2 : null;
    }

    static ArrayList<Path2D.Double> createBlockPolygons() {
        ArrayList<Path2D.Double> blocks = new ArrayList<>();
        for (int i = 1; i <= MazeGenerator.rows; i++) {
            for (int j = 1; j <= MazeGenerator.cols; j++) {
                Point p = MazeGenerator.maze.get(i).get(j);
                for (int w = 0; w < 4; w++) {
                    if (p.block[w] == 0) {
                        continue;
                    }
                    Path2D.Double polygon = createBlockPolygon(i, j, w);
                    if (polygon != null) {
                        blocks.add(polygon);
                    }
                }
            }
        }
        return blocks;
    }

    static Path2D.Double isColliding(Path2D.Double tankPoly, ArrayList<Path2D.Double> blocks) {
        Area tankArea = new Area(tankPoly);

        for (Path2D.Double block : blocks) {
            Area blockArea = new Area(block);
            Area testArea = (Area) tankArea.clone();
            testArea.intersect(blockArea);
            if (!testArea.isEmpty()) {
                return block;
            }
        }
        return null;
    }

    static void checkTankAndBlock(Tank tank, double dx, double dy) {
        ArrayList<Path2D.Double> blocks = createBlockPolygons();
        int step = 5;
        double stepX = dx / step;
        double stepY = dy / step;

        double tempX = tank.centerX;
        double tempY = tank.centerY;

        for (int i = 0; i < step; i++) {
            double newX = tempX + stepX;
            double newY = tempY + stepY;

            Path2D.Double tankPoly = createTankPolygon(tank, newX, newY, tank.angle);
            if (isColliding(tankPoly, blocks) == null) {
                tempX = newX;
                tempY = newY;
                continue;
            }

            tankPoly = createTankPolygon(tank, tempX, newY, tank.angle);
            if (isColliding(tankPoly, blocks) == null) {
                tempY = newY;
                continue;
            }

            tankPoly = createTankPolygon(tank, newX, tempY, tank.angle);
            if (isColliding(tankPoly, blocks) == null) {
                tempX = newX;
                continue;
            }
        }

        tank.centerX = tempX;
        tank.centerY = tempY;
    }

    static void checkTankRotationAndBlock(Tank tank, double angleDiff) {
        int steps = 5;
        double stepAngle = angleDiff / steps;
    
        ArrayList<Path2D.Double> blocks = createBlockPolygons();
        double tempAngle = tank.angle;
    
        for (int i = 0; i < steps; i++) {
            double newAngle = tempAngle + stepAngle;
            
            Path2D.Double tankPoly = createTankPolygon(tank, tank.centerX, tank.centerY, newAngle);
            Path2D.Double block = isColliding(tankPoly, blocks);
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
    
                    tankPoly = createTankPolygon(tank, tank.centerX, tank.centerY, newAngle);
                    if (isColliding(tankPoly, blocks) == null) {
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
}