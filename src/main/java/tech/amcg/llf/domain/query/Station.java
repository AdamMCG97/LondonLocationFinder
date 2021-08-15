package tech.amcg.llf.domain.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class Station implements Location {

    @NonNull
    private String name;

    private Point point;

    private int walkTime;

}
