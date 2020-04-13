package tech.amcg.LLF.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tech.amcg.LLF.objects.LLFQuery;
import tech.amcg.LLF.objects.LLFResultset;
import tech.amcg.LLF.service.LLFService;

@RestController
public class LLFController {

    @Autowired
    LLFService LLFService;

    @RequestMapping(method = RequestMethod.POST, value = "/llp")
    public LLFResultset getLLPResults(@RequestBody LLFQuery query) throws Exception {
        return LLFService.calculateResults(query);
    }

/*    @RequestMapping("/llpquery")
public LLFQuery getLLFQuery(){
    return new LLFQuery(Arrays.asList(new LLFPerson("W2 6TT", 20), new LLFPerson("E14 4FT", 30)), "Tube Only", 2, 1500, 2000, true);
}*/
}
