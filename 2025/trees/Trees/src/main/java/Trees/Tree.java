package Trees;

public interface Tree<T,V> {
    String getName();
    boolean insert(T key,V value);
    boolean delete(T key);
    V get(T key);
    void printTree();
}
