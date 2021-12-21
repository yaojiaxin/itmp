package cn.pioneeruniverse.common.databus;

import com.ccic.databus.client.publish.DataBusPublishClient;

/**
 * DataBus
 * 
 * @author:tingting
 * @version:2019年1月23日 上午9:48:56
 */
public class DataBusPublishClientSingleton {
	private static volatile DataBusPublishClient dataBusPublishClient = null;

	private DataBusPublishClientSingleton() {
	}

	public static DataBusPublishClient getInstance(String databussFlag) {
		if (null == dataBusPublishClient) {
			synchronized (DataBusPublishClientSingleton.class) {
				if (null == dataBusPublishClient) {
					dataBusPublishClient = new DataBusPublishClient(databussFlag, null, null);
					dataBusPublishClient.init();
				}
			}
		}
		return dataBusPublishClient;
	}
}
