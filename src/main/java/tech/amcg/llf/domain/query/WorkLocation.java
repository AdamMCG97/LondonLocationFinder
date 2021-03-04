package tech.amcg.llf.domain.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@ToString
public class WorkLocation implements Location {
    
    private String postcode;

    private String latitude;

    private String longitude;

}
