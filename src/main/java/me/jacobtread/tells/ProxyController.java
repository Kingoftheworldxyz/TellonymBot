package me.jacobtread.tells;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import net.lightbody.bmp.BrowserMobProxyServer;
import org.openqa.selenium.Proxy;

import java.net.InetSocketAddress;

public class ProxyController {

    public static BrowserMobProxyServer PROXY;
    private static boolean isSetup = false;
    private static Proxy seleniumProxy;

    /**
     * If the proxy server isn't set up then setup will be run
     *
     * @return The proxy for selenium to use
     */
    public static Proxy getSeleniumProxy() {
        if (!isSetup) {
            setup();
        }
        if (seleniumProxy == null) {
            seleniumProxy = new Proxy();
            seleniumProxy.setHttpProxy("localhost:" + PROXY.getPort());
            seleniumProxy.setSslProxy("localhost:" + PROXY.getPort());
        }
        return seleniumProxy;
    }

    /**
     * Sets up the proxy server and the request filter (For faster page loading)
     */
    private static void setup() {
        PROXY = new BrowserMobProxyServer();
        PROXY.addRequestFilter((request, contents, messageInfo) -> {
            String url = messageInfo.getUrl();
            if (
                    url.contains("c.amazon-adsystem.com")
                            || url.contains("securepubads.g.doubleclick.net")
                            || url.endsWith(".css")
                            || url.endsWith(".png")
                            || url.endsWith(".jpg")
                            || url.contains("apex.go.sonobi.com")
                            || url.contains("ads.pubmatic.com")
                            || url.contains("facebook.com")
                            || url.contains("google-analytics")
            ) {
                return new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.NOT_FOUND);
            }
            return null;
        });
        PROXY.start();
        isSetup = true;
        Runtime.getRuntime().addShutdownHook(new Thread(PROXY::abort));
    }

    /**
     * Tells the proxy server to use a second proxy at the provided address
     *
     * @param address The chained proxy
     */
    public static void setChainedProxy(InetSocketAddress address) {
        PROXY.setChainedProxy(address);
    }
}
