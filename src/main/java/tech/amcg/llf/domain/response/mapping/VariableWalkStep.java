package tech.amcg.llf.domain.response.mapping;

public class VariableWalkStep extends WalkStep {

    private final int maximumDuration;

    public VariableWalkStep(String startPoint, int maximumDuration) {
        super(startPoint, "Anywhere");
        this.maximumDuration = maximumDuration;
    }

    public int getMaximumDuration() {
        return maximumDuration;
    }
}
