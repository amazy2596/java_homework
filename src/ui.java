import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

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

        TankController tank1 = new TankController(cellSize, rows, cols, 1);
        tank1.setBounds(0, 0, frameWidth, frameHeight);
        layeredPane.add(tank1, JLayeredPane.PALETTE_LAYER);

        // TankController tank2 = new TankController(cellSize, rows, cols, 2);
        // tank2.setBounds(0, 0, frameWidth, frameHeight);
        // layeredPane.add(tank2, JLayeredPane.PALETTE_LAYER);

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
                    g2.fillRect(adjustedX, adjustedY, UI.cellSize + UI.BlockWidth, UI.BlockWidth);
                }
                if (p.block[1] == 1) {
                    g2.fillRect(adjustedX + UI.cellSize, adjustedY, UI.BlockWidth, UI.cellSize + UI.BlockWidth);
                }
                if (p.block[2] == 1) {
                    g2.fillRect(adjustedX, adjustedY + UI.cellSize, UI.cellSize + UI.BlockWidth, UI.BlockWidth);
                }
                if (p.block[3] == 1) {
                    g2.fillRect(adjustedX, adjustedY, UI.BlockWidth, UI.cellSize + UI.BlockWidth);
                }
            }
        }
    }
}

class PaintTank extends JPanel {
    Tank tank;

    PaintTank(Tank tank) {
        this.tank = tank;

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.5f));

        g2.translate(tank.centerX, tank.centerY);
        g2.rotate(-Math.toRadians(tank.angle - 90));

        g2.setColor(Color.green);
        g2.fillRect(-(tank.width / 2), -(tank.height / 2), tank.width, tank.height);

        g2.setColor(Color.black);
        g2.drawRect(-(tank.width / 2), -(tank.height / 2), tank.width, tank.height);

        int arcX = 3 - tank.width / 2;
        int arcY = (tank.height - tank.width + 6) / 2 - tank.height / 2;
        g2.drawArc(arcX, arcY, tank.width - 6, tank.width - 6, 115, 310);

        int gunX = (int) ((tank.width - tank.gunWidth) / 2 - tank.width / 2);
        int gunY = (int) ((tank.height - tank.width + 6) / 2 - tank.gunHeight - tank.height / 2);
        g2.setColor(Color.green);
        g2.fillRect(gunX, gunY, (int) tank.gunWidth, (int) tank.gunHeight);

        g2.setColor(Color.black);
        g2.drawLine(gunX, gunY, gunX + (int) tank.gunWidth, gunY);
        g2.drawLine(gunX, gunY, gunX, gunY + (int) tank.gunHeight);
        g2.drawLine(gunX + (int) tank.gunWidth, gunY, gunX + (int) tank.gunWidth, gunY + (int) tank.gunHeight);

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

    TankController(int cellSize, int rows, int cols, int id) {
        this.tank = new Tank(cellSize, rows, cols);
        this.tankPanel = new PaintTank(tank);

        setLayout(new BorderLayout());
        add(tankPanel, BorderLayout.CENTER);
        setOpaque(false);

        setupKeyBindings(id);

        Timer timer = new Timer(1, e -> {
            updateTankState();
            tankPanel.repaint();
        });

        timer.start();
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