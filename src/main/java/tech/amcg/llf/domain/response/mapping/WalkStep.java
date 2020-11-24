package tech.amcg.llf.domain.response.mapping;

public class WalkStep implements JourneyStep {
    private final JourneyType journeyType = JourneyType.WALK;

    private final String startPoint;

    private final String endPoint;

    public WalkStep(String startPoint, String endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public JourneyType getJourneyType() {
        return journeyType;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

}
