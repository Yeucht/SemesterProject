package simulation;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MetricPointRepository extends JpaRepository<MetricPoint, Long> {

    Optional<MetricPoint> findTopByOrderByTimestampDesc();

    List<MetricPoint> findTop100ByOrderByTimestampDesc();

    List<MetricPoint> findByRunIdOrderByTimestampAsc(Long runId);
}

