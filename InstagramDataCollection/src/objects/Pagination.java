package objects;

public class Pagination 
{
    private String next_min_id;
    private String deprecation_warning;
    private String min_tag_id;
    private String next_url = "";
    private Data[] data;
    
     
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("***** Pagination Details *****\n");
        sb.append("Next Min Id="+getNextMinId()+"\n");
        sb.append("Depreciation Warning="+getDepreciationWarning()+"\n");
        sb.append("Min Tag Id="+getMin_tag_id()+"\n");
        sb.append("Data="+getPhotoData()+"\n");
        sb.append("*****************************");
        return sb.toString();
    }


	public String getNextMinId() {
		return next_min_id;
	}


	public String getDepreciationWarning() {
		return deprecation_warning;
	}


	public String getMin_tag_id() {
		return min_tag_id;
	}


	public Data[] getPhotoData() {
		return data;
	}


	public void setNextMinId(String nextMinId) {
		this.next_min_id = nextMinId;
	}


	public void setDepreciationWarning(String depreciationWarning) {
		this.deprecation_warning = depreciationWarning;
	}


	public void setMin_tag_id(String min_tag_id) {
		this.min_tag_id = min_tag_id;
	}


	public void setData(Data[] data) {
		this.data = data;
	}


	public String getNext_url() {
		return next_url;
	}


	public void setNext_url(String next_url) {
		this.next_url = next_url;
	}
}
