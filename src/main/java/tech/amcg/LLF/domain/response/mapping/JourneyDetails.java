package tech.amcg.llf.domain.response.mapping;

import java.util.List;

public class JourneyDetails {

    private List<JourneyStep> stepsList;

    public JourneyDetails(List<JourneyStep> stepsList) {
        this.stepsList = stepsList;
    }

    public List<JourneyStep> getStepsList() {
        return stepsList;
    }

}

enum JourneyType {
    TUBE, WALK;
}
