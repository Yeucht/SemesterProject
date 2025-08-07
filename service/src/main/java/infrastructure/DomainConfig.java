package infrastructure;

import config.ConfigRepository;
import config.SimulationConfig;
import controller.PopulateController;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import service.*;
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
    public DBManagerService dbManagerService(SimulationConfig config, Counter counter) {
        return new DBManagerService(config, counter);
    }

    @Bean
    public InjectionService injectionService(SimulationConfig config, Counter counter) {
        return new InjectionService(config, counter);
    }

    @Bean
    public SimulationService simulationService(ConfigService configService, MetricsService metricsService, Counter counter) {
        return new SimulationService(configService, metricsService, counter);
    }

    @Bean
    public PopulateController populateController(SimulationRepository rep){
        return new PopulateController(rep);
    }

    @Bean
    public PrometheusService prometheusService() {
        return new PrometheusService();
    }

    @Bean
    public MetricsService metricsService(
            SimulationRepository simulationRepository,
            ConfigRepository configRepository,
            PrometheusService prometheusService,
            ConfigService configService,
            MeterRegistry meterRegistry,
            Counter counter
    ) {
        return new MetricsService(simulationRepository, configRepository, configService, prometheusService, meterRegistry, counter);
    }

    @Bean
    public Counter counter(){
        return new Counter();
    }

}
