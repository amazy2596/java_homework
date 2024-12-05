import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ui {
    public static void main(String[] args) {
        int rows = utility.rand(6, 9), cols = utility.rand(12, 15);
        MazeGenerator mg = new MazeGenerator(rows, cols);
        ui u = new ui(rows, cols);
    }

    ui(int rows, int cols) {
        JFrame frame = new JFrame("tank battle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int cellSize = 75;
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

        TankController tank1 = new TankController(cellSize, rows, cols, 1);
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
        this.maze = MazeGenerator.maze;
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
    int cellSize = 75;
    double prevCenterX, prevCenterY, prevAngle;

    PaintTank(Tank tank) {
        this.tank = tank;
        prevCenterX = tank.centerX;
        prevCenterY = tank.centerY;
        prevAngle = tank.angle;

        setOpaque(false); // 使背景透明
    }

    void updateInterpolation(double deltaTime, double logicInterval) {
        double Factor = Math.min(deltaTime / logicInterval, 1.0);

        prevCenterX = interpolate(prevCenterX, tank.centerX, Factor);
        prevCenterY = interpolate(prevCenterY, tank.centerY, Factor);
        prevAngle = interpolate(prevAngle, tank.angle, Factor);
    }

    double interpolate(double a, double b, double factor) {
        return a + (b - a) * factor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 启用抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.4f));

        // 平移和旋转
        g2.translate(tank.centerX, tank.centerY);
        g2.rotate(-Math.toRadians(tank.angle + 90));

        // 绘制坦克主体
        g2.setColor(Color.green);
        g2.fillRect(-(tank.width / 2), -(tank.height / 2), tank.width, tank.height);

        g2.setColor(Color.black);
        g2.drawRect(-(tank.width / 2), -(tank.height / 2), tank.width, tank.height);

        // 绘制装饰圆弧
        int arcX = 3 - tank.width / 2;
        int arcY = (tank.height - tank.width + 6) / 2 - tank.height / 2;
        g2.drawArc(arcX, arcY, tank.width - 6, tank.width - 6, 115, 308);

        // 绘制炮管
        int gunX = (int) ((tank.width - tank.gunWidth) / 2 - tank.width / 2);
        int gunY = (int) ((tank.height - tank.width + 6) / 2 - tank.gunHeight - tank.height / 2);
        g2.setColor(Color.green);
        g2.fillRect(gunX, gunY, (int) tank.gunWidth, (int) tank.gunHeight);

        g2.setColor(Color.black);
        g2.drawLine(gunX, gunY, gunX + (int) tank.gunWidth, gunY); // 炮管顶线
        g2.drawLine(gunX, gunY, gunX, gunY + (int) tank.gunHeight); // 炮管左线
        g2.drawLine(gunX + (int) tank.gunWidth, gunY, gunX + (int) tank.gunWidth, gunY + (int) tank.gunHeight); // 炮管右线

        // 恢复画布状态
        g2.setTransform(g2.getDeviceConfiguration().getDefaultTransform());
    }
}


class TankController extends JPanel {
    Tank tank;
    PaintTank tankPanel;

    boolean isForward = false;
    boolean isBackward = false;
    boolean isLeft = false;
    boolean isRight = false;

    long lastLogicUpdateTime = System.currentTimeMillis();
    double logicInterval = 100;

    TankController(int cellSize, int rows, int cols, int id) {
        this.tank = new Tank(cellSize, rows, cols);
        this.tankPanel = new PaintTank(tank);

        setLayout(new BorderLayout());
        add(tankPanel, BorderLayout.CENTER);
        setOpaque(false);

        setupKeyBindings(id);

        Timer timer = new Timer(1, e -> {
            updateTankState();
            // System.out.println("row: " + tank.row + " col: " + tank.col);
            tankPanel.repaint();
        });

        timer.start();

        // Timer renderTimer = new Timer(16, e -> {
        //     long currentTime = System.currentTimeMillis();
        //     double deltaTime = currentTime - lastLogicUpdateTime;

        //     tankPanel.updateInterpolation(deltaTime, logicInterval);
        //     tankPanel.repaint();
        // });

        // Timer logicTimer = new Timer((int) logicInterval, e -> {
        //     updateTankState();
        //     lastLogicUpdateTime = System.currentTimeMillis();
        // });

        // renderTimer.start();
        // logicTimer.start();
    }

    private void setupKeyBindings(int id) {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        if (id == 1)
            inputMap.put(KeyStroke.getKeyStroke("pressed W"), "forwardPressed");
        else if (id == 2)
            inputMap.put(KeyStroke.getKeyStroke("pressed UP"), "forwardPressed");
        actionMap.put("forwardPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isForward = true;
            }
        });

        if (id == 1)
            inputMap.put(KeyStroke.getKeyStroke("released W"), "forwardReleased");
        else if (id == 2)
            inputMap.put(KeyStroke.getKeyStroke("released UP"), "forwardReleased");
        actionMap.put("forwardReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isForward = false;
            }
        });

        if (id == 1)
            inputMap.put(KeyStroke.getKeyStroke("pressed S"), "backwardPressed");
        else if (id == 2)
            inputMap.put(KeyStroke.getKeyStroke("pressed DOWN"), "backwardPressed");
        actionMap.put("backwardPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBackward = true;
            }
        });

        if (id == 1)
            inputMap.put(KeyStroke.getKeyStroke("released S"), "backwardReleased");
        else if (id == 2)
            inputMap.put(KeyStroke.getKeyStroke("released DOWN"), "backwardReleased");
        actionMap.put("backwardReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBackward = false;
            }
        });

        if (id == 1)
            inputMap.put(KeyStroke.getKeyStroke("pressed A"), "leftPressed");
        else if (id == 2)
            inputMap.put(KeyStroke.getKeyStroke("pressed LEFT"), "leftPressed");
        actionMap.put("leftPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isLeft = true;
            }
        });

        if (id == 1)
            inputMap.put(KeyStroke.getKeyStroke("released A"), "leftReleased");
        else if (id == 2)
            inputMap.put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");
        actionMap.put("leftReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isLeft = false;
            }
        });

        if (id == 1)
            inputMap.put(KeyStroke.getKeyStroke("pressed D"), "rightPressed");
        else if (id == 2)
            inputMap.put(KeyStroke.getKeyStroke("pressed RIGHT"), "rightPressed");
        actionMap.put("rightPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isRight = true;
            }
        });

        if (id == 1)
            inputMap.put(KeyStroke.getKeyStroke("released D"), "rightReleased");
        else if (id == 2)
            inputMap.put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");
        actionMap.put("rightReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isRight = false;
            }
        });
    }

    void updateTankState() {
        if (isForward) {
            tank.moveForward();
        }
        if (isBackward) {
            tank.moveBackward();
        }
        if (isLeft) {
            tank.turnLeft();
        }
        if (isRight) {
            tank.turnRight();
        }
    }
}