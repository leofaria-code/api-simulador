package br.com.leo.apisimulador.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TelemetryService {
    
    private final Map<String, AtomicLong> requestVolume = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> totalResponseTimeNanos = new ConcurrentHashMap<>();

    public void recordResponseTime(String serviceName, Duration duration) {
        requestVolume.computeIfAbsent(serviceName, k -> new AtomicLong(0)).incrementAndGet();
        totalResponseTimeNanos.computeIfAbsent(serviceName, k -> new AtomicLong(0)).addAndGet(duration.toNanos());
    }
    
    public Map<String, Object> getTelemetryData() {
        Map<String, Object> telemetryData = new ConcurrentHashMap<>();
        for (String serviceName : requestVolume.keySet()) {
            long volume = requestVolume.get(serviceName).get();
            long totalTime = totalResponseTimeNanos.get(serviceName).get();
            double averageTimeMillis = (double) totalTime / volume / 1_000_000.0;
            telemetryData.put(serviceName + "_volume", volume);
            telemetryData.put(serviceName + "_average_response_time_ms", String.format("%.2f", averageTimeMillis));
        }
        return telemetryData;
    }
}
