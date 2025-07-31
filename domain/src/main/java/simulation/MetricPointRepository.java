package simulation;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MetricPointRepository extends JpaRepository<MetricPoint, Long> {

    // Le plus récent
    Optional<MetricPoint> findTopByOrderByTimestampDesc();

    // Les 100 plus récents
    List<MetricPoint> findTop100ByOrderByTimestampDesc();

    // Tous les points pour un run donné
    List<MetricPoint> findByRunIdOrderByTimestampAsc(Long runId);
}

