package bizsocket.core;

import bizsocket.logger.Logger;
import bizsocket.logger.LoggerFactory;
import bizsocket.tcp.Packet;

/**
 * Created by tong on 16/10/5.
 */
public class CacheInterceptor implements Interceptor {
    private final Logger logger = LoggerFactory.getLogger(CacheInterceptor.class.getSimpleName());

    private final CacheManager cacheManager;

    public CacheInterceptor(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean postRequestHandle(RequestContext context) throws Exception {
        CacheEntry cacheEntry = cacheManager.get(context.getRequestPacket());
        Packet cachedPacket = null;
        if (cacheEntry != null && (cachedPacket = cacheEntry.getEntry()) != null) {
            //如果没有过期直接拦截请求,并把cache信息分发回去
            //如果cache过期,并且CacheEntry的type是TYPE_EXPIRED_USE_AND_REFRESH先把把cache信息分发回去，在到服务器上请求
            if (!cacheEntry.isExpired()) {
                logger.debug("Use cache packet " + cachedPacket);
                context.sendSuccessMessage(context.getRequestCommand(),null,cacheEntry.getEntry());
                return true;
            }
            else if (cacheEntry.getType() == CacheEntry.TYPE_EXPIRED_USE_AND_REFRESH) {
                logger.debug("Use cache packet and refresh " + cachedPacket);
                context.sendSuccessMessage(context.getRequestCommand(),null,cacheEntry.getEntry());
            }
        }
        return false;
    }

    @Override
    public boolean postResponseHandle(int command, Packet responsePacket) throws Exception {
        cacheManager.onReceivePacket(responsePacket);
        return false;
    }
}