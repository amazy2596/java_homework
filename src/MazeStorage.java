package java_homework.src;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public record MazeStorage(ArrayList<ArrayList<Point>> maze, int x, int y, int rowNum,
                          int colNum) implements Serializable {
    public static boolean storageMaze(int x, int y, ArrayList<ArrayList<Point>> maze, int rowNum, int colNum) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("maze.ser"))) {
            outputStream.writeObject(new MazeStorage(maze, x, y, rowNum, colNum));
            return true;
        } catch (IOException e) {
            System.out.println("进度保存失败!");
        }
        return false;
    }

    public static MazeStorage getMageStorage() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("maze.ser"))) {
            return (MazeStorage) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("进度读取失败!");
        }
        return null;
    }
}
