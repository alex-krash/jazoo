package dev.vbabaev.tools.jazoo.command;

import dev.vbabaev.tools.jazoo.ConnectionPool;
import dev.vbabaev.tools.jazoo.ListChildrenValueProvider;
import dev.vbabaev.tools.jazoo.PathResolver;
import dev.vbabaev.tools.jazoo.ZooKeeperPoolable;
import org.apache.zookeeper.KeeperException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
public class CommandList {

    private ConnectionPool pool;
    private PathResolver resolver;

    public CommandList(ConnectionPool pool, PathResolver resolver) {
        this.pool = pool;
        this.resolver = resolver;
    }

    @ShellMethod(key = "ls", value = "Returns a list of sub-nodes from the given node")
    public List<String> list(@ShellOption(value = "", defaultValue = "", valueProvider = ListChildrenValueProvider.class) String path
    ) throws KeeperException, InterruptedException {

        if (path.equals("")) {
            path = resolver.getCurrent();
        }
        String resolved_name = resolver.resolve(path);

        try (ZooKeeperPoolable connection = pool.getConnection()) {
            return connection.listChildren(resolved_name).stream().sorted().map(PathResolver::filename).collect(Collectors.toList());
        }
    }
}
