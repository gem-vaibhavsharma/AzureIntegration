package demo.model;

import org.jetbrains.annotations.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;


public class AzurePost {

    @NotNull
    private String title;

    @NotNull
    private String assignedTo;

    @NotNull
    private String description;

    private Long parent;

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

}
