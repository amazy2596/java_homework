import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ui {
    public static void main(String[] args) {
        int rows = utility.rand(9, 12), cols = utility.rand(15, 18);
        MazeGenerator mg = new MazeGenerator(rows, cols);
        ui u = new ui(rows, cols);
    }

    ui(int rows, int cols) {
        JFrame frame = new JFrame("tank battle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int cellSize = 50;
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

        PaintTank tank1 = new PaintTank(cellSize, rows, cols);
        tank1.setBounds(0, 0, frameWidth, frameHeight);
        layeredPane.add(tank1, JLayeredPane.PALETTE_LAYER);

        frame.add(layeredPane);
        frame.setVisible(true);
    }
    
}

class Map extends JPanel {
    ArrayList<ArrayList<Point>> maze;
    int cellSize, rows, cols;
    int offsetX = 70;
    int offsetY = 50;

    Map(int cellSize, int rows, int cols) {
        this.maze = MazeGenerator.getMaze();
        this.cellSize = cellSize;
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.decode("#E6E6E6"));
        g.fillRect(offsetX, offsetY, cols * cellSize, rows * cellSize);

        g.setColor(Color.black);
        g2.setStroke(new BasicStroke(5));


        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                Point p = maze.get(i).get(j);
                int x = (j - 1) * cellSize;
                int y = (i - 1) * cellSize;

                // 偏移后的坐标
                int adjustedX = x + offsetX;
                int adjustedY = y + offsetY;

                // 绘制墙壁
                if (p.block[0] == 1) { // 上墙
                    g2.drawLine(adjustedX, adjustedY, adjustedX + cellSize, adjustedY);
                }
                if (p.block[1] == 1) { // 右墙
                    g2.drawLine(adjustedX + cellSize, adjustedY, adjustedX + cellSize, adjustedY + cellSize);
                }
                if (p.block[2] == 1) { // 下墙
                    g2.drawLine(adjustedX, adjustedY + cellSize, adjustedX + cellSize, adjustedY + cellSize);
                }
                if (p.block[3] == 1) { // 左墙
                    g2.drawLine(adjustedX, adjustedY, adjustedX, adjustedY + cellSize);
                }
            }
        }
    }
}

class PaintTank extends JPanel {
    Tank tank;
    int x, y;
    int width = 22, height = 28;
    int gunWidth = 6, gunHeight = 12;
    int offsetX = 70;
    int offsetY = 50;

    PaintTank(int cellSize, int rows, int cols) {
        this.tank = new Tank(rows, cols, cellSize);
        this.x = tank.x + offsetX;
        this.y = tank.y + offsetY;

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.4f));

        // 绘制坦克
        g2.setColor(Color.green);
        g2.fillRect(x, y, width, height);

        // 绘制边框
        g2.setColor(Color.black);
        g2.drawRect(x, y, width, height);

        g2.setColor(Color.black);
        g2.drawArc(x + 3, y + (height - width + 6) / 2, width - 6, width - 6, 115, 308);

        g2.setColor(Color.green);
        g2.fillRect(x + (width - gunWidth) / 2, y + (height - width + 6) / 2 - gunHeight, gunWidth, gunHeight);

        g2.setColor(Color.black);
        g2.drawLine(x + (width - gunWidth) / 2, y + (height - width + 6) / 2 - gunHeight, x + (width - gunWidth) / 2 + gunWidth, y + (height - width + 6) / 2 - gunHeight);
        g2.drawLine(x + (width - gunWidth) / 2, y + (height - width + 6) / 2 - gunHeight, x + (width - gunWidth) / 2, y + (height - width + 6) / 2);
        g2.drawLine(x + (width - gunWidth) / 2 + gunWidth, y + (height - width + 6) / 2 - gunHeight, x + (width - gunWidth) / 2 + gunWidth, y + (height - width + 6) / 2);
    }
}