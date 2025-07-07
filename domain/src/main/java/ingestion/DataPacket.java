package ingestion;

import java.util.List;

public class DataPacket {
    private String authUser;
    private String authSerialNumber;
    private String authDigest;
    private long receivedTime;
    private int connectionCause;
    private boolean isAuthenticated;
    private boolean isMessageBrokerJob;
    private String archiverConnectionId;
    private String cacheFileName;
    private String masterUnitNumber;
    private String masterUnitOwnerId;
    private String masterUnitType;
    private List<MeterData> meteringData;

    public DataPacket() {}

    public DataPacket(
            String authUser,
            String authSerialNumber,
            String authDigest,
            long receivedTime,
            int connectionCause,
            boolean isAuthenticated,
            boolean isMessageBrokerJob,
            String archiverConnectionId,
            String cacheFileName,
            String masterUnitNumber,
            String masterUnitOwnerId,
            String masterUnitType,
            List<MeterData> meterData
    ) {
        this.authUser = authUser;
        this.authSerialNumber = authSerialNumber;
        this.authDigest = authDigest;
        this.receivedTime = receivedTime;
        this.connectionCause = connectionCause;
        this.isAuthenticated = isAuthenticated;
        this.isMessageBrokerJob = isMessageBrokerJob;
        this.archiverConnectionId = archiverConnectionId;
        this.cacheFileName = cacheFileName;
        this.masterUnitNumber = masterUnitNumber;
        this.masterUnitOwnerId = masterUnitOwnerId;
        this.masterUnitType = masterUnitType;
        this.meteringData = meterData;
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public String getAuthSerialNumber() {
        return authSerialNumber;
    }

    public void setAuthSerialNumber(String authSerialNumber) {
        this.authSerialNumber = authSerialNumber;
    }

    public String getAuthDigest() {
        return authDigest;
    }

    public void setAuthDigest(String authDigest) {
        this.authDigest = authDigest;
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public int getConnectionCause() {
        return connectionCause;
    }

    public void setConnectionCause(int connectionCause) {
        this.connectionCause = connectionCause;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public boolean isMessageBrokerJob() {
        return isMessageBrokerJob;
    }

    public void setMessageBrokerJob(boolean messageBrokerJob) {
        isMessageBrokerJob = messageBrokerJob;
    }

    public String getArchiverConnectionId() {
        return archiverConnectionId;
    }

    public void setArchiverConnectionId(String archiverConnectionId) {
        this.archiverConnectionId = archiverConnectionId;
    }

    public String getCacheFileName() {
        return cacheFileName;
    }

    public void setCacheFileName(String cacheFileName) {
        this.cacheFileName = cacheFileName;
    }

    public String getMasterUnitNumber() {
        return masterUnitNumber;
    }

    public void setMasterUnitNumber(String masterUnitNumber) {
        this.masterUnitNumber = masterUnitNumber;
    }

    public String getMasterUnitOwnerId() {
        return masterUnitOwnerId;
    }

    public void setMasterUnitOwnerId(String masterUnitOwnerId) {
        this.masterUnitOwnerId = masterUnitOwnerId;
    }

    public String getMasterUnitType() {
        return masterUnitType;
    }

    public void setMasterUnitType(String masterUnitType) {
        this.masterUnitType = masterUnitType;
    }

    public List<MeterData> getMeteringData() {
        return meteringData;
    }

    public void setMeteringData(List<MeterData> meteringData) {
        this.meteringData = meteringData;
    }
}
