package util;

/*
 * HttpUtil中用到了HttpCallbackListener接口来回调服务返回的结果，因此要添加这个接口
 */
public interface HttpCallbackListener {

	void onFinish(String response);
	
	void onError(Exception e);
}
