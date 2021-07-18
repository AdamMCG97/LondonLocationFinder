package tech.amcg.llf.domain.response.mapping;

import java.util.List;

public class JourneyDetails {

    private final List<JourneyStep> stepsList;

    public JourneyDetails(List<JourneyStep> stepsList) {
        this.stepsList = stepsList;
    }

    public List<JourneyStep> getStepsList() {
        return stepsList;
    }

}

enum StepType {
    TUBE, WALK;
}
