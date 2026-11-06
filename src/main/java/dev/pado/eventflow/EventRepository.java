package dev.pado.eventflow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event,UUID>,JpaSpecificationExecutor<Event> {
    interface Counts { long getTotal(); Long getPlanned(); }
    @Query("select count(e) as total, sum(case when e.status = :planned then 1 else 0 end) as planned from Event e")
    Counts counts(@Param("planned") EventStatus planned);
}
