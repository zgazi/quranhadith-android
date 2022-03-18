package com.greenledge.common;


import android.graphics.Bitmap;

public class RSSItem {
	private String title;
	private String description; //Atom summary
	private String link, linkType="";
	private String pubDate=""; //Atom updated
	private String imageUrl="";
    private String category = "";
    private String guid = ""; //Atom id
    private String content = "";
	private String mediaUrl="";
    private String mediaType="";
    private Bitmap thumbnailImage = null;
    private String author="";
    private String source = "";

	public RSSItem(String title, String description, String link, String pubDate, String imageUrl)
	{
		this.title = title;
		this.description = description;
		this.link = link;
		this.pubDate = pubDate;
		this.imageUrl = imageUrl;
	}

	public RSSItem() {
		//nothing to set
	}

	public void setTitle(String t){
		this.title = t;
	}

	public String getTitle()
	{
		return title;
	}

	public void setDescription(String d)
	{
		this.description= d;
	}

	public String getDescription()
	{
		return description;
	}

	public void setLink(String l)
	{
		this.link = l;
	}
	public String getLink()
	{
		return link;
	}

	public void setMediaUrl(String l)
	{
		this.mediaUrl = l;
	}
	public String getMediaUrl()
	{
		return mediaUrl;
	}

	public void setMediaType(String t)
	{
		this.mediaType = t;
	}
	public String getMediaType()
	{
		return mediaType;
	}

	public void setSource(String s)
	{
		this.source = s;
	}
	public String getSource()
	{
		return source;
	}
	public void setPubDate(String d)
	{
		this.pubDate = d;
	}
	public String getPubDate()
	{
		return pubDate;
	}

	public String getImageUrl()
	{
		return imageUrl;
	}
	public void setImageUrl(String url)
	{
		this.imageUrl = url;
	}

    public String getCategory() {
        return category;
    }

    public void setCategory(String feedCategory) {
        this.category = feedCategory;
    }
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Bitmap getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(Bitmap thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }
    /**
    * Two users are equal if their firstName, lastName and email address is same.
    */
    @Override
    public boolean equals(Object obj) {
           return (this.title.equals(((RSSItem) obj).title)
                    && this.link.equals(((RSSItem) obj).link) && this.description
    	                    .equals(((RSSItem) obj).description));
    }
}
