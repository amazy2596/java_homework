

import javax.swing.*;

import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.awt.image.BufferedImage;

public class Controller {
    static TankController tank1, tank2, tank3;
    static BulletController bulletController = new BulletController();
}

class TankController extends JPanel {
    Tank tank;
    PaintTank tankPanel;

    Timer timer;

    TankController(int rows, int cols, int id) {
        this.tank = new Tank(rows, cols, id);
        this.tankPanel = new PaintTank(tank);

        setLayout(new BorderLayout());
        add(tankPanel, BorderLayout.CENTER);
        setOpaque(false);

        setupKeyBindings(id);

        timer = new Timer(1000 / 60, _ -> {
            updateTankState();
            Collision.checkTankAndBullet(tank, Controller.bulletController.activeBullets);
            checkAlive();
            repaint();
        });

        timer.start();
    }

    public void stopTimer() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
            timer = null;
        }
    }

    private void checkAlive() {
        if (!tank.isAlive) {
            this.setVisible(false);
            stopTimer();
            setDead();
        }
    }

    private void setDead() {
        if (tank.id == 1) {
            if (!Controller.tank2.tank.isAlive) {
                return;
            }
        } else if (tank.id == 2) {
            if (!Controller.tank1.tank.isAlive) {
                return;
            }
        }

        Timer scoreTimer = new Timer(3000, _ -> {
            if (tank.id == 1) {
                if (!Controller.tank2.tank.isAlive) {
                    UI.updateScore(0);
                } else {
                    UI.updateScore(2);
                }
            } else if (tank.id == 2) {
                if (!Controller.tank1.tank.isAlive) {
                    UI.updateScore(0);
                } else {
                    UI.updateScore(1);
                }
            }
            Main.game.resetGame();
            Main.game.twoPlayer();
        });
        
        scoreTimer.setRepeats(false);
        scoreTimer.start();
    }

    private void setupKeyBindings(int id) {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        if (id == 1) {
            inputMap.put(KeyStroke.getKeyStroke("pressed E"), "forwardPressed");
            inputMap.put(KeyStroke.getKeyStroke("released E"), "forwardReleased");
            inputMap.put(KeyStroke.getKeyStroke("pressed D"), "backwardPressed");
            inputMap.put(KeyStroke.getKeyStroke("released D"), "backwardReleased");
            inputMap.put(KeyStroke.getKeyStroke("pressed S"), "leftPressed");
            inputMap.put(KeyStroke.getKeyStroke("released S"), "leftReleased");
            inputMap.put(KeyStroke.getKeyStroke("pressed F"), "rightPressed");
            inputMap.put(KeyStroke.getKeyStroke("released F"), "rightReleased");
            inputMap.put(KeyStroke.getKeyStroke("pressed Q"), "fire");
        }
        else if (id == 2) {
            inputMap.put(KeyStroke.getKeyStroke("pressed UP"), "forwardPressed");
            inputMap.put(KeyStroke.getKeyStroke("released UP"), "forwardReleased");
            inputMap.put(KeyStroke.getKeyStroke("pressed DOWN"), "backwardPressed");
            inputMap.put(KeyStroke.getKeyStroke("released DOWN"), "backwardReleased");
            inputMap.put(KeyStroke.getKeyStroke("pressed LEFT"), "leftPressed");
            inputMap.put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");
            inputMap.put(KeyStroke.getKeyStroke("pressed RIGHT"), "rightPressed");
            inputMap.put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");
            inputMap.put(KeyStroke.getKeyStroke("pressed ENTER"), "fire");
        }

        actionMap.put("forwardPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tank.isForward = true;
            }
        });
        actionMap.put("forwardReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tank.isForward = false;
            }
        });

        actionMap.put("backwardPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tank.isBackward = true;
            }
        });
        actionMap.put("backwardReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tank.isBackward = false;
            }
        });

        actionMap.put("leftPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tank.isLeft = true;
            }
        });
        actionMap.put("leftReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tank.isLeft = false;
            }
        });

        actionMap.put("rightPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tank.isRight = true;
            }
        });

        actionMap.put("rightReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tank.isRight = false;
            }
        });

        actionMap.put("fire", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tank.isFire = true;
            }
        });

    }

    void updateTankState() {
        if (tank.isForward) {
            tank.moveForward();
        }
        if (tank.isBackward) {
            tank.moveBackward();
        }
        if (tank.isLeft) {
            tank.turnLeft();
        }
        if (tank.isRight) {
            tank.turnRight();
        }
        if (tank.isFire) {
            tank.fire();
            tank.isFire = false;
        }
    }
}

class BulletController extends JPanel {
    Queue<Bullet> bulletPool = new LinkedList<>();
    ArrayList<Bullet> activeBullets;
    long previousTime = System.nanoTime();
    BufferedImage bulletImage;

    BulletController() {
        this.activeBullets = new ArrayList<>();
        setOpaque(false);
        setDoubleBuffered(true);

        createBullet();

        Timer bulletTimer = new Timer(1000 / 60, _ -> updateBullet(System.nanoTime()));
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
                if (bullet.id == 1) {
                    Controller.tank1.tank.getBullet();
                } else if (bullet.id == 2) {
                    Controller.tank2.tank.getBullet();
                }
            }
        }
        activeBullets.removeAll(bulletsToRemove);
        updateBulletState();
    }

    void addBullet(Bullet bullet, int id) {
        Bullet reusedBullet = bulletPool.poll();
        if (reusedBullet == null) {
            reusedBullet = new Bullet(bullet.x, bullet.y, bullet.angle, id);
        } else {
            reusedBullet.reset(bullet.x, bullet.y, bullet.angle, id);
        }
        activeBullets.add(reusedBullet);
    }

    void updateBulletState() {
        for (Bullet bullet : activeBullets) {
            bullet.move();
        }
        repaint();
    }

    void createBullet() {
        int size = 12;
        bulletImage = new BufferedImage(size * 2, size * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bulletImage.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.translate(size / 2, size / 2);
        g2.scale(2, 2);
        g2.setColor(Color.black);
        g2.fillOval(0, 0, 6, 6);

        g2.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Bullet bullet : activeBullets) {
            AffineTransform oldTransform = g2.getTransform();

            g2.translate(bullet.x, bullet.y);
            g2.rotate(-Math.toRadians(bullet.angle + 45));

            g2.scale(0.5, 0.5);
            g2.drawImage(bulletImage, -6, -6, null);

            g2.setTransform(oldTransform);
        }
    }
}