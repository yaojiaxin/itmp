package cn.pioneeruniverse.common.message;

import java.util.Map;

public class WeChatRequestDTO {
	private Map<String, String> requestHead;
	private Map<String, Object> requestBody;

	public Map<String, String> getRequestHead() {
		return requestHead;
	}

	public void setRequestHead(Map<String, String> requestHead) {
		this.requestHead = requestHead;
	}

	public Map<String, Object> getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(Map<String, Object> requestBody) {
		this.requestBody = requestBody;
	}

}
