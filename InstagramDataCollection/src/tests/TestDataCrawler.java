package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import crawler.Crawler;
import objects.Data;
import objects.Image;
import objects.InstagramInformation;
import objects.Likes;
import objects.LowImage;
import objects.Pagination;
import objects.TagData;

public class TestDataCrawler {

	private static String API_HOST = "https://api.instagram.com/v1/tags/";
	private static String CLIENT_ID = "c614e88e441148c5b3314c669ae65e38";
	private static TagData tag;
	private static InstagramInformation test = new InstagramInformation();
	private static String SEARCH_TAG = "jmullins202";
	private static Data testPhoto;
	private static Data testPhoto2;
	private static Data testPhoto3;
	private static Data[] allData;

	@BeforeClass
     public static void onceExecutedBeforeAll() 
    {
    	System.out.println("@BeforeClass: onceExecutedBeforeAll");
		StringBuilder searchURL = new StringBuilder();
		searchURL.append(API_HOST).append(SEARCH_TAG).append("/media/recent?callback=?&client_id=").append(CLIENT_ID);
		test = Crawler.crawlInstagram(SEARCH_TAG, searchURL.toString());
		tag = new TagData(SEARCH_TAG);
		
		testPhoto = new Data();
		Likes testLikes = new Likes();
		testLikes.setCount(10);
		testPhoto.setLikes(testLikes);
		Pagination pagination = new Pagination();
		pagination.setNext_url("http://test1Next.com");
		testPhoto.setPagination(pagination);
		Image testImage = new Image();
		LowImage testLowImage = new LowImage();
		testLowImage.setUrl("http://test1.com");
		testImage.setLow_resolution(testLowImage);
		testPhoto.setImage(testImage);
		testPhoto.setTags(new String[]{"test", "design", "summer", "photos"});
		
		testPhoto2 = new Data();
		Likes testLikes2 = new Likes();
		testLikes2.setCount(10);
		testPhoto2.setLikes(testLikes);
		Pagination pagination2 = new Pagination();
		pagination2.setNext_url("http://test2Next.com");
		testPhoto2.setPagination(pagination2);
		Image testImage2 = new Image();
		LowImage testLowImage2 = new LowImage();
		testLowImage2.setUrl("http://test2.com");
		testImage2.setLow_resolution(testLowImage2);
		testPhoto2.setImage(testImage2);
		testPhoto2.setTags(new String[]{"test", "design", "summer"});

		testPhoto3 = new Data();
		Likes testLikes3 = new Likes();
		testLikes3.setCount(10);
		testPhoto3.setLikes(testLikes3);
		Pagination pagination3 = new Pagination();
		pagination3.setNext_url("http://test3Next.com");
		testPhoto3.setPagination(pagination3);
		Image testImage3 = new Image();
		LowImage testLowImage3 = new LowImage();
		testLowImage3.setUrl("http://test3.com");
		testImage3.setLow_resolution(testLowImage3);
		testPhoto3.setImage(testImage3);
		testPhoto3.setTags(new String[]{"test", "creative", "Instagram"});
		
		allData = new Data[4];
		allData[0] = testPhoto;
		allData[1] = testPhoto2;
		allData[2] = testPhoto3;
		allData[3] = testPhoto;

    }
	
	@AfterClass
	public static void after()
	{
		System.out.println("Finished Testing");
	}

	/**
	 * Test checks the tags returned after querying the Instagram API
	 */
	@Test
	public void testCrawlInstagram()
	{
		Data testData = test.getData()[0];
		assertEquals(8,testData.getLikes().getCount());
		String[] tags = {"drozdiet", "jmullins202"};
		Assert.assertArrayEquals(tags, testData.getTags());
	}
	
	/**
	 * Test checks TagData creation
	 */
	@Test
	public void testCreateTag()
	{
		String searchTag = "jmullins202";
		TagData tag = new TagData(searchTag);
		assertEquals(searchTag, tag.getTag());
	}
	
	/**
	 * Checks to see if photo data and links are properly added to TagData
	 */
	@Test
	public void testAddPhotoDataAndLinks()
	{
		List<String> allLinks = new ArrayList<String>();
		List<String> testLinks = tag.addPhotoDataAndLinks(test.getData(), allLinks);
		Assert.assertEquals(1, testLinks.size());
		assertEquals(testLinks.get(0), tag.getLink()[0]);
		
		allLinks = tag.addPhotoDataAndLinks(allData, testLinks);
		assertEquals (4, allLinks.size());
		assertNotEquals(allData, tag.getPhotoData());
		assertEquals (allLinks.size(), tag.getPhotoData().length);
	}
	
	/**
	 * Checks the getAllTags method to see if it is properly finding the most frequently co-occuring tags
	 */
	@Test
	public void testGetAllTags()
	{
		List<String> testTags = Crawler.getAllTags(allData, 3, "jmullins202");
		List<String> tags = new ArrayList<String>();
		tags.add("test");
		tags.add("design");
		tags.add("summer");
		assertTrue(tags.contains("test") && tags.contains("design") && tags.contains("summer"));
		assertFalse(tags.contains("creative"));
		assertEquals(3, tags.size());
		
		testTags = Crawler.getAllTags(allData, 3, "test");
		assertFalse(testTags.contains("test"));
	}
}
