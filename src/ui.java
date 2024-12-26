import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;

public class UI {
    static int cellSize = 75;
    static int offsetX = 70;
    static int offsetY = 50;
    static int BlockWidth = 6;

    UI(int rows, int cols) {
        JFrame frame = new JFrame("tank trouble");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        int frameWidth = cols * cellSize + 150;
        int frameHeight = rows * cellSize + 200;
        frame.setSize(frameWidth, frameHeight);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        frame.setLocation((screenWidth - frameWidth) / 2, (screenHeight - frameHeight) / 2);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, frameWidth, frameHeight);

        Map p = new Map(cellSize, rows, cols);
        p.setBounds(0, 0, frameWidth, frameHeight);
        layeredPane.add(p, JLayeredPane.DEFAULT_LAYER);

        if (Controller.tank1 != null) {
            Controller.tank1.setBounds(0, 0, frameWidth, frameHeight);
            layeredPane.add(Controller.tank1, JLayeredPane.PALETTE_LAYER);
        }

        if (Controller.tank2 != null) {
            Controller.tank2.setBounds(0, 0, frameWidth, frameHeight);
            layeredPane.add(Controller.tank2, JLayeredPane.PALETTE_LAYER);
        }

        Controller.bulletController.setBounds(0, 0, frameWidth, frameHeight);
        layeredPane.add(Controller.bulletController, JLayeredPane.PALETTE_LAYER);

        frame.add(layeredPane);
        frame.setVisible(true);
    }

}

class Map extends JPanel {
    ArrayList<ArrayList<Point>> maze;

    Map(int cellSize, int rows, int cols) {
        this.maze = MazeGenerator.maze;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.decode("#E6E6E6"));
        g.fillRect(UI.offsetX, UI.offsetY, MazeGenerator.cols * UI.cellSize, MazeGenerator.rows * UI.cellSize);

        g.setColor(Color.decode("#666666"));
        for (int i = 1; i <= MazeGenerator.rows; i++) {
            for (int j = 1; j <= MazeGenerator.cols; j++) {
                Point p = maze.get(i).get(j);
                int x = (j - 1) * UI.cellSize;
                int y = (i - 1) * UI.cellSize;

                int adjustedX = x + UI.offsetX;
                int adjustedY = y + UI.offsetY;

                if (p.block[0] == 1) {
                    g2.fillRect(adjustedX, adjustedY, UI.cellSize, UI.BlockWidth);
                }
                if (p.block[1] == 1) {
                    g2.fillRect(adjustedX + UI.cellSize, adjustedY, UI.BlockWidth, UI.cellSize + UI.BlockWidth);
                }
                if (p.block[2] == 1) {
                    g2.fillRect(adjustedX, adjustedY + UI.cellSize, UI.cellSize + UI.BlockWidth, UI.BlockWidth);
                }
                if (p.block[3] == 1) {
                    g2.fillRect(adjustedX, adjustedY, UI.BlockWidth, UI.cellSize);
                }

                // if (i == Controller.tank1.tank.row && j == Controller.tank1.tank.col) {
                    // g.setColor(Color.red);
                    // g.fillRect(adjustedX + UI.BlockWidth, adjustedY + UI.BlockWidth, UI.cellSize - UI.BlockWidth, UI.cellSize - UI.BlockWidth);
                    // g.setColor(Color.decode("#666666")); // Reset color for walls
                // }
            }
        }
    }
}

class PaintTank extends JPanel {
    Tank tank;
    BufferedImage tankImage;
    double lastAngle = -1;

    PaintTank(Tank tank) {
        this.tank = tank;
        setOpaque(false);
        setDoubleBuffered(true);

        createTankImage();
    }

    private void createTankImage(){
        tankImage = new BufferedImage(tank.maxDimension * 2, tank.maxDimension * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = tankImage.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2.setStroke(new BasicStroke(1.5f));

        g2.translate(tank.maxDimension / 2, tank.maxDimension / 2);
        g2.rotate(-Math.toRadians(tank.angle - 90));

        g2.scale(2.0, 2.0);

        g2.setColor(Color.green);
        g2.fillRect(-(tank.width / 2), -(tank.height / 2), tank.width, tank.height);

        g2.setColor(Color.black);
        g2.drawRect(-(tank.width / 2), -(tank.height / 2), tank.width, tank.height);

        int arcX = 3 - tank.width / 2;
        int arcY = (tank.height - tank.width + 6) / 2 - tank.height / 2;
        g2.drawArc(arcX, arcY, tank.width - 6, tank.width - 6, 115, 310);

        int gunX = (tank.width - tank.gunWidth) / 2 - tank.width / 2;
        int gunY = (tank.height - tank.width + 6) / 2 - tank.gunHeight - tank.height / 2;
        g2.setColor(Color.green);
        g2.fillRect(gunX, gunY, tank.gunWidth, tank.gunHeight);

        g2.setColor(Color.black);
        g2.drawLine(gunX, gunY, gunX + tank.gunWidth, gunY);
        g2.drawLine(gunX, gunY, gunX, gunY + tank.gunHeight);
        g2.drawLine(gunX + tank.gunWidth, gunY, gunX + tank.gunWidth, gunY + tank.gunHeight);

        g2.setTransform(g2.getDeviceConfiguration().getDefaultTransform());

        g2.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (tankImage == null || lastAngle != tank.angle) {
            createTankImage();
            lastAngle = tank.angle;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform oldTransform = g2.getTransform();

        g2.translate(tank.centerX, tank.centerY);
        g2.scale(0.5, 0.5);
        g2.drawImage(tankImage, -tank.maxDimension / 2, -tank.maxDimension / 2, null);

        g2.setTransform(oldTransform);
    }
}

class BulletPaint extends JPanel {
    Bullet bullet;

    BulletPaint(Bullet bullet) {
        this.bullet = bullet;

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.black);
        g2.fillArc((int) bullet.x, (int) bullet.y, 2 * bullet.r, 2 * bullet.r, 0, 360);
    }
}