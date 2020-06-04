package tech.amcg.llf.domain;

import java.util.List;

public class Journey {

    private List<JourneyStep> stepsList;

    public Journey(List<JourneyStep> stepsList) {
        this.stepsList = stepsList;
    }

    public List<JourneyStep> getStepsList() {
        return stepsList;
    }

}

enum JourneyType {
    TUBE, WALK;
}
