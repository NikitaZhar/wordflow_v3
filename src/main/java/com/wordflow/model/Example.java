package com.wordflow.model;

import java.util.List;

public class Example {
    private String id;
    private String deExample;
    private String translateExample;
    private List<String> collocations;
    private List<String> grammarTags;
    private Progress progress;
    private Boolean active;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

//    public Progress getExampleProgress() { return progress; }
    public void setProgress(Progress progress) { this.progress = progress; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getDeExample() { return deExample; }
    public void setDeExample(String deExample) { this.deExample = deExample; }

    public String getTranslateExample() { return translateExample; }
    public void setTranslateExample(String translateExample) { this.translateExample = translateExample; }

    public List<String> getCollocations() { return collocations; }
    public void setCollocations(List<String> collocations) { this.collocations = collocations; }

    public List<String> getGrammarTags() { return grammarTags; }
    public void setGrammarTags(List<String> grammarTags) { this.grammarTags = grammarTags; }
    
    public Progress getProgress() { return progress; }
    
    public void registerExampleProgress(boolean reviewSuccess) {
        if (reviewSuccess) {
            progress.incReviewInterval();
        } else {
            progress.decReviewInterval();
        }
        progress.setLastReviewTime();
        progress.setIntervalToRepeat();
    }
    
    

    @Override
    public String toString() {
        return "\n * Examples * " +
                "\ndeExample : " + deExample + "\n" +
                "translateExample : " + translateExample + "\n" +
                "collocationsUsed : " + collocations + "\n" +
                "progress : " + progress + "\n" +
                "grammarTags : " + grammarTags + "\n" +
                "active : " + active;
    }
}
