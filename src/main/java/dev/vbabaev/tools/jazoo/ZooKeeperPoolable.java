package dev.vbabaev.tools.jazoo;

import org.apache.commons.pool2.ObjectPool;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Poolable object, that performs returning of itself to pool, instead of real closing
 */
public class ZooKeeperPoolable extends ZooKeeper implements AutoCloseable {

    private final ObjectPool<ZooKeeperPoolable> pool;

    public ZooKeeperPoolable(String connectString, int sessionTimeout, ObjectPool<ZooKeeperPoolable> pool) throws IOException {
        super(connectString, sessionTimeout, event -> {
        });
        this.pool = pool;
    }

    @Override
    public synchronized void close() throws InterruptedException {
        try {
            pool.returnObject(this);
        } catch (Exception e) {
            throw new InterruptedException(e.getMessage());
        }
    }

    public List<String> listChildren(String path) throws KeeperException, InterruptedException {
        List<String> children = getChildren(path, false);
        return children.stream().map(s -> PathResolver.join(path, s)).collect(Collectors.toList());
    }

    void reallyClose() throws InterruptedException {
        super.close();
    }
}
