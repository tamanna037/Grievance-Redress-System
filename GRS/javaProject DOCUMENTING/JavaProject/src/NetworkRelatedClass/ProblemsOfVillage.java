package NetworkRelatedClass;

import java.io.Serializable;

/**
 * This class is used to send all problems of villages to user from server
 */
public class ProblemsOfVillage implements Serializable{
    private String problemType;
    private String summary;
    private String description;
    private String shortDescription;
    private String scope;
    private String problemIntensity;
    private String problemPostedBy;
    private String dateTime;
    private String problemStatus;
    private String vote;
    private String problemNo;
    private String votedBy;
    private String voteStatus;

    public ProblemsOfVillage(String problemType,String summary,String description,String problemPostedBy,String scope,String dateTime,String problemStatus,String vote,String problemNo,String votedBy,String voteStatus)
    {
        this.problemType=problemType;
        this.summary=summary;
        this.description=description;
        this.problemPostedBy=problemPostedBy;
        this.setScope(scope);
        this.dateTime=dateTime;
        this.problemStatus=problemStatus;
        this.vote=vote;
        this.problemNo=problemNo;
        this.votedBy=votedBy;
        this.voteStatus=voteStatus;
        if(description.length()<=10) this.shortDescription=description;
        else this.shortDescription=this.getDescription().substring(0,10)+"....";
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

    public String getProblemPostedBy() {return problemPostedBy;}
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

    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getVote() {
        return vote;
    }
    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getVotedBy() {
        return votedBy;
    }
    public void setVotedBy(String votedBy) {
        this.votedBy = votedBy;
    }

    public String getProblemNo() {
        return problemNo;
    }
    public void setProblemNo(String problemNo) {
        this.problemNo = problemNo;
    }

    public String getVoteStatus() {
        return voteStatus;
    }
    public void setVoteStatus(String voteStatus) {
        this.voteStatus = voteStatus;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
}
