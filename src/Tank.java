import java.util.*;

public class Tank {
    int x, y;
    int rows, cols;
    int cellSize;

    Tank(int cellSize, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cellSize = cellSize;

        this.x =  utility.rand(0, rows + 1);
        this.y =  utility.rand(0, cols + 1);

    }

    public void moveUp() {
        if (this.x > 0) {
            this.x--;
        }
    }

    public void moveDown() {
        if (this.x < this.rows - 1) {
            this.x++;
        }
    }

    public void moveLeft() {
        if (this.y > 0) {
            this.y--;
        }
    }

    public void moveRight() {
        if (this.y < this.cols - 1) {
            this.y++;
        }
    }

}
