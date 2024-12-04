package java_homework.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class ui {
    public static void main(String[] args) {

    }

    ui(int rows, int cols) {
        JFrame frame = new JFrame("tank battle");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        File saveFile = new File("maze.ser");
        Optional<MazeStorage> maze = Optional.empty();
        if (saveFile.exists()) {
            int result = JOptionPane.showConfirmDialog(
                    frame,
                    "发现存档，是否读取?",
                    "读取存档",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                maze = Optional.ofNullable(MazeStorage.getMageStorage());
                if (maze.isPresent()) {
                    rows = maze.get().rowNum();
                    cols = maze.get().colNum();
                }
            }
        }

        int cellSize = 50;
        int frameWidth = cols * cellSize + 50;
        int frameHeight = rows * cellSize + 70;
        frame.setSize(frameWidth, frameHeight);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        frame.setLocation((screenWidth - frameWidth) / 2, (screenHeight - frameHeight) / 2);

        Panel p = new Panel(cellSize, rows, cols);
        maze.ifPresent(x -> p.maze = x.maze());

        int finalRows = rows;
        int finalCols = cols;
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        frame,
                        "是否要保存进度?",
                        "保存进度",
                        JOptionPane.YES_NO_OPTION
                );
                if (result == JOptionPane.YES_OPTION) {
                    if (MazeStorage.storageMaze(0, 0, p.maze, finalRows, finalCols)) {
                        frame.dispose();
                    }
                    MazeStorage.storageMaze(0, 0, p.maze, finalRows, finalCols);
                } else if (result == JOptionPane.NO_OPTION) {
                    frame.dispose();
                    File file = new File("maze.ser");
                    file.delete();
                }
            }
        });
        frame.add(p);
        frame.setVisible(true);
    }
    
}

class Panel extends JPanel {
    ArrayList<ArrayList<Point>> maze;
    int cellSize, rows, cols;
    int offsetX = 0;
    int offsetY = 0;

    Panel(int cellSize, int rows, int cols) {
        this.maze = MazeGenerator.getMaze();
        this.cellSize = cellSize;
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.black);
        g2.setStroke(new BasicStroke(4));


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