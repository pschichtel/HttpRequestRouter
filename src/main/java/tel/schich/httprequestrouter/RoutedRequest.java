package tel.schich.httprequestrouter;

import java.util.Map;

public class RoutedRequest<TRequest> {
    private final Map<String, String> routeParameters;
    private final TRequest request;

    public RoutedRequest(Map<String, String> routeParameters, TRequest request) {
        this.routeParameters = routeParameters;
        this.request = request;
    }

    public Map<String, String> getRouteParameters() {
        return routeParameters;
    }

    public TRequest getRequest() {
        return request;
    }
}
