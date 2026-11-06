package dev.pado.eventflow;

import java.util.UUID;
public final class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(UUID id) { super("Event not found: " + id); }
}
