package edu.baylor.cs.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/order/{type}")
    public String order(@PathVariable String type) {
        demoService.incrementOrderCount(type);
        return "car type: " + type;
    }

    @GetMapping("/production/{value}")
    public String production(@PathVariable int value) {
        demoService.setProductionValue(value);
        return "car production value: " + value;
    }
}
