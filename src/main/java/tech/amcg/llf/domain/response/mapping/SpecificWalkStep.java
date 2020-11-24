package tech.amcg.llf.domain.response.mapping;

public class SpecificWalkStep extends WalkStep {

    private final int duration;

    public SpecificWalkStep(String startPoint, String endPoint, int duration) {
        super(startPoint, endPoint);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

}
