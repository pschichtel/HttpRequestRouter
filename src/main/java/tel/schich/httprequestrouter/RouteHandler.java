package tel.schich.httprequestrouter;

@FunctionalInterface
public interface RouteHandler<TRequest, TResponse> {
    TResponse handle(RoutedRequest<TRequest> request);
}
