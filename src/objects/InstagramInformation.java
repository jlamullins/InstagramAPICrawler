package objects;

public class InstagramInformation {

	private Data[] data;
	private Pagination pagination;

	public Data[] getData()
	{
		return data;
	}

	public void setData(Data[] data) 
	{
		this.data = data;
	}

	public InstagramInformation() 
	{

	}

	public Pagination getPagination()
	{
		return pagination;
	}

	public void setPagination(Pagination pagination) 
	{
		this.pagination = pagination;
	}
	
	@Override
	public String toString()
	{
		StringBuilder instagramInfoString = new StringBuilder();
		instagramInfoString.append("----Instagram Information-----");
		instagramInfoString.append("Data: " + getData());
		instagramInfoString.append("Pagination" + getPagination());
		instagramInfoString.append("-------------------------------");
		return(instagramInfoString.toString());
	}
}
