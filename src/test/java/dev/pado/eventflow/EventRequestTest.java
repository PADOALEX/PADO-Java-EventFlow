package dev.pado.eventflow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class EventRequestTest {
    @Test void validRequestAndDefaultStatus() {
        try (var factory=Validation.buildDefaultValidatorFactory()) {
            var r=new EventRequest("Meetup",null,LocalDate.of(2026,12,1),null,"Lviv",null);
            assertTrue(factory.getValidator().validate(r).isEmpty());
            assertEquals(EventStatus.Planned,r.status());
        }
    }
    @Test void rejectsBlankFieldsMissingDateAndLengthLimits() {
        try (var factory=Validation.buildDefaultValidatorFactory()) {
            var v=factory.getValidator();
            assertEquals(3,v.validate(new EventRequest(" ",null,null,null," ",EventStatus.Planned)).size());
            assertFalse(v.validate(new EventRequest("x".repeat(121),null,LocalDate.now(),null,"Lviv",EventStatus.Planned)).isEmpty());
            assertFalse(v.validate(new EventRequest("Valid","x".repeat(2001),LocalDate.now(),null,"Lviv",EventStatus.Planned)).isEmpty());
        }
    }
    @Test void rejectsDotnetMinimumDate() {
        try (var f=Validation.buildDefaultValidatorFactory()) {
            assertFalse(f.getValidator().validate(new EventRequest("Meetup",null,LocalDate.of(1,1,1),null,"Lviv",EventStatus.Planned)).isEmpty());
        }
    }
}
