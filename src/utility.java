import java.io.Serializable;
import java.util.*;

public class utility {
    static int rand(int low, int high) {
        return (int) (Math.random() * (high - low + 1) + low);
    }
}

class Pair<K, V> {
    K key;
    V value;

    Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    K getKey() {
        return key;
    }

    V getValue() {
        return value;
    }

    void setKey(K key) {
        this.key = key;
    }

    void setValue(V value) {
        this.value = value;
    }

    void set(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Pair<?, ?> other = (Pair<?, ?>) obj;
        return Objects.equals(key, other.key) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}

class Point {
    int[] block = new int [4]; // 上, 右, 下, 左 (默认墙都存在)
    Point() {
        for (int i = 0; i < 4; i++)
            block[i] = 1;
    }
}

class Block {
    int where = -1;
    Pair<Integer, Integer> a, b;
    Block(int x1, int y1, int x2, int y2, int w) {
        a = new Pair<Integer, Integer>(x1, y1);
        b = new Pair<Integer, Integer>(x2, y2);
        where = w;
    }

    Pair<Integer, Integer> getX() {
        return a;
    }

    Pair<Integer, Integer> getY() {
        return b;
    }

    int getW() {
        return where;
    }
}