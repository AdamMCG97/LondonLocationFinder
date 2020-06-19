package tech.amcg.llf.domain.response.mapping;

import tech.amcg.llf.domain.query.Location;

public class TubeStep implements JourneyStep {
    private JourneyType journeyType = JourneyType.TUBE;

    private Location startPoint;

    private Location endPoint;

    private TubeLine tubeLine;

    private int duration;

    public TubeStep(Location startPoint, Location endPoint, TubeLine tubeLine, int duration) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.tubeLine = tubeLine;
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

    public TubeLine getTubeLine() {
        return tubeLine;
    }

    public int getDuration() {
        return duration;
    }
}

enum TubeLine {
    JUBILEE, METROPOLITAN, HAMMERSMITHANDCITY, NORTHERN, BAKERLOO, CENTRAL, CIRCLE, DISTRICT, VICTORIA, ELIZABETH, PICADILLY, DLR, WATERLOOANDCITY;
}