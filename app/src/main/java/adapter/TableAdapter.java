package adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lei.qrcode.R;

import model.AbsenseInfo;


public class TableAdapter extends BaseAdapter {

	private List<AbsenseInfo> list;
	private LayoutInflater inflater;

	public TableAdapter(Context context, List<AbsenseInfo> list){
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		int ret = 0;
		if(list!=null){
			ret = list.size();
		}
		return ret;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		AbsenseInfo AbsenseInfo = (AbsenseInfo) this.getItem(position);

		ViewHolder viewHolder;

		if(convertView == null){

			viewHolder = new ViewHolder();

			convertView = inflater.inflate(R.layout.list_item, null);
			viewHolder.courseName = (TextView) convertView.findViewById(R.id.text_courseName);
			viewHolder.teacherName = (TextView) convertView.findViewById(R.id.text_teacherName);
			viewHolder.place = (TextView) convertView.findViewById(R.id.text_place);
			viewHolder.signInTime = (TextView) convertView.findViewById(R.id.text_signInTime);
			viewHolder.signType = (TextView) convertView.findViewById(R.id.text_signType);
			viewHolder.courseName.setSelected(true);
			viewHolder.teacherName.setSelected(true);
			viewHolder.place.setSelected(true);
			viewHolder.signInTime.setSelected(true);
			viewHolder.signType.setSelected(true);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.courseName.setText(AbsenseInfo.getCourseName());
		viewHolder.courseName.setTextSize(13);
		viewHolder.teacherName.setText(AbsenseInfo.getTeacherName());
		viewHolder.teacherName.setTextSize(13);
		viewHolder.place.setText(AbsenseInfo.getPlace());
		viewHolder.place.setTextSize(13);
		viewHolder.signInTime.setText(AbsenseInfo.getSignInTime());
		viewHolder.signInTime.setTextSize(13);
		viewHolder.signType.setText(AbsenseInfo.getSignType());
		viewHolder.signType.setTextSize(13);

		return convertView;
	}

	public static class ViewHolder{
		public TextView courseName;
		public TextView teacherName;
		public TextView place;
		public TextView signInTime;
		public TextView signType;
	}

}