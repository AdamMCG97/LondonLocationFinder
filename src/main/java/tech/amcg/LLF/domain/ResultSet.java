package tech.amcg.llf.domain;

import java.util.List;

public class ResultSet {
    private String result;

    private List<Journey> journeyList;

    public ResultSet(){}

    public ResultSet(String result){
        this.result = result;
    }

    public String getResult(){
        return this.result;
    }

    public List<Journey> getJourneyList() {
        return journeyList;
    }

    public void setJourneyList(List<Journey> journeyList) {
        this.journeyList = journeyList;
    }
}
