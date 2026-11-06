package dev.pado.eventflow;

import jakarta.persistence.*;
import java.time.*;
import java.util.UUID;

@Entity
@Table(name="events")
public class Event {
    @Id private UUID id;
    @Column(nullable=false,length=120) private String name;
    @Column(length=2000) private String description;
    @Column(name="event_date",nullable=false) private LocalDate date;
    @Column(name="start_time") private LocalTime startTime;
    @Column(nullable=false,length=160) private String location;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private EventStatus status;
    @Column(name="created_at_utc",nullable=false,updatable=false) private Instant createdAtUtc;
    @Column(name="updated_at_utc",nullable=false) private Instant updatedAtUtc;

    protected Event() { }
    Event(EventRequest request) {
        id=UUID.randomUUID();createdAtUtc=Instant.now();apply(request);
    }
    void apply(EventRequest request) {
        name=request.name().trim();location=request.location().trim();
        description=request.description()==null || request.description().isBlank() ? null : request.description().trim();
        date=request.date();startTime=request.startTime();status=request.status();updatedAtUtc=Instant.now();
    }
    void complete() { status=EventStatus.Completed;updatedAtUtc=Instant.now(); }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public String getLocation() { return location; }
    public EventStatus getStatus() { return status; }
    public Instant getCreatedAtUtc() { return createdAtUtc; }
    public Instant getUpdatedAtUtc() { return updatedAtUtc; }
}
