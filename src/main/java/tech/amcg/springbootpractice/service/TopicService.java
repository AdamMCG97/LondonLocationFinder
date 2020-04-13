package tech.amcg.springbootpractice.service;

import org.springframework.stereotype.Service;
import tech.amcg.springbootpractice.objects.Topic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TopicService {

    private List<Topic> topics = new ArrayList<>(Arrays.asList(
            new Topic("SB123", "Spring Boot", "Spring Boot description"),
            new Topic("SF123", "Spring Framework", "Spring Framework description"),
            new Topic("VA123", "Vavr", "Vavr description"))
        );

    public List<Topic> getAllTopics(){
        return topics;
    }

    public Topic getTopic(String id){
        return topics.stream().filter(t -> t.getId().equals(id)).findFirst().get();
    }

    public void addTopic(Topic topic){
        topics.add(topic);
    }

    public void updateTopic(String id, Topic topic) {
        int index;

        index = topics.indexOf(topics.stream().filter(t -> t.getId().equals(id)).findFirst().get());
        topics.set(index, topic);
    }

    public void deleteTopic(String id){
        topics.removeIf(t -> t.getId().equals(id));
    }
}
