package util;

import model.City;
import model.County;
import model.Province;
import android.text.TextUtils;
import db.CoolWeatherDB;


public class Utility {

	/*
	 * �����ʹ����������������ʡ������
	 */
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response)
	{
		/*
		 * �����ַ�������AndroidΪ�����ṩ��һ����ʵ�õ�TextUtils�࣬
		 * �������Ƚϼ򵥵����ݲ���ȥ˼��������ʽ�������������android.text.TextUtils���࣬��Ҫ�Ĺ�������:
                             �Ƿ�Ϊ���ַ� boolean android.text.TextUtils.isEmpty(CharSequence str) 
		 */
		if(!TextUtils.isEmpty(response))
		{
			String[] allProvinces = response.split(",");
			if(allProvinces!=null && allProvinces.length>0)
			{
				for(String p :allProvinces)
				{
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceText(array[0]);
					province.setProvinceName(array[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId)
	
	{
		String[] allCitys = response.split(",");
		if(allCitys != null && allCitys.length >= 0)
		{
			for(String p :allCitys)
			{
				City city = new City();
				String[] array = p.split("\\|");
				city.setCityCode(array[0]);
				city.setCityName(array[1]);
				city.setProvinceId(provinceId);
				coolWeatherDB.saveCity(city);
				return true;
				
			}
		}
		
		return false;
	}
	
	public synchronized static boolean handleCountyHandler(CoolWeatherDB coolWeatherDB,String response,int CityId)
	{
		String allCounty[] = response.split(",");
		if(allCounty != null && allCounty.length>=0)
		{
			for ( String C : allCounty)
			{
				County county = new County();
				String array[] = C.split("\\|");
				county.setCountyCode(array[0]);
				county.setCountyName(array[1]);
				county.setCityId(CityId);
				
				return true;
			}
		}
		return false;
	}
}
