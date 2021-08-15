package tech.amcg.llf.domain.response.mapping;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static tech.amcg.llf.domain.response.mapping.StepType.WALK;

@RequiredArgsConstructor
@Getter
public class WalkStep implements JourneyStep {

    private final StepType stepType = WALK;

    @NonNull
    private final String startPoint;

    @NonNull
    private final String endPoint;

}
