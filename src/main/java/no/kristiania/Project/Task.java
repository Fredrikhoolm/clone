package no.kristiania.Project;

public class Task {
    private Integer id;
    private String name;
    private Integer statusId;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {

        this.id = id;
    }

    public Integer getStatusId() {

        return statusId;
    }

    public void setStatusId(Integer statusId) {

        this.statusId = statusId;
    }

}