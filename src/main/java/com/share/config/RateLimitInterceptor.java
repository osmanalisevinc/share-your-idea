package com.share.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, RequestInfo> ipRequestMap = new ConcurrentHashMap<>();
    private final int LIMIT = 50; // Dakikada 5 istek
    private final long WINDOW_MILLIS = 60 * 1000; // 1 dakika

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String ip = request.getRemoteAddr();
        long now = Instant.now().toEpochMilli();

        RequestInfo requestInfo = ipRequestMap.getOrDefault(ip, new RequestInfo(0, now));
        if (now - requestInfo.startTime > WINDOW_MILLIS) {
            // Yeni pencere başlat
            requestInfo = new RequestInfo(0, now);
        }

        if (requestInfo.count >= LIMIT) {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded. Try again later.");
            return false;
        }

        requestInfo.count++;
        ipRequestMap.put(ip, requestInfo);
        return true;
    }

    private static class RequestInfo {
        int count;
        long startTime;

        RequestInfo(int count, long startTime) {
            this.count = count;
            this.startTime = startTime;
        }
    }
}