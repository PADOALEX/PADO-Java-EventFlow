package dev.pado.eventflow;

import java.time.*;
import java.util.UUID;

public record EventResponse(UUID id,String name,String description,LocalDate date,LocalTime startTime,
    String location,EventStatus status,Instant createdAtUtc,Instant updatedAtUtc) {
    static EventResponse from(Event e) {
        return new EventResponse(e.getId(),e.getName(),e.getDescription(),e.getDate(),e.getStartTime(),
            e.getLocation(),e.getStatus(),e.getCreatedAtUtc(),e.getUpdatedAtUtc());
    }
}
