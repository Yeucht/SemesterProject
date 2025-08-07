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
    public PrometheusService prometheusService(MeterRegistry meterRegistry, ConfigService configService) {
        return new PrometheusService(meterRegistry, configService);
    }

    @Bean
    public MetricsService metricsService(
            SimulationRepository simulationRepository,
            ConfigRepository configRepository,
            PrometheusService prometheusService,
            ConfigService configService
    ) {
        return new MetricsService(simulationRepository, configRepository, configService, prometheusService);
    }

}
