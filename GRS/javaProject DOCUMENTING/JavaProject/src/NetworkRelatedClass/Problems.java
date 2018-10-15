package NetworkRelatedClass;

import java.io.Serializable;

/**
 * This Class is used to send complain from user to server and all problems submitted by users from  server to user
 */
public class Problems implements Serializable{
    private String problemType;
    private String summary;
    private String description;
    private String problemIntensity;
    private String problemPostedBy;
    private String dateTime;
    private String problemStatus;
    private String problemScope;
    private int voteOnProblem;
    private String fileName;


 //constructor needed to show history or all problems submitted by users from  server to user
   public Problems(String problemType,String summary,String description,String dateTime,String problemStatus,String problemScope,int voteOnProblem)//problemStatus,dateTime
    {
        this.problemType=problemType;
        this.summary=summary;
        this.description=description;
        this.dateTime=dateTime;
        this.problemStatus=problemStatus;
        this.problemScope=problemScope;
        this.setVoteOnProblem(voteOnProblem);

    }

    //constructor needed to send complain from user to server
    public Problems(String problemType,String summary,String description,String problemPostedBy,String dateTime,String problemScope,String fileName)
    {
        this.problemType=problemType;
        this.summary=summary;
        this.description=description;
        this.problemPostedBy=problemPostedBy;
        this.dateTime=dateTime;
        this.problemScope=problemScope;
        this.fileName=fileName;
    }




    public String getProblemType() {
        return problemType;
    }
    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }

    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getProblemIntensity() {
        return problemIntensity;
    }
    public void setProblemIntensity(String problemIntensity) {
        this.problemIntensity = problemIntensity;
    }

    public String getProblemPostedBy() {
        return problemPostedBy;
    }
    public void setProblemPostedBy(String problemPostedBy) {
        this.problemPostedBy = problemPostedBy;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getProblemStatus() {
        return problemStatus;
    }
    public void setProblemStatus(String problemStatus) {
        this.problemStatus = problemStatus;
    }

    public String getProblemScope() {
        return problemScope;
    }
    public void setProblemScope(String problemScope) {
        this.problemScope = problemScope;
    }

    public int getVoteOnProblem() {
        return voteOnProblem;
    }
    public void setVoteOnProblem(int voteOnProblem) {
        this.voteOnProblem = voteOnProblem;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
