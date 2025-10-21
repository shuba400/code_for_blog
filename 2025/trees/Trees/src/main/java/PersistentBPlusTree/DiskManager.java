package PersistentBPlusTree;

import PersistentBPlusTree.Serializer.Serializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;


/*
<root><last_offset><Node1><Node2><Node3><Node4>.....
 */

public class DiskManager<K,V> implements AutoCloseable {
    private final static String dbFilePath = "/Users/singh1/personal_dev/code_for_blog/2025/trees/Trees/src/main/resources/file.txt";
    private static final Integer pageSize = 80;
    private Integer lastOffset;
    private Integer rootOffset;
    private final Integer headerSize;
    private final FileChannel fileChannel;
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;

    public DiskManager(Serializer<K> keySerializer, Serializer<V> valueSerializer) throws IOException {
        Path path = Paths.get(dbFilePath);
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.fileChannel = FileChannel.open(path, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.READ));
        System.out.printf("File Channel Opened: %s\n", path.getFileName());
        headerSize = 4;
        lastOffset = -1;
        rootOffset = -1;
        readHeader();
        System.out.printf("Root Node - %d, LastOffset - %d\n",rootOffset,lastOffset);
    }

    void readHeader() throws IOException {
        System.out.printf("File - %d, Header - %d\n",fileChannel.size(),headerSize);
        if (fileChannel.size() < headerSize){
            ByteBuffer byteBuffer = ByteBuffer.allocate(headerSize);
            byteBuffer.putInt(-1); //root
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            return;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(headerSize);
        fileChannel.read(byteBuffer);
        byteBuffer.flip();
        this.rootOffset = byteBuffer.getInt();
        this.lastOffset = Math.toIntExact(Math.floorDiv((fileChannel.size() - headerSize), pageSize));
        return;
    }

    public Node<K,V> getRootNode() throws IOException{
        if (rootOffset == -1){
            return null;
        }
        return getNode(rootOffset);
    }

    void updateRootNode(final Node<K,V> node) throws IOException {
        this.rootOffset = node.currentOffset;
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(this.rootOffset);
        byteBuffer.flip();
        fileChannel.position(0);
        fileChannel.write(byteBuffer);
    }

    boolean needSplit(Node<K,V> node) throws IOException {
        ByteBuffer byteBuffer = NodeSerializer.serialize(node,keySerializer,valueSerializer,pageSize);
        return byteBuffer.remaining() < (pageSize / 5);
    }

    public Node<K,V> getNode(Integer offset) throws IOException {
        fileChannel.position(headerSize + (long) offset * pageSize);
        ByteBuffer byteBuffer = ByteBuffer.allocate(pageSize);
        fileChannel.read(byteBuffer);
        byteBuffer.flip();
        return NodeSerializer.deserialize(byteBuffer,offset,keySerializer,valueSerializer);
    }


    public Integer getNewOffset(){
        lastOffset = lastOffset + 1;
        return lastOffset;
    }

    public void addNode(Node<K,V> node) throws IOException {
        assert (node.currentOffset != null);
        ByteBuffer byteBuffer = NodeSerializer.serialize(node,keySerializer,valueSerializer,pageSize);
        byteBuffer.flip();
        fileChannel.position(headerSize + (long) node.currentOffset *pageSize);
        fileChannel.write(byteBuffer);
    }

    @Override
    public void close() throws IOException {
        if(this.fileChannel != null && this.fileChannel.isOpen()){
            fileChannel.close();
        }
    }
}
