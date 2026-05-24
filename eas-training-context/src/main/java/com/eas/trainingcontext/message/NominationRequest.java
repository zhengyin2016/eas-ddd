package com.eas.trainingcontext.message;

public class NominationRequest {
    private String trainingId;
    private String nomineeEmployeeId;
    private String nomineeName;
    private String nominatorEmployeeId;
    private String nominatorName;

    public NominationRequest() {
    }

    public String getTrainingId() { return trainingId; }
    public void setTrainingId(String trainingId) { this.trainingId = trainingId; }
    public String getNomineeEmployeeId() { return nomineeEmployeeId; }
    public void setNomineeEmployeeId(String nomineeEmployeeId) { this.nomineeEmployeeId = nomineeEmployeeId; }
    public String getNomineeName() { return nomineeName; }
    public void setNomineeName(String nomineeName) { this.nomineeName = nomineeName; }
    public String getNominatorEmployeeId() { return nominatorEmployeeId; }
    public void setNominatorEmployeeId(String nominatorEmployeeId) { this.nominatorEmployeeId = nominatorEmployeeId; }
    public String getNominatorName() { return nominatorName; }
    public void setNominatorName(String nominatorName) { this.nominatorName = nominatorName; }
}
