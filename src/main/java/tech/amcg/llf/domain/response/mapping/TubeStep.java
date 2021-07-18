package tech.amcg.llf.domain.response.mapping;

import static tech.amcg.llf.domain.response.mapping.StepType.TUBE;

public class TubeStep implements JourneyStep {
    private StepType stepType = TUBE;

    private String startPoint;

    private String endPoint;

    private TubeLine tubeLine;

    private Double duration;

    public TubeStep(String startPoint, String endPoint, TubeLine tubeLine, Double duration) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.tubeLine = tubeLine;
        this.duration = duration;
    }

    public StepType getStepType() {
        return stepType;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public TubeLine getTubeLine() {
        return tubeLine;
    }

    public Double getDuration() {
        return duration;
    }
}

