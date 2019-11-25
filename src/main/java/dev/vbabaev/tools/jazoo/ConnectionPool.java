package dev.vbabaev.tools.jazoo;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class ConnectionPool {

    private final GenericObjectPool<ZooKeeperPoolable> pool;

    public ConnectionPool(@Value("${server:localhost}") String connectionString, @Value("${timeout:10000}") int timeOut, @Value("${max-connections:10}") int maxConnections) {
        PoolableConnectionFactory factory = new PoolableConnectionFactory(connectionString, timeOut);

        GenericObjectPoolConfig<ZooKeeperPoolable> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(maxConnections);

        GenericObjectPool<ZooKeeperPoolable> pool = new GenericObjectPool<>(factory, config);
        factory.setPool(pool);
        this.pool = pool;
    }

    public ZooKeeperPoolable getConnection() throws InterruptedException {
        try {
            return pool.borrowObject();
        } catch (Exception e) {
            throw new InterruptedException(e.getMessage());
        }
    }

}
