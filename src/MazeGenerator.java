import java.util.ArrayList;
import java.util.HashSet;

public class MazeGenerator {
    static int rows;
    static int cols;
    static ArrayList<ArrayList<Point>> maze;
    static ArrayList<Block> blocks;
    static ArrayList<Pair<Integer, Integer>> directions;

    MazeGenerator(int rows, int cols) {
        MazeGenerator.rows = rows;
        MazeGenerator.cols = cols;
        maze = new ArrayList<>();
        for (int i = 0; i <= rows; i++) {
            ArrayList<Point> row = new ArrayList<>();
            for (int j = 0; j <= cols; j++) {
                row.add(new Point());
            }
            maze.add(row);
        }
        blocks = new ArrayList<>();
        directions = new ArrayList<>();
        directions.add(new Pair<>(-1, 0));
        directions.add(new Pair<>(0, 1));
        directions.add(new Pair<>(1, 0));
        directions.add(new Pair<>(0, -1));
        generateMaze();
    }

    private void generateMaze() {
        prim(new Pair<>(1, 1));
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

        while (!arr.isEmpty()) {
            int idx = utility.rand(0, arr.size() - 1);
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
        return i >= 1 && i <= rows && j >= 1 && j <= cols;
    }
}