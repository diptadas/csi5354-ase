package edu.baylor.cs.demo;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class DemoService {
    private final MeterRegistry registry;
    private final AtomicInteger productionGauge;


    public DemoService(MeterRegistry registry) {
        this.registry = registry;
        productionGauge = registry.gauge("car.production", new AtomicInteger(0));
    }

    public void incrementOrderCount(String type) {
        registry.counter("car.orders", "type", type).increment();
    }

    public void setProductionValue(int value) {
        productionGauge.set(value);
    }
}
