package dev.vbabaev.tools.jazoo;

import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ValueProvider;
import org.springframework.shell.standard.ValueProviderSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListChildrenValueProvider extends ValueProviderSupport {

    private ConnectionPool pool;
    private PathResolver resolver;

    public ListChildrenValueProvider(ConnectionPool pool, PathResolver resolver) {
        this.pool = pool;
        this.resolver = resolver;
    }

    @Override
    public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext completionContext, String[] hints) {

        String input = completionContext.currentWordUpToCursor();
        int lastSlash = input.lastIndexOf("/");
        String dir = lastSlash > -1 ? input.substring(0, lastSlash+1) : "";
        String prefix = input.substring(lastSlash + 1, input.length());

        try (ZooKeeperPoolable conn = pool.getConnection()) {
            String parentDir = resolver.resolve(dir);
            List<String> strings = conn.getChildren(parentDir, false);
            List<CompletionProposal> retval = strings.stream().filter(s -> s.startsWith(prefix)).map(s -> new CompletionProposal(dir + s)).collect(Collectors.toList());
            return retval;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }
}
