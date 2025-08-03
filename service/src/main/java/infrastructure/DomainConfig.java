package infrastructure;

import config.ConfigRepository;
import config.SimulationConfig;
import controller.PopulateController;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import service.*;
import simulation.MetricPointRepository;
import simulation.SimulationRepository;

@Configuration
@EnableScheduling
public class DomainConfig {

    @Bean
    public SimulationConfig config(){
        return new SimulationConfig();
    }

    @Bean
    public ConfigService configService(SimulationConfig config, DBManagerService dbManagerService, InjectionService injectionService) {
        return new ConfigService(config, dbManagerService, injectionService);
    }

    @Bean
    public DBManagerService dbManagerService(SimulationConfig config) {
        return new DBManagerService(config);
    }

    @Bean
    public InjectionService injectionService(SimulationConfig config) {
        return new InjectionService(config);
    }

    @Bean
    public SimulationService simulationService(ConfigService configService, MetricsService metricsService) {
        return new SimulationService(configService, metricsService);
    }

    @Bean
    public PopulateController populateController(SimulationRepository rep){
        return new PopulateController(rep);
    }

    @Bean
    public PrometheusMetricsFetcher prometheusMetricsFetcher(MeterRegistry meterRegistry) {
        return new PrometheusMetricsFetcher(meterRegistry);
    }

    @Bean
    public MetricsService metricsService(
            SimulationRepository simulationRepository,
            ConfigRepository configRepository,
            ConfigService configService
    ) {
        return new MetricsService(simulationRepository, configRepository, configService);
    }
}
