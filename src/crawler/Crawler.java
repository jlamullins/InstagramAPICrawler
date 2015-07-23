package crawler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

import objects.InstagramInformation;
import objects.TagData;
import objects.Data;

public class Crawler
{
	private static final int NUM_HITS = 100;
	private static final String API_HOST = "https://api.instagram.com/v1/tags/";
	private static final String CLIENT_ID = "c614e88e441148c5b3314c669ae65e38";
	static List<String> tagsSearched = new ArrayList<String>();
	private static boolean SKIP = false;
	static StringBuilder globalWriter = new StringBuilder();
	private static int hits = 0;
	private static long estimatedTime = System.currentTimeMillis();
	private static long startTime = System.currentTimeMillis();

	
	public static void main(String[] args) 
	{
		String[] tags = {"wine", "wineholder", "winestorage"};
		for (String tag : tags)
		{
			crawlAndWriteData(tag);
			tagsSearched = new ArrayList<String>();
			globalWriter = new StringBuilder();
			System.out.println("FINISHED:: " + tag);
		}
	}
	
	
	/**
	 * For a given search tag, this method will query Instagram for that tag.
	 * @param string
	 * @return 
	 */
	private static void crawlAndWriteData(String searchTag) 
	{
		try 
		{
			getRelatedPhotoData(2, searchTag);
			globalWriter.append("************************************ \n\n All Tags Searched:" + tagsSearched.toString());
			toFile(searchTag);
		} 
		catch (Exception e) 
		{
			System.out.println("crawlAndWriteData:::: ");
			e.printStackTrace();
		} 
	}


	/**
	 * Runs an Instagram search using the given search tag and URL. 
	 * Converts the data from a JSON object to an InstagramInformation data object
	 * @param searchTag
	 * @param searchURL
	 * @return InstagramInformation
	 */
	public static InstagramInformation crawlInstagram(String searchTag,
			String searchURL)
	{
		InstagramInformation data = new InstagramInformation();
		try 
		{
			if (hits == 70)
			{
				// Instagram limits requests to 5000 per hour (sliding window).
				estimatedTime = System.currentTimeMillis() - startTime;
				if (estimatedTime < 60000)
				{
					Thread.sleep(60000 - estimatedTime);
					startTime = System.currentTimeMillis(); // Reset start time
				}
				hits = 0;
			}
			hits++;
			URL apiSearchURL = new URL(searchURL);
			URLConnection apiConnection = apiSearchURL.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String inputLine = "";
			while ((inputLine = in.readLine()) != null)
			{
				response.append(inputLine);
			}
			in.close();
			Gson gson = new Gson();
			data = gson.fromJson(response.toString(), InstagramInformation.class);
		} 
		catch (Exception e)
		{
			System.out.println("Failed on : " + searchTag);
			e.printStackTrace();
			SKIP = true;
			return data;
		}
		return data;
	}
	
	private static void getRelatedPhotoData(int degrees, String firstTag)
	{
		int i = 0;
		List<String> currentLevel = new ArrayList<String>();
		List<String> nextLevel = new ArrayList<String>();

		currentLevel.add(firstTag);
		// While i is less than the number of degrees we are expanding to
		while ( i < degrees)
		{
			globalWriter.append("Degree:" + i + "\n");
			// For each tag in this level
			for (String tag : currentLevel)
			{				
				globalWriter.append("TAG: " + tag + "\n");
				// Query Instagram x number of times to build a list of connected tags
				StringBuilder searchURL = new StringBuilder();
				searchURL.append(API_HOST).append(tag).append("/media/recent?callback=?&client_id=").append(CLIENT_ID);
				int count = 0;
				List <String> allLinks = new ArrayList<String>();
				TagData searchTag = new TagData(tag);
				String url = searchURL.toString();
				while (count < NUM_HITS && !url.equals(""))
				{
					InstagramInformation instagramData  = crawlInstagram(tag, url);					
					// Extract Photo and Links Data
					// Save all links to prevent duplicates
					allLinks = searchTag.addPhotoDataAndLinks(instagramData.getData(), allLinks);
					url = instagramData.getPagination().getNext_url();
					count ++ ;
					if (SKIP)
					{
						count = NUM_HITS + 1;
						SKIP  = false;
					}
				}
				System.out.println("Tag: " + tag);
				tagsSearched.add(tag);
				Data[] data = searchTag.getPhotoData();
				
				// Get co-occurring tags
				List<String> allTags = getAllTags(data, 20, tag);
				
				// Add co-occurring tags to next level to be searched
				nextLevel.addAll(allTags);
			}
			currentLevel.clear();
			currentLevel = nextLevel;
			nextLevel = new ArrayList<String>();
			i++;
		} // end while i < degrees
	} // end getRelatedPhotoData
	
	/**
	 * Returns a list of x number of the most frequent co-occurring tag.
	 * Filters out some tags with non-English characters.
	 * @param photoData
	 * @param numTags
	 * @return tagList
	 */
	public static List<String> getAllTags(Data[] photoData, int numTags, String searchTag)
	{
		List<String> tags = new ArrayList<String>();
		Map<String, Integer> tagFrequency = new HashMap<String, Integer>();
		int count = 0;
		if (null != photoData)
		{
			// Find the most frequently used tags
			for (Data photo : photoData) 
			{
				for (String tag : photo.getTags())
				{
					tag.replaceAll("[^\\x00-\\x7F]", "");
					tag.replace("?", "");
						if (null != tag && !tag.isEmpty() && !tag.contains("???")
								&& !tag.contains("�") && !tag.contains("�")
								&& !tag.contains("�") && !tag.contains("�")
								&& !tag.contains("�") && !tag.contains("�")
								&& !tag.contains("????")
								&& !tag.contains("�")
								&& !tag.contains("�")
								&& !tag.contains("?????")
								&& !tag.contains("?????????")
								&& !tag.contains("�")
								&& !tag.contains("�")
								&& !tag.contains("??")
								&& !tag.contains("?")
								&& !tag.contains("�")
								&& !tag.contains("�")
								&& !tag.contains("�")
								&& !tag.contains("�")
								&& !tag.contains("�")
								&& !tag.contains("�")
								&& hasNoCJKCharacters(tag)) 
						{
							// Track the frequency of each tag
							if (tagFrequency.containsKey(tag))
							{
								count = tagFrequency.get(tag);
								count++;
								tagFrequency.put(tag, count);
							} 
							else 
							{
								tagFrequency.put(tag, 1);
							}
						}
				} // end for each tag
				
			}
			// Find the top numTags
			tagFrequency = sortByComparator(tagFrequency, false);
			globalWriter.append(tagFrequency.toString() + "\n");
			count = 0;
			for (String tag : tagFrequency.keySet())
			{
				// Don't want to include the tag we searched on
				if (!tag.equalsIgnoreCase(searchTag))
				{
					tags.add(tag);
					count ++ ;
				}
				if (count == numTags)
				{
					break;
				}
			}
		}
		return tags;
	}
	
	/**
	 * Compares the elements in the given map. Sorts them based on their value.
	 * Returns the sorted list.
	 * @param unsortMap
	 * @param order
	 * @return
	 */
	private static Map<String, Integer> sortByComparator(
			Map<String, Integer> unsortMap, final boolean order) 
	{
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(
				unsortMap.entrySet());
		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) 
			{
				if (order) 
				{
					return o1.getValue().compareTo(o2.getValue());
				} 
				else 
				{
					return o2.getValue().compareTo(o1.getValue());

				}
			}
		});
		// Maintaining insertion order with the help of LinkedList
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> entry : list) 
		{
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	
	/**
	 * Checks for CJK characters. Returns false if the given string contains one.
	 * @param tag
	 * @return boolean
	 */
	private static boolean hasNoCJKCharacters(String tag) 
	{
		char[] chars = tag.toCharArray();
		for (char c : chars)
		{
		    if ((Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B)
		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS)
		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT)
		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS)) {
		        return false;
		    }
		}
		return true;
	}
	
	
	private static void toFile(String startTag) throws FileNotFoundException,
	UnsupportedEncodingException 
	{
		PrintWriter writer = new PrintWriter("instagramJSONData-"+startTag+"TAGS.txt", "UTF-8");
		writer.println(globalWriter.toString());
		writer.close();
	}
	

}
