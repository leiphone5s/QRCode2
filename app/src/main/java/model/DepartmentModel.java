package model;

import java.util.List;

public class DepartmentModel {
    private String name;
    private List<ClassModel> classList;

    public DepartmentModel(){
        super();
    }

    public DepartmentModel(String name, List<ClassModel> classList) {
        this.name = name;
        this.classList = classList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ClassModel> getClassList() {
        return classList;
    }

    public void setClassList(List<ClassModel> classList) {
        this.classList = classList;
    }
}
