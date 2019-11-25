package dev.vbabaev.tools.jazoo;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

class PoolableConnectionFactory implements PooledObjectFactory<ZooKeeperPoolable> {

    private ObjectPool<ZooKeeperPoolable> pool;

    private final String connectionString;
    private final int sessionTimeout;

    public PoolableConnectionFactory(String connectionString, int sessionTimeout) {
        this.connectionString = connectionString;
        this.sessionTimeout = sessionTimeout;
    }

    public void setPool(ObjectPool<ZooKeeperPoolable> pool) {
        this.pool = pool;
    }

    @Override
    public PooledObject<ZooKeeperPoolable> makeObject() throws Exception {
        return new DefaultPooledObject<>(new ZooKeeperPoolable(connectionString, sessionTimeout, pool));
    }

    @Override
    public void destroyObject(PooledObject<ZooKeeperPoolable> p) throws Exception {
        p.getObject().reallyClose();
    }

    @Override
    public boolean validateObject(PooledObject<ZooKeeperPoolable> p) {
        return true;
    }

    @Override
    public void activateObject(PooledObject<ZooKeeperPoolable> p) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<ZooKeeperPoolable> p) throws Exception {

    }
}
