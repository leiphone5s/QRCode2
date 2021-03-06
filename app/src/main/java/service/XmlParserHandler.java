package service;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import model.FacultyModel;
import model.DepartmentModel;
import model.ClassModel;

import model.FacultyModel;

public class XmlParserHandler extends DefaultHandler {

    /**
     * 存储所有的解析对象
     */
    private List<FacultyModel> facultyList = new ArrayList<FacultyModel>();

    public XmlParserHandler() {

    }

    public List<FacultyModel> getDataList() {
        return facultyList;
    }

    @Override
    public void startDocument() throws SAXException {
        // 当读到第一个开始标签的时候，会触发这个方法
    }

    FacultyModel facultyModel = new FacultyModel();
    DepartmentModel departModel = new DepartmentModel();
    ClassModel classModel = new ClassModel();

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // 当遇到开始标记的时候，调用这个方法
        if (qName.equals("faculty")) {
            facultyModel = new FacultyModel();
            facultyModel.setName(attributes.getValue(0));
            facultyModel.setDepartList(new ArrayList<DepartmentModel>());
        } else if (qName.equals("depart")) {
            departModel = new DepartmentModel();
            departModel.setName(attributes.getValue(0));
            departModel.setClassList(new ArrayList<ClassModel>());
        } else if (qName.equals("class")) {
            classModel = new ClassModel();
            classModel.setName(attributes.getValue(0));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // 遇到结束标记的时候，会调用这个方法
        if (qName.equals("class")) {
            departModel.getClassList().add(classModel);
        } else if (qName.equals("depart")) {
            facultyModel.getDepartList().add(departModel);
        } else if (qName.equals("faculty")) {
            facultyList.add(facultyModel);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
    }

}
