package dev.pado.eventflow;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class HealthController {
    /** Process liveness only; startup requires DB migrations, but this is not a readiness probe. */
    @GetMapping("/health") public Map<String,String> health() { return Map.of("status","healthy"); }
}
