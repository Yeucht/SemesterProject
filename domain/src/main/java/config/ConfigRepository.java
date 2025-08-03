package config;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ConfigRepository extends JpaRepository<SimulationConfig, Long> {
}
