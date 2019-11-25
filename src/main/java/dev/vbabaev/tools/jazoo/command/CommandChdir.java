package dev.vbabaev.tools.jazoo.command;

import dev.vbabaev.tools.jazoo.ConnectionPool;
import dev.vbabaev.tools.jazoo.ListChildrenValueProvider;
import dev.vbabaev.tools.jazoo.PathResolver;
import dev.vbabaev.tools.jazoo.ZooKeeperPoolable;
import org.apache.zookeeper.KeeperException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.management.relation.RoleUnresolved;

@ShellComponent
public class CommandChdir {

    private ConnectionPool pool;
    private PathResolver resolver;

    public CommandChdir(ConnectionPool pool, PathResolver resolver) {
        this.pool = pool;
        this.resolver = resolver;
    }

    @ShellMethod(key = "cd", value = "Changes current dir")
    public void cd(@ShellOption(value = "", defaultValue = "", valueProvider = ListChildrenValueProvider.class) String path) throws KeeperException, InterruptedException {
        String real_path = resolver.resolve(path);

        try (ZooKeeperPoolable connection = pool.getConnection()) {
            if (null != connection.exists(real_path, false)) {
                resolver.set(real_path);
            } else {
                throw new RuntimeException("Node '" + real_path + "' doesn't exist");
            }
        }
    }
}
