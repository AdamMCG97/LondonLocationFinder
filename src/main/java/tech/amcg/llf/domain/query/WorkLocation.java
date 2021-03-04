package tech.amcg.llf.domain.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class WorkLocation implements Location {
    
    private String postcode;

    private String latitude;

    private String longitude;

}
