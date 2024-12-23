import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Controller {
    static TankController tank1, tank2;
    static BulletController bulletController;
}

class TankController extends JPanel {
    Tank tank;
    PaintTank tankPanel;

    boolean isForward = false;
    boolean isBackward = false;
    boolean isLeft = false;
    boolean isRight = false;
    boolean isFire = false;

    TankController(int rows, int cols, int id) {
        this.tank = new Tank(rows, cols, id);
        this.tankPanel = new PaintTank(tank);

        setLayout(new BorderLayout());
        add(tankPanel, BorderLayout.CENTER);
        setOpaque(false);

        setupKeyBindings(id);

        Timer timer = new Timer(1000 / 120, _ -> {
            updateTankState();
            Collision.checkTankAndBullet(tank, Controller.bulletController.activeBullets);
            checkAlive();
            repaint();
        });

        timer.start();
    }

    private void checkAlive() {
        if (!tank.isAlive) {
            this.setVisible(false);
        }
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

        if (id == 1)
            inputMap.put(KeyStroke.getKeyStroke("pressed SPACE"), "fire");
        else if (id == 2)
            inputMap.put(KeyStroke.getKeyStroke("pressed ENTER"), "fire");
        actionMap.put("fire", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isFire = true;
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
        if (isFire) {
            tank.fire();
            isFire = false;
        }
    }
}

class BulletController extends JPanel {
    Queue<Bullet> bulletPool = new LinkedList<>();
    ArrayList<Bullet> activeBullets;
    long previousTime = System.nanoTime();

    BulletController() {
        this.activeBullets = new ArrayList<>();
        setOpaque(false);
        setDoubleBuffered(true);

        Timer bulletTimer = new Timer(1000 / 120, _ -> updateBullet(System.nanoTime()));
        bulletTimer.start();

        setLayout(new BorderLayout());
    }

    void updateBullet(long currentTime) {
        double deltaTime = (currentTime - previousTime) / 1.0e9;
        previousTime = currentTime;

        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : activeBullets) {
            bullet.time -= deltaTime;
            if (bullet.time <= 0) {
                bulletPool.add(bullet);
                bulletsToRemove.add(bullet);
            }
        }
        activeBullets.removeAll(bulletsToRemove);
        updateBulletState();
    }

    void addBullet(Bullet bullet) {
        Bullet reusedBullet = bulletPool.poll();
        if (reusedBullet == null) {
            reusedBullet = new Bullet(bullet.x, bullet.y, bullet.angle);
        } else {
            reusedBullet.reset(bullet.x, bullet.y, bullet.angle);
        }
        activeBullets.add(reusedBullet);
    }

    void updateBulletState() {
        for (Bullet bullet : activeBullets) {
            bullet.move();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.black);
        for (Bullet bullet : activeBullets) {
            g2.translate(bullet.x, bullet.y);
            g2.rotate(-Math.toRadians(bullet.angle + 45));
            g2.fillArc(0, 0, 2 * bullet.r, 2 * bullet.r, 0, 360);
            g2.setTransform(g2.getDeviceConfiguration().getDefaultTransform());
        }
    }
}