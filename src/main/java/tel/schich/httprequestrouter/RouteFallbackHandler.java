package tel.schich.httprequestrouter;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface RouteFallbackHandler<Verb, In, Out> {
    CompletableFuture<Out> handle(String matchedPrefix, RouteTree<Verb, In, Out> subTree, In request);
}
