package dev.pado.eventflow;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {
    @Test void missingEventUsesDomainException() {
        var repository=mock(EventRepository.class);var id=UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class,()->new EventService(repository).get(id));
    }
    @Test void updatePreservesIdentityCreationTimeAndTrimsText() {
        var first=new EventRequest("Old",null,LocalDate.of(2026,12,1),null,"Lviv",EventStatus.Planned);
        var event=new Event(first);var id=event.getId();var created=event.getCreatedAtUtc();
        var repository=mock(EventRepository.class);when(repository.findById(id)).thenReturn(Optional.of(event));
        var result=new EventService(repository).update(id,new EventRequest(" New ","   ",first.date(),null," Online ",EventStatus.Completed));
        assertEquals(id,result.id());assertEquals(created,result.createdAtUtc());assertEquals("New",result.name());
        assertEquals("Online",result.location());assertNull(result.description());assertEquals(EventStatus.Completed,result.status());
        verify(repository,never()).save(any()); // Managed entities are flushed at the transaction boundary.
    }
    @Test void rejectsUnrecognizedSortBeforeQueryingDatabase() {
        var repository=mock(EventRepository.class);
        assertThrows(IllegalArgumentException.class,()->new EventService(repository).list(null,null,"id;drop table events","asc",1,10));
        verifyNoInteractions(repository);
    }
}
