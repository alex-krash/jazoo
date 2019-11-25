package dev.vbabaev.tools.jazoo.command;

import dev.vbabaev.tools.jazoo.ConnectionPool;
import dev.vbabaev.tools.jazoo.PathResolver;
import dev.vbabaev.tools.jazoo.ZooKeeperConnection2;
import dev.vbabaev.tools.jazoo.ZooKeeperPoolable;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class CommandTouch {

    private ConnectionPool pool;
    private PathResolver resolver;

    public CommandTouch(ConnectionPool pool, PathResolver resolver) {
        this.pool = pool;
        this.resolver = resolver;
    }

    @ShellMethod(key = "touch", value = "Create new file or updates mtime for the existing file")
    public void touch(
            @ShellOption(value = {"-e", "--ephemeral"}, arity = 0, defaultValue = "false") boolean ephemeral,
            @ShellOption(value = {"-s", "--sequential"}, arity = 0, defaultValue = "false") boolean sequential,
            @ShellOption(value = "") String path
    ) throws KeeperException, InterruptedException {
        try (ZooKeeperPoolable connection = pool.getConnection()) {
            final CreateMode mode;
            if (ephemeral) {
                mode = sequential ? CreateMode.EPHEMERAL_SEQUENTIAL : CreateMode.EPHEMERAL;
            } else {
                mode = sequential ? CreateMode.PERSISTENT_SEQUENTIAL : CreateMode.PERSISTENT;
            }
            connection.create(path, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
        }
    }
}
