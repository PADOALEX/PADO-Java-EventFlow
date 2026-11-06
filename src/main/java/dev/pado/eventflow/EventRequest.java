package dev.pado.eventflow;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventRequest(
    @NotBlank @Size(max=120) String name,
    @Size(max=2000) String description,
    @NotNull LocalDate date,
    LocalTime startTime,
    @NotBlank @Size(max=160) String location,
    @NotNull EventStatus status
) {
    // Omitted status follows the original API default; explicit null is normalized too.
    public EventRequest { if (status==null) status=EventStatus.Planned; }
    @AssertTrue(message="Date must be after 0001-01-01")
    public boolean isValidDate() { return date==null || date.isAfter(LocalDate.of(1,1,1)); }
}
