package com.zftlive.android.sample.soap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.zftlive.android.R;
import com.zftlive.android.base.BaseActivity;
import com.zftlive.android.tools.ToolAlert;
import com.zftlive.android.tools.ToolSOAP;

/**
 * 调用WebService接口获取省份对应的城市Activity
 * @author 曾繁添
 * @version 1.0
 *
 */
public class CityActivity extends BaseActivity {
	
	/**显示省份Listview**/
	private ListView mCityListView;
	/***省份数据集合***/
	private List<String> citysList = new ArrayList<String>();

	@Override
	public int bindLayout() {
		return R.layout.activity_soap_provice_city;
	}

	@Override
	public void initView(View view) {
		mCityListView = (ListView) findViewById(R.id.province_list);
		mCityListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				getOperation().addParameter("city", citysList.get(position));
				getOperation().forward(WeatherActivity.class);
			}
		});
	}

	@Override
	public void doBusiness(final Context mContext) {
		
		//等待对话框
		ToolAlert.showLoading(this, "数据加载中...");
		
		//添加参数
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("byProvinceName", String.valueOf(getOperation().getParameters("province")));
		
		ToolSOAP.callService(ProviceActivity.WEB_SERVER_URL,ProviceActivity.NAME_SPACE,"getSupportCity", properties, new ToolSOAP.WebServiceCallBack() {
			
			@Override
			public void onSucced(SoapObject result) {
				//关闭等待对话框
				ToolAlert.closeLoading();
				if(result != null){
					citysList = parseSoapObject(result);
					mCityListView.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, citysList));
				}else{
					ToolAlert.showShort(mContext, "呼叫WebService-->getSupportCity失败");
				}
			}

			@Override
			public void onFailure(String result) {
				//关闭等待对话框
				ToolAlert.closeLoading();
				
				ToolAlert.showShort(mContext, "呼叫WebService-->getSupportProvince失败，原因："+result);
			}
		});
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void destroy() {
		
	}
	
	/**
	 * 解析SoapObject对象
	 * @param result
	 * @return
	 */
	private List<String> parseSoapObject(SoapObject result){
		List<String> list = new ArrayList<String>();
		SoapObject provinceSoapObject = (SoapObject) result.getProperty("getSupportCityResult");
		for(int i=0; i<provinceSoapObject.getPropertyCount(); i++){
			list.add(provinceSoapObject.getProperty(i).toString());
		}
		
		return list;
	}
}
