package service;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import model.SchoolModel;
import model.DepartmentModel;
import model.ClassModel;

import model.SchoolModel;

public class XmlParserHandler extends DefaultHandler {

    /**
     * 存储所有的解析对象
     */
    private List<SchoolModel> schoolList = new ArrayList<SchoolModel>();

    public XmlParserHandler() {

    }

    public List<SchoolModel> getDataList() {
        return schoolList;
    }

    @Override
    public void startDocument() throws SAXException {
        // 当读到第一个开始标签的时候，会触发这个方法
    }

    SchoolModel schoolModel = new SchoolModel();
    DepartmentModel departModel = new DepartmentModel();
    ClassModel classModel = new ClassModel();

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // 当遇到开始标记的时候，调用这个方法
        if (qName.equals("school")) {
            schoolModel = new SchoolModel();
            schoolModel.setName(attributes.getValue(0));
            schoolModel.setDepartList(new ArrayList<DepartmentModel>());
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
            schoolModel.getDepartList().add(departModel);
        } else if (qName.equals("school")) {
            schoolList.add(schoolModel);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
    }

}
