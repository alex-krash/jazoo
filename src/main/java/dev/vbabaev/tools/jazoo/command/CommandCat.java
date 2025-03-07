package dev.vbabaev.tools.jazoo.command;

import dev.vbabaev.tools.jazoo.ConnectionPool;
import dev.vbabaev.tools.jazoo.PathResolver;
import dev.vbabaev.tools.jazoo.ZooKeeperConnection2;
import dev.vbabaev.tools.jazoo.ZooKeeperPoolable;
import org.apache.zookeeper.KeeperException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class CommandCat {

    private final ConnectionPool pool;
    private PathResolver resolver;

    public CommandCat(ConnectionPool pool, PathResolver resolver) {
        this.pool = pool;
        this.resolver = resolver;
    }

    @ShellMethod(key = "cat", value = "Returns contents of the given node")
    public String cat(@ShellOption(value = "") String path
    ) throws KeeperException, InterruptedException {

        try (ZooKeeperPoolable connection = pool.getConnection()) {
            return new String(connection.getData(resolver.resolve(path), false, null));
        }
    }
}
