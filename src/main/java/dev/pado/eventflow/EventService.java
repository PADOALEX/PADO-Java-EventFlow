package dev.pado.eventflow;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional(readOnly=true)
public class EventService {
    private final EventRepository repository;
    public EventService(EventRepository repository) { this.repository=repository; }
    public record PageResponse(List<EventResponse> items,long totalCount,int page,int pageSize,int totalPages) { }
    public record Stats(long total,long planned,long completed) { }

    public PageResponse list(String search,EventStatus status,String sort,String direction,int page,int pageSize) {
        if (!List.of("date","name").contains(sort) || !List.of("asc","desc").contains(direction))
            throw new IllegalArgumentException("sort must be date/name and direction must be asc/desc");
        Sort.Direction dir=direction.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort ordering=Sort.by(dir,sort);
        if (sort.equals("date")) ordering=ordering.and(Sort.by(dir,"startTime"));
        ordering=ordering.and(Sort.by("id"));
        Specification<Event> specification=(root,query,cb)->{
            var predicate=cb.conjunction();
            if (search!=null && !search.isBlank()) {
                String term=search.trim().toLowerCase(Locale.ROOT).replace("\\","\\\\").replace("%","\\%").replace("_","\\_");
                predicate=cb.and(predicate,cb.or(cb.like(cb.lower(root.get("name")),"%"+term+"%",'\\'),
                    cb.like(cb.lower(root.get("description")),"%"+term+"%",'\\')));
            }
            if (status!=null) predicate=cb.and(predicate,cb.equal(root.get("status"),status));
            return predicate;
        };
        var result=repository.findAll(specification,PageRequest.of(page-1,pageSize,ordering));
        return new PageResponse(result.getContent().stream().map(EventResponse::from).toList(),result.getTotalElements(),page,pageSize,result.getTotalPages());
    }
    public EventResponse get(UUID id) { return EventResponse.from(find(id)); }
    public Stats stats() {
        var counts=repository.counts(EventStatus.Planned);
        long planned=counts.getPlanned()==null ? 0 : counts.getPlanned();
        return new Stats(counts.getTotal(),planned,counts.getTotal()-planned);
    }
    @Transactional public EventResponse create(EventRequest request) { return EventResponse.from(repository.save(new Event(request))); }
    @Transactional public EventResponse update(UUID id,EventRequest request) {
        Event event=find(id);event.apply(request);return EventResponse.from(event);
    }
    @Transactional public EventResponse complete(UUID id) { Event event=find(id);event.complete();return EventResponse.from(event); }
    @Transactional public void delete(UUID id) { repository.delete(find(id)); }
    private Event find(UUID id) { return repository.findById(id).orElseThrow(()->new EventNotFoundException(id)); }
}
