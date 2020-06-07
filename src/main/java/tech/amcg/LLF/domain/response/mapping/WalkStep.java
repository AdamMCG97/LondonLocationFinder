package tech.amcg.llf.domain.response.mapping;

import tech.amcg.llf.domain.query.Location;

public class WalkStep {
    private JourneyType journeyType = JourneyType.WALK;

    private Location startPoint;

    private Location endPoint;

    private int duration;

    public WalkStep(Location startPoint, Location endPoint, int duration) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.duration = duration;
    }

    public JourneyType getJourneyType() {
        return journeyType;
    }

    public Location getStartPoint() {
        return startPoint;
    }

    public Location getEndPoint() {
        return endPoint;
    }

    public int getDuration() {
        return duration;
    }
}
