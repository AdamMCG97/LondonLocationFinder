package tech.amcg.llf.domain.neo4j;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Tube {

    @Id @GeneratedValue
    Long id;

    private String name;

    //TODO: the rest
}
