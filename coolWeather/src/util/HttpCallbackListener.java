package util;

/*
 * HttpUtil���õ���HttpCallbackListener�ӿ����ص����񷵻صĽ�������Ҫ�������ӿ�
 */
public interface HttpCallbackListener {

	void onFinish(String response);
	
	void onError(Exception e);
}
