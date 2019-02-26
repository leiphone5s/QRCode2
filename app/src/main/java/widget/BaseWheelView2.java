package widget;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import model.ClassModel;
import model.DepartmentModel;
import model.FacultyModel;
import service.XmlParserHandler;

public class BaseWheelView2 extends Activity {

	/**
	 * 所有省
	 */
	protected String[] mFacultyDatas;
	/**
	 * key - 学校 value - 系别
	 */
	protected Map<String, String[]> mDepartDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 系别 values - 班级
	 */

	/**
	 * 当前学院的名称
	 */
	protected String mCurrentFacultyName;
	/**
	 * 当前系别的名称
	 */
	protected String mCurrentDepartName;

	/**
	 * 解析省市区的XML数据
	 */

	protected void initFacultyDatas()
	{
		List<FacultyModel> facultyList = null;
		AssetManager asset = getAssets();
		try {
			InputStream input = asset.open("teacher_data.xml");
			// 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			facultyList = handler.getDataList();
			//*/ 初始化默认选中的学院、系别
			if (facultyList!= null && !facultyList.isEmpty()) {
				mCurrentFacultyName = facultyList.get(0).getName();
				List<DepartmentModel> departList = facultyList.get(0).getDepartList();
				if (departList!= null && !departList.isEmpty()) {
					mCurrentDepartName = departList.get(0).getName();
				}
			}
			//*/
			mFacultyDatas = new String[facultyList.size()];
			for (int i=0; i< facultyList.size(); i++) {
				// 遍历所有学院的数据
				mFacultyDatas[i] = facultyList.get(i).getName();
				List<DepartmentModel> departList = facultyList.get(i).getDepartList();
				String[] departNames = new String[departList.size()];
				for (int j=0; j< departList.size(); j++) {
					// 遍历学院下面的所有系别的数据
					departNames[j] = departList.get(j).getName();
				}
				// 学院-系别的数据，保存到mDepartDatasMap
				mDepartDatasMap.put(facultyList.get(i).getName(), departNames);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {

		}
	}

}
