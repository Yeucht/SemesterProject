package ingestion;

import java.util.List;

public class MeterData {
    private int sequence;
    private int status;
    private int version;
    private String address;
    private List<Integer> payload;

    public MeterData() {}

    public MeterData(int sequence, int status, int version, String address, List<Integer> payload) {
        this.sequence = sequence;
        this.status = status;
        this.version = version;
        this.address = address;
        this.payload = payload;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Integer> getPayload() {
        return payload;
    }

    public void setPayload(List<Integer> payload) {
        this.payload = payload;
    }
}
