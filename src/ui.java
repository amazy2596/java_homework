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

    static JFrame frame = new JFrame("tank trouble");
    static JLayeredPane layeredPane = new JLayeredPane();
    static JButton menuButton;
    
    static JPanel scorePanel;
    static JLabel player1ScoreLabel;
    static JLabel player2ScoreLabel;
    static int player1Score = 0;
    static int player2Score = 0;

    UI() {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        menu();

        frame.add(layeredPane);
        frame.setVisible(true);
    }

    void menu() {
        layeredPane.removeAll();
        int frameWidth = 1080;
        int frameHeight = 720;
        frame.setSize(frameWidth, frameHeight);
        centerWindow();
        
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(null);
        menuPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        menuPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("坦克动荡(Tank Trouble)");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
        titleLabel.setBounds(frame.getWidth()/2 - 250, 50, 500, 50);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        menuPanel.add(titleLabel);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 0, 20));
        buttonPanel.setBounds(frame.getWidth()/2 - 150, 150, 300, 300);
        buttonPanel.setOpaque(false);
        
        String[] modes = {"单人游戏", "双人对战", "双人电脑", "三人对战"};
        for (String mode : modes) {
            JButton button = createStyledButton(mode);
            buttonPanel.add(button);
        }
        
        menuPanel.add(buttonPanel);
        layeredPane.add(menuPanel, JLayeredPane.DEFAULT_LAYER);
        frame.repaint();
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(60, 60, 60));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(80, 80, 80));
                } else {
                    g2.setColor(new Color(100, 100, 100));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("微软雅黑", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(text, textX, textY);
                
                g2.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(200, 50));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        
        button.addActionListener(_ -> {
            switch (text) {
                case "单人游戏":
                    break;
                case "双人对战":
                    twoPlayer();
                    break;
                case "双人电脑":
                    break;
                case "三人对战":
                    break;
            }
        });
        
        return button;
    }

    public void twoPlayer() {
        layeredPane.removeAll();

        int rows = utility.rand(4, 8), cols = utility.rand(4, 8);
        MazeGenerator mg = new MazeGenerator(rows, cols);
        createPolygon.blocks = createPolygon.createBlockPolygons();

        int frameWidth = cols * cellSize + 150;
        int frameHeight = rows * cellSize + 200;
        frame.setSize(frameWidth, frameHeight);
        centerWindow();

        Map p = new Map(cellSize, rows, cols);
        p.setBounds(0, 0, frameWidth, frameHeight);
        layeredPane.add(p, JLayeredPane.DEFAULT_LAYER);

        Controller.tank1 = new TankController(rows, cols, 1);
        Controller.tank2 = new TankController(rows, cols, 2);

        if (Controller.tank1 != null) {
            Controller.tank1.setBounds(0, 0, frameWidth, frameHeight);
            layeredPane.add(Controller.tank1, JLayeredPane.PALETTE_LAYER);
        }

        if (Controller.tank2 != null) {
            Controller.tank2.setBounds(0, 0, frameWidth, frameHeight);
            layeredPane.add(Controller.tank2, JLayeredPane.PALETTE_LAYER);
        }

        if (Controller.tank3 != null) {
            Controller.tank3.setBounds(0, 0, frameWidth, frameHeight);
            layeredPane.add(Controller.tank3, JLayeredPane.PALETTE_LAYER);
        }

        Controller.bulletController.setBounds(0, 0, frameWidth, frameHeight);
        layeredPane.add(Controller.bulletController, JLayeredPane.PALETTE_LAYER);

        menuButton = createMenuButton();
        menuButton.setBounds(frameWidth - 100, frameHeight - 80, 80, 40);
        layeredPane.add(menuButton, JLayeredPane.PALETTE_LAYER);

        createScorePanel();
        scorePanel.setBounds(0, frameHeight - 80, frameWidth, 40);
        layeredPane.add(scorePanel, JLayeredPane.PALETTE_LAYER);

        layeredPane.revalidate();
        frame.repaint();
    }

    void createScorePanel() {
        scorePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.dispose();
            }
        };

        scorePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 10));
        scorePanel.setOpaque(false);

        // 创建玩家1的分数标签
        player1ScoreLabel = new JLabel("玩家: " + player1Score) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(Color.GREEN.darker());
                g2.setFont(getFont());
                g2.drawString(getText(), 0, g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        player1ScoreLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        
        // 创建玩家2的分数标签
        player2ScoreLabel = new JLabel("玩家: " + player2Score) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(Color.RED.darker());
                g2.setFont(getFont());
                g2.drawString(getText(), 0, g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        player2ScoreLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        
        scorePanel.add(player1ScoreLabel);
        scorePanel.add(player2ScoreLabel);
    }

    static public void updateScore(int playerId) {
        if (playerId == 1) {
            player1Score++;
            player1ScoreLabel.setText("玩家1: " + player1Score);
        } else if (playerId == 2) {
            player2Score++;
            player2ScoreLabel.setText("玩家2: " + player2Score);
        }
    }

    void centerWindow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        frame.setLocation((screenWidth - frame.getWidth()) / 2, (screenHeight - frame.getHeight()) / 2);
    }

    public JButton createMenuButton() {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 绘制按钮背景
                if (getModel().isPressed()) {
                    g2.setColor(new Color(60, 60, 60, 200));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(80, 80, 80, 200));
                } else {
                    g2.setColor(new Color(100, 100, 100, 200));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // 绘制文字
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("微软雅黑", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String text = "菜单";
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(text, textX, textY);
                
                g2.dispose();
            }
        };

        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(_ -> returnToMenu());
        
        return button;
    }

    public void returnToMenu() {
        cleanUpGame();
        player1Score = 0;
        player2Score = 0;
        menu();
    }

    public void cleanUpGame() {
        resetControllers();
        layeredPane.removeAll();
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    public void resetControllers() {
        if (Controller.tank1 != null) {
            Controller.tank1.setVisible(false);
            Controller.tank1.stopTimer();
            Controller.tank1 = null;
        }
        if (Controller.tank2 != null) {
            Controller.tank2.setVisible(false);
            Controller.tank2.stopTimer();
            Controller.tank2 = null;
        }
        if (Controller.tank3 != null) {
            Controller.tank3.setVisible(false);
            Controller.tank3.stopTimer();
            Controller.tank3 = null;
        }
        if (Controller.bulletController != null) {
            Controller.bulletController.activeBullets.clear();
        }
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

        g.setColor(Color.decode("#4D4D4D"));
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

        Color color = new Color(0, 0, 0, 0);

        if (tank.id == 1) {
            color = Color.green;
        } else if (tank.id == 2) {
            color = Color.red;
        } else if (tank.id == 3) {
            color = Color.blue;
        }

        g2.setColor(color);
        g2.fillRect(-(tank.width / 2), -(tank.height / 2), tank.width, tank.height);

        g2.setColor(Color.black);
        g2.drawRect(-(tank.width / 2), -(tank.height / 2), tank.width, tank.height);

        int arcX = 3 - tank.width / 2;
        int arcY = (tank.height - tank.width + 6) / 2 - tank.height / 2;
        g2.drawArc(arcX, arcY, tank.width - 6, tank.width - 6, 115, 310);

        int gunX = (tank.width - tank.gunWidth) / 2 - tank.width / 2;
        int gunY = (tank.height - tank.width + 6) / 2 - tank.gunHeight - tank.height / 2;
        g2.setColor(color);
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