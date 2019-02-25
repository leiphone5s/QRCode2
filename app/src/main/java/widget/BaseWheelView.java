package widget;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import model.ClassModel;
import model.DepartmentModel;
import model.SchoolModel;
import service.XmlParserHandler;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class BaseWheelView extends Activity {

	/**
	 * 所有省
	 */
	protected String[] mSchoolDatas;
	/**
	 * key - 学校 value - 系别
	 */
	protected Map<String, String[]> mDepartDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 系别 values - 班级
	 */
	protected Map<String, String[]> mClassDatasMap = new HashMap<String, String[]>();

	/**
	 * key - 班级 values -  班号
	 */
	protected Map<String, String> mClasscodeDatasMap = new HashMap<String, String>();

	/**
	 * 当前省的名称
	 */
	protected String mCurrentSchoolName;
	/**
	 * 当前市的名称
	 */
	protected String mCurrentDepartName;
	/**
	 * 当前区的名称
	 */
	protected String mCurrentClassName ="";

	/**
	 * 当前区的邮政编码
	 */
	protected String mCurrentClassCode ="";

	/**
	 * 解析省市区的XML数据
	 */

	protected void initSchoolDatas()
	{
		List<SchoolModel> schoolList = null;
		AssetManager asset = getAssets();
		try {
			InputStream input = asset.open("school_data.xml");
			// 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			schoolList = handler.getDataList();
			//*/ 初始化默认选中的省、市、区
			if (schoolList!= null && !schoolList.isEmpty()) {
				mCurrentSchoolName = schoolList.get(0).getName();
				List<DepartmentModel> departList = schoolList.get(0).getDepartList();
				if (departList!= null && !departList.isEmpty()) {
					mCurrentDepartName = departList.get(0).getName();
					List<ClassModel> classList = departList.get(0).getClassList();
					mCurrentClassName = classList.get(0).getName();
					mCurrentClassCode = classList.get(0).getClasscode();
				}
			}
			//*/
			mSchoolDatas = new String[schoolList.size()];
			for (int i=0; i< schoolList.size(); i++) {
				// 遍历所有省的数据
				mSchoolDatas[i] = schoolList.get(i).getName();
				List<DepartmentModel> departList = schoolList.get(i).getDepartList();
				String[] departNames = new String[departList.size()];
				for (int j=0; j< departList.size(); j++) {
					// 遍历省下面的所有市的数据
					departNames[j] = departList.get(j).getName();
					List<ClassModel> classList = departList.get(j).getClassList();
					String[] classNameArray = new String[classList.size()];
					ClassModel[] classArray = new ClassModel[classList.size()];
					for (int k=0; k<classList.size(); k++) {
						// 遍历市下面所有区/县的数据
						ClassModel classModel = new ClassModel(classList.get(k).getName());

						mClasscodeDatasMap.put(classList.get(k).getName(), classList.get(k).getClasscode());
						classArray[k] = classModel;
						classNameArray[k] = classModel.getName();
						Log.d("feng","classModel.getName() = "+classModel.getName());
					}
					// 市-区/县的数据，保存到mDistrictDatasMap
					mClassDatasMap.put(departNames[j], classNameArray);
				}
				// 省-市的数据，保存到mCitisDatasMap
				mDepartDatasMap.put(schoolList.get(i).getName(), departNames);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {

		}
	}

}
