package com.astrolabs.astroexpansion.common.research;

import java.util.List;

public class Research {
    private final String id;
    private final String name;
    private final String description;
    private final int researchPoints;
    private final List<String> prerequisites;
    
    public Research(String id, String name, String description, int researchPoints, List<String> prerequisites) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.researchPoints = researchPoints;
        this.prerequisites = prerequisites;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getResearchPoints() {
        return researchPoints;
    }
    
    public List<String> getPrerequisites() {
        return prerequisites;
    }
}