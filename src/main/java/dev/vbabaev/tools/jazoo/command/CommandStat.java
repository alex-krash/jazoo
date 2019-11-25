package dev.vbabaev.tools.jazoo.command;

import dev.vbabaev.tools.jazoo.ConnectionPool;
import dev.vbabaev.tools.jazoo.PathResolver;
import dev.vbabaev.tools.jazoo.ZooKeeperPoolable;
import org.apache.zookeeper.KeeperException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class CommandStat {

    private final ConnectionPool pool;
    private final PathResolver resolver;

    public CommandStat(ConnectionPool pool, PathResolver resolver) {
        this.pool = pool;
        this.resolver = resolver;
    }

    @ShellMethod(key = "stat", value = "Returns meta-information about node")
    public String stat(@ShellOption(value = "") String path) throws KeeperException, InterruptedException {

        String resolved = resolver.resolve(path);
        try (ZooKeeperPoolable connection = pool.getConnection()) {

            return new String(connection.getData(resolver.resolve(path), false, null));
        }
    }
}
