package tech.amcg.springbootpractice.objects;

public class LLFResultset {
    public LLFResultset(){}

    public LLFResultset(String result){
        this.result = result;
    }

    public String getResult(){
        return this.result;
    }
    private String result;

}
