package objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class TagData {

	private String[] link = null;
	private String name;
	private Data[] photoData = null;
	private String id;
	private TagData[] children;
	private int size;
	
	public String getTag() {
		return name;
	}

	public void setTag(String tag) {
		this.name = tag;
	}
	
	public TagData()
	{
		
	}
	
	public TagData(String tag)
	{
		this.name = tag;
	}
	
	public TagData (String tag, TagData parent, String id)
	{
		this.name = tag;
	//	this.parent = parent;
		this.id = id;
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	public Data[] getPhotoData() {
		return photoData;
	}

	public void setPhotoData(Data[] photoData) {
		this.photoData = photoData;
	}

	/**
	 * Sets/updates the list of photos connected to this tag. 
	 * @param newPhotoData
	 * @param allLinks
	 * @return allLinks
	 */
	public List<String> addPhotoDataAndLinks(Data[] newPhotoData, List<String> allLinks) 
	{
		Collection<Data> collection = new ArrayList<Data>();
		if (this.photoData != null)
		{
			collection.addAll(Arrays.asList(this.photoData));
		}
		List<String> links = new ArrayList<String>();
		if (this.link != null)
		{
			links.addAll(Arrays.asList(this.link));			
		}
		String tempLink = "";
		for (Data photo : newPhotoData)
		{
			Image images = photo.getImage();
			tempLink = images.getLow_resolution().getUrl();
			if (!allLinks.contains(tempLink))
			{
				links.add(tempLink);
				allLinks.add(tempLink);
				collection.add(photo);
			}
		}
		this.link = links.toArray(new String[links.size()]);
		this.size = links.size();
		this.photoData = collection.toArray(new Data[] {});			

		return allLinks;
	}
	
	public String[] getLink()
	{
		return this.link;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TagData[] getChild() {
		return children;
	}

	public void setChild(TagData[] child) {
		this.children = child;
	}
	
	public void clearPhotoData()
	{
		this.photoData = new Data[0];
	}
}
