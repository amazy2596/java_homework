package java_homework.src;

import java.util.*;

public class MazeGenerator {
    public static void main(String[] args) {
        MazeGenerator m = new MazeGenerator(5, 5);
        m.print();
    }

    private int rows;
    private int cols;
    private static ArrayList<ArrayList<Point>> maze;
    private ArrayList<Pair<Integer, Integer>> directions;

    MazeGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        maze = new ArrayList<>();
        for (int i = 0; i <= rows; i++) {
            ArrayList<Point> row = new ArrayList<>();
            for (int j = 0; j <= cols; j++) {
                row.add(new Point());
            }
            maze.add(row);
        }
        this.directions = new ArrayList<>();
        directions.add(new Pair(-1, 0));
        directions.add(new Pair(0, 1));
        directions.add(new Pair(1, 0));
        directions.add(new Pair(0, -1));
        generateMaze();
    }

    private void generateMaze() {
        prim(new Pair(1, 1));
    }

    private void prim(Pair<Integer, Integer> p) {
        HashSet<Pair<Integer, Integer>> visited = new HashSet<>();
        ArrayList<Block> arr = new ArrayList<>();
        visited.add(p);
        int x = p.getKey(), y = p.getValue();
        Point point = maze.get(x).get(y);
        for (int i = 0; i < 4; i++) {
            if (point.block[i] == 1) {
                int nx = x + directions.get(i).getKey();
                int ny = y + directions.get(i).getValue();
                if (isValid(nx, ny)) {
                    arr.add(new Block(x, y, nx, ny, i));
                } 
            }
        }

        while (arr.size() != 0) {
            int idx = utility.rand(0, arr.size());
            Block b = arr.get(idx);
            arr.remove(idx);
            Pair<Integer, Integer> p1 = b.getX(), p2 = b.getY();
            int x1 = p1.getKey(), y1 = p1.getValue();
            x = p2.getKey();
            y = p2.getValue();
            int w = b.getW();

            if (visited.contains(p1) && !visited.contains(p2)) {
                maze.get(x1).get(y1).block[w] = 0;
                visited.add(p2);
                for (int i = 0; i < 4; i++) {
                    if (maze.get(x).get(y).block[i] == 1) {
                        int nx = x + directions.get(i).getKey();
                        int ny = y + directions.get(i).getValue();

                        if (isValid(nx, ny)) {
                            if (nx == x1 && ny == y1) {
                                maze.get(x).get(y).block[i] = 0;
                            } else {
                                arr.add(new Block(x, y, nx, ny, i));
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isValid(int i, int j) {
        if (i < 1 || i > rows || j < 1 || j > cols)
            return false;
        return true;
    }

    public static ArrayList<ArrayList<Point>> getMaze() {
        return maze;
    }

    public void print() {
        for (int i = 1; i <= rows; i++) {
            // 打印顶部墙壁
            for (int j = 1; j <= cols; j++) {
                System.out.print("+");
                if (maze.get(i).get(j).block[0] == 1) {
                    System.out.print("---");
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println("+");
            // 打印左侧墙壁和单元格
            for (int j = 1; j <= cols; j++) {
                if (maze.get(i).get(j).block[3] == 1) {
                    System.out.print("|");
                } else {
                    System.out.print(" ");
                }
                System.out.print("   "); // 单元格内部空白
            }
            // 处理行末的右侧墙壁
            if (maze.get(i).get(cols).block[1] == 1) {
                System.out.println("|");
            } else {
                System.out.println(" ");
            }
        }
        // 打印底部墙壁
        for (int j = 1; j <= cols; j++) {
            System.out.print("+");
            if (maze.get(rows).get(j).block[2] == 1) {
                System.out.print("---");
            } else {
                System.out.print("   ");
            }
        }
        System.out.println("+");
    }
}