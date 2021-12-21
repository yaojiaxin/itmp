package cn.pioneeruniverse.common.bean;

public class BasePage<T>{
	public static final String ASC = "asc";
	public static final String DESC = "desc";
	protected long pageNo = 1L;

	protected long totalPages = -1L;

	protected long pageSize = 10L;

	private long offset = 0L;

	protected boolean autoCount = false;

	protected long totalCount = -1L;

	protected String orderBy = "ID";

	protected String orderDirection = "DESC";

	private Long onlineCount;

	public Long getOnlineCount() {
		return this.onlineCount;
	}
	 

	public void setOnlineCount(Long onlineCount) {
		this.onlineCount = onlineCount;
	}

	 

	public long getPageNo() {
		return this.pageNo;
	}

	public void setPageNo(long pageNo) {
		this.pageNo = pageNo<1?1:pageNo;
 	}

	 

	public long getPageSize() {
		return pageSize;
	}

	public void setPageSize(long size) {
		pageSize = size<1?pageSize:size;
	}

	 

	public long getOffset() {
		this.offset = ((this.pageNo - 1L) * pageSize);
		return this.offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public String getOrderBy() {
		return this.orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderDirection() {
		return this.orderDirection;
	}

	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}

	 

	public boolean isAutoCount() {
		return this.autoCount;
	}

	public void setAutoCount(boolean autoCount) {
		this.autoCount = autoCount;
	}

	 

	public long getTotalCount() {
		return this.totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}

	public long getTotalPages() {
		if (this.totalCount < 0L) {
			return -1L;
		}

		this.totalPages = (this.totalCount / pageSize);
		if (this.totalCount % pageSize > 0L) {
			this.totalPages += 1L;
		}
		return this.totalPages;
	}

	public boolean isHasNext() {
		return this.pageNo + 1L <= getTotalPages();
	}

	public long getNextPage() {
		if (isHasNext()) {
			return this.pageNo + 1L;
		}
		return this.pageNo;
	}

	public boolean isHasPre() {
		return this.pageNo - 1L >= 1L;
	}

	public long getPrePage() {
		if (isHasPre()) {
			return this.pageNo - 1L;
		}
		return this.pageNo;
	}
 
}
