package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlaskConfigResponse {
    private String status;
    private Integer buildEpoch;
    private Map<String, Object> appliedChanges;
    private Map<String, Object> config;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getBuildEpoch() { return buildEpoch; }
    public void setBuildEpoch(Integer buildEpoch) { this.buildEpoch = buildEpoch; }

    public Map<String, Object> getAppliedChanges() { return appliedChanges; }
    public void setAppliedChanges(Map<String, Object> appliedChanges) { this.appliedChanges = appliedChanges; }

    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
}
