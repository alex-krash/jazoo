package dev.vbabaev.tools.jazoo;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Scope("singleton")
public class ZooKeeperConnection2 {

    private final SimpleDateFormat formatter_ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final ZooKeeper keeper;

    public ZooKeeperConnection2(@Value("${server:localhost}") String server, @Value("${timeout:10000}") int timeout) throws IOException {
        System.out.println("Zookeeper server:" + server);
        System.out.println("Zookeeper connection timeout:" + timeout);
        this.keeper = new ZooKeeper(server, timeout, (a) -> {
        });
        try {
            this.keeper.exists("/", (a) -> {
            });
        } catch (Exception e) {
            throw new RuntimeException("Cannot connect to " + server);
        }

    }

    public void touch(final String path, boolean ephemeral, boolean sequential) throws KeeperException, InterruptedException {
        final CreateMode mode;
        if (ephemeral) {
            mode = sequential ? CreateMode.EPHEMERAL_SEQUENTIAL : CreateMode.EPHEMERAL;
        } else {
            mode = sequential ? CreateMode.PERSISTENT_SEQUENTIAL : CreateMode.PERSISTENT;
        }
        this.keeper.create(path, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
    }

    public List<String> list(String path) throws KeeperException, InterruptedException {
        List<String> children = listChildren(path);
        if (children.size() > 0) {
            return children;
        } else {
            return Collections.singletonList(path);
        }
    }

    public List<String> listChildren(String path) throws KeeperException, InterruptedException {
        try {
            List<String> children = this.keeper.getChildren(path, (w) -> {
            });
            List<String> result = new ArrayList<>();
            children.forEach(name -> result.add(PathResolver.join(path, name)));
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public void delete(String path) throws KeeperException, InterruptedException {
        this.keeper.delete(path, -1);
    }

    public String stat(String path) throws KeeperException, InterruptedException {
        Stat stat = this.keeper.exists(path, (w) -> {
        });

        return stat.getDataLength() + "\t" + stat.getNumChildren() + "\t" + formatter_ts.format(new Date(stat.getMtime()));
    }

    public boolean nodeExists(String path) throws KeeperException, InterruptedException {
        return null != this.keeper.exists(path, (x) -> {
        });
    }

    public String data(String path) throws KeeperException, InterruptedException {
        return new String(this.keeper.getData(path, false, null));
    }
}
