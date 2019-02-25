package model;

import java.util.List;

public class SchoolModel {
    private String name;
    private List<DepartmentModel> departList;

    public SchoolModel(){
        super();
    }

    public SchoolModel(String name, List<DepartmentModel> departList) {
        this.name = name;
        this.departList = departList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DepartmentModel> getDepartList() {
        return departList;
    }

    public void setDepartList(List<DepartmentModel> departList) {
        this.departList = departList;
    }
}
