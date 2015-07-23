package objects;

public class Data implements Comparable<Data> {
	private String link = "";
	private String[] tags = null;
	private Likes likes;
	private Image images;
	private Pagination pagination;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("---- Photo Details -----\n");
		sb.append("Link=" + getLink() + "\n");
		sb.append("tags:\n");
		for (String tag : tags) {
			sb.append(tag +",");
		}
		sb.append("Likes: " + getLikes().getCount() + "\n");
		sb.append("-------------------------");
		return sb.toString();
	}
	
	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public Data() {
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Likes getLikes() {
		return likes;
	}

	public void setLikes(Likes likes) {
		this.likes = likes;
	}

	@Override
	public int compareTo(Data p) {
		return p.getLikes().getCount() - likes.getCount();
	}

	public Image getImage() {
		return images;
	}

	public void setImage(Image image) {
		this.images = image;
	}

	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

}
