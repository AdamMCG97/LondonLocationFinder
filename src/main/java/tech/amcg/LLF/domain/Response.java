package tech.amcg.llf.domain;

import tech.amcg.llf.domain.response.LLFResult;

import java.util.List;

public class Response {

    private List<LLFResult> llfResults;

    private Query returnQuery;

    public Response(List<LLFResult> llfResults, Query returnQuery) {
        this.llfResults = llfResults;
        this.returnQuery = returnQuery;
    }

    public Response(){}

    public List<LLFResult> getLlfResults() {
        return llfResults;
    }

    public Query getReturnQuery() {
        return returnQuery;
    }
}
