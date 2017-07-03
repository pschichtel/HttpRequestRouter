package tel.schich.httprequestrouter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestRouterTest {

    @Test
    void testRouter() {
        HttpRequestRouter router = new HttpRequestRouter();
        assertNotNull(router);
    }
}