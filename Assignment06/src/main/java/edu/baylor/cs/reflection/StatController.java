package edu.baylor.cs.reflection;

import edu.baylor.cs.reflection.model.Details;
import edu.baylor.cs.reflection.model.Summery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class StatController {
    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @GetMapping("/summery")
    public Summery getSummery() {
        return statService.getSummery();
    }

    @GetMapping("/details")
    public Details getDetails() {
        return statService.getDetails();
    }
}
