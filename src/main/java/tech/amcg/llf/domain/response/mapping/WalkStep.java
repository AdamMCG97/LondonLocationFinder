package tech.amcg.llf.domain.response.mapping;

import static tech.amcg.llf.domain.response.mapping.stepType.WALK;

public class WalkStep implements JourneyStep {
    private final stepType stepType = WALK;

    private final String startPoint;

    private final String endPoint;

    public WalkStep(String startPoint, String endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public stepType getStepType() {
        return stepType;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

}
