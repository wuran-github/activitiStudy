package learn.activiti.spring.demo.demo.model;

public class ProcessModel {
    private String id;
    private String name;
    private int version;

    public ProcessModel() {
    }

    public ProcessModel(String id, String name, int version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
