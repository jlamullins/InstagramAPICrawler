//package crawler;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Array;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.LinkedHashMap;
//import java.util.LinkedList;
//import java.util.Map.Entry;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import objects.Data;
//import objects.FinalData;
//import objects.InstagramInformation;
//import objects.TagData;
//
//public class OldCrawler {
//
//	private static String CLIENT_ID = "c614e88e441148c5b3314c669ae65e38";
//	private static String API_HOST = "https://api.instagram.com";
//	static List<String> tagsSearched = new ArrayList<String>();
//	static StringBuilder globalWriter;
//	private static List<TagData> currentLevel = new ArrayList<TagData>();
//	private static List<TagData> nextLevel = new ArrayList<TagData>();
//	private static List<TagData> toClear = new ArrayList<TagData>();
//	private static List<String> allLinks = new ArrayList<String>();
//	private static String startTag = "engagement";
//	private static int degree = 2;
//	private static boolean skip = false;
//	private static int NUM_HITS = 50;
//	private static BufferedReader in;
//	private static Gson gson;
//	private static InstagramInformation photo;
//	private static long startTime = System.currentTimeMillis(); 
//	private static long estimatedTime = System.currentTimeMillis();
//	private static int hits = 0;
//
//	public static void main(String[] args) throws InterruptedException {
//		try {
//			String[] searchSeeds = {"proposalideas"};
//			for (String seed : searchSeeds)
//			{
//				// Run the search starting at a specific tag
//				globalWriter = new StringBuilder();
//				startTag = seed;
//				FinalData info = runSearch(startTag);
//				// Generate JSON from resulting data
//			//	String json = generateJSON(info);
//				String json = "TESTING";
//				// Write to file
//				toFile(json,degree);
//				
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static void toFile(String json, int degree) throws FileNotFoundException,
//			UnsupportedEncodingException {
//		String depth = "";
//		switch (degree) {
//        case 0:  depth = "zero";
//                 break;
//        case 1:  depth = "first";
//                 break;
//        case 2:  depth = "second";
//                 break;
//        case 3:  depth = "third";
//                 break;
//         default: depth = "";
//         break;
//		}
//		PrintWriter writer = new PrintWriter(startTag+"-"+depth+".txt", "UTF-8");
//		writer.println(json);
//		writer.close();
//		writer = new PrintWriter("instagramJSONData-"+startTag+"TAGS.txt", "UTF-8");
//		writer.println(globalWriter.toString());
//		writer.close();
//	}
//
//	/**
//	 * Runs an Instagram image search based on the given tag. Also searches for
//	 * images on the related tags
//	 * 
//	 * @param tag
//	 * @return
//	 * @throws MalformedURLException
//	 * @throws IOException
//	 * @throws InterruptedException 
//	 */
//	private static FinalData runSearch(String tag) throws MalformedURLException, IOException, InterruptedException {
//		// This is the search seed
//		String url = API_HOST + "/v1/tags/" + tag + "/media/recent?callback=?&client_id=" + CLIENT_ID;
//		InstagramInformation data = getInstagramInfo(tag, url);
//		TagData searchTag = new TagData(tag);
//		// searchTag.addPhotoDataAndLinks(photoData, allLinks);
//		int count = 0;
//		while (count < NUM_HITS)
//		{
//			searchTag.addPhotoDataAndLinks(data.getData(), allLinks);
//			url = data.getPagination().getNext_url();
//			data = getInstagramInfo(tag,url);
//			count++;
//			if (skip)
//			{
//				count = NUM_HITS + 1;
//				skip = false;
//			}
//		}
//		searchTag.setId("0");
//		tagsSearched.add(tag);
//		searchTag = getRelatedPhotoData(searchTag.getPhotoData(), degree, searchTag);
//		globalWriter.append("************************************ \n\n All Tags Searched:" + tagsSearched.toString());
//		FinalData dataToJson = new FinalData();
//		dataToJson.setData(searchTag);
//		return dataToJson;
//	}
//
//	/**
//	 * Sends a request to Instagram for the given tag
//	 * @param tag
//	 * @param url
//	 * @return
//	 */
//	private static InstagramInformation getInstagramInfo(String tag, String url) {
//		try{
//		if (hits == 70)
//		{
//			// Instagram limits requests to 5000 per hour (sliding window).
//			// This means we can do roughly 83 requests per minute	
//			estimatedTime = System.currentTimeMillis() - startTime;
//			if (estimatedTime < 60000) // Took less than a minute to do 83 requests, so we wait
//			{
//		//		System.out.println("Up to " + hits + " hits in " + estimatedTime + ". Waiting...");
//				Thread.sleep(60000 - estimatedTime);
//				startTime = System.currentTimeMillis(); // Reset start time
//			}
//			hits = 0;
//		}
//		hits++;
//		// Build the Instagram search and read the JSON file
//		URL apiSearchURL = new URL(url);
//		URLConnection apiConnection = apiSearchURL.openConnection();
//		in = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
//		String inputLine = "", response = "";
//		while ((inputLine = in.readLine()) != null) {
//			response += inputLine;
//		}
//		in.close();
//		gson = new Gson();
//		photo = gson.fromJson(response, InstagramInformation.class);
//		
//		}
//		catch(Exception e)
//		{
//			System.out.println("Failed on : " + tag);
//			System.out.println(e.toString());
//			skip = true;
//			return photo;
//		}
//		return photo;
//	}
//
//	private static TagData getRelatedPhotoData(Data[] data, int depth,
//			TagData firstTag) throws MalformedURLException, IOException, InterruptedException {
//		int i = 0;
//		currentLevel.add(firstTag); // Search seed
//		TagData child;
//		InstagramInformation info;
//		List<String> allTags;
//		List<TagData> children;
//		while (i < depth) {
//			globalWriter.append("Degree:" + i + "\n");
//			for (TagData parentTag : currentLevel) // For each tag on this level of the tree
//			{
//				globalWriter.append("Search seed: " + parentTag.getTag() + "\n");	
//				data = parentTag.getPhotoData();
//				// Get all the child tags
//				allTags = getAllTags(data, 20);
//				if (!allTags.isEmpty()) {
//					int nodeCount = 1;
//					children = new ArrayList<TagData>();
//					for (String curTag : allTags) // For each child tag, add to
//													// the parent
//					{
//							if (null != curTag && !curTag.isEmpty()
//									&& !curTag.contains("???")
//									&& !curTag.contains("�")
//									&& !curTag.contains("�")
//									&& !curTag.contains("�")
//									&& !curTag.contains("�")
//									&& !curTag.contains("�r")	
//									&& !curTag.contains("�")
//									&& !curTag.contains("�")
//									&& !curTag.contains("�")
//									&& !curTag.contains("�")
//									&& !curTag.contains("??????")
//									&& !curTag.contains("????")) {
//								// Set up child node
//								child = new TagData(curTag, parentTag,
//										parentTag.getId() + nodeCount + "");
//								if (nodeCount > 9)
//								{
//									child.setId(parentTag.getId() + "a");
//								}
//								// Get Instagram Data
//								String url = API_HOST + "/v1/tags/" + curTag + "/media/recent?callback=?&client_id=" + CLIENT_ID;
//								int count = 0;
//								System.out.println(curTag);
//								while (count < NUM_HITS)
//								{
//									info = getInstagramInfo(curTag,url);
//									allLinks = child.addPhotoDataAndLinks(info.getData(), allLinks);
//									url = info.getPagination().getNext_url();
//									count++;
//									if (skip)
//									{
//										count = NUM_HITS+1;
//										skip = false;
//									}
//								}
//								
//								tagsSearched.add(curTag);
//								children.add(child);
//								// Add to next level
//								nextLevel.add(child);
//								toClear.add(child);
//								nodeCount++;
//							}
//					}
//					// Add children to parent
//					parentTag.setChild(children.toArray(new TagData[children.size()]));
//					// Clear all photoData
//					parentTag.clearPhotoData();
//				} // end if !allTags
//			} // end for currentLevel
//			currentLevel.clear();
//			currentLevel = nextLevel;
//			nextLevel = new ArrayList<TagData>();
//			FinalData dataToJson = new FinalData();
//			dataToJson.setData(firstTag);
//			// Write to file
//		//	toFile(generateJSON(dataToJson),i);
//			i++;
//		} // end while
//		
//		for (TagData tagData : toClear) 
//		{
//			tagData.clearPhotoData();
//		}
//		return firstTag;
//	}
//
//	private static List<String> getAllTags(Data[] pd, int numTags) {
//		List<String> tags = new ArrayList<String>();
//		Map<String, Integer> tagFrequency = new HashMap<String, Integer>();
//		int count = 0;
//		if (null != pd) {
//			// Find the most frequently used tags
//			for (Data photo : pd) {
//				for (String t : photo.getTags()) {
//					t.replaceAll("[^\\x00-\\x7F]", "");
//					t.replace("?", "");
//						if (null != t && !t.isEmpty() && !t.contains("???")
//								&& !t.contains("�") && !t.contains("�")
//								&& !t.contains("�") && !t.contains("�")
//								&& !t.contains("�") && !t.contains("�")
//								&& !t.contains("????")
//								&& !t.contains("�")
//								&& !t.contains("�")
//								&& !t.contains("?????")
//								&& !t.contains("?????????")
//								&& !t.contains("�")
//								&& !t.contains("�")
//								&& !t.contains("??")
//								&& !t.contains("?")
//								&& !t.contains("�")
//								&& !t.contains("�")
//								&& !t.contains("�")
//								&& !t.contains("�")
//								&& !t.contains("�")
//								&& !t.contains("�")
//								&& checkForCJK(t)) {
//							if (tagFrequency.containsKey(t)) // We've seen this tag before
//							{
//								count = tagFrequency.get(t);
//								count++;
//								tagFrequency.put(t, count);
//							} else {
//								tagFrequency.put(t, 1);
//							}
//						}
//				}
//			}
//
//			// Take the top numTags
//			tagFrequency = sortByComparator(tagFrequency, false);
//			globalWriter.append(tagFrequency.toString() + "\n");
//			count = 0;
//			for (String tag : tagFrequency.keySet()) {
//				if (!tag.equalsIgnoreCase(startTag))
//				{
//					tags.add(tag);
//					count++;
//				}
//				if (count > numTags) {
//					break;
//				}
//			}
//
//		}
//		return tags;
//	}
//
//	private static boolean checkForCJK(String t) {
//		char[] chars = t.toCharArray();
//		for (char c : chars)
//		{
//		    if ((Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
//		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
//		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B)
//		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS)
//		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
//		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT)
//		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
//		            || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS)) {
//		        return false;
//		    }
//		}
//		return true;
//	}
//	
//	
//	public static Data[] concatenate(Data[] A, Data[] B) {
//		int aLen = A.length;
//		int bLen = B.length;
//		@SuppressWarnings("unchecked")
//		Data[] C = (Data[]) Array.newInstance(A.getClass().getComponentType(),
//				aLen + bLen);
//		System.arraycopy(A, 0, C, 0, aLen);
//		System.arraycopy(B, 0, C, aLen, bLen);
//
//		return C;
//	}
//
//	private static String generateJSON(FinalData info)
//	{
//		StringBuilder result = new StringBuilder();
//		Gson g = new GsonBuilder().setPrettyPrinting().create();
//		result.append(g.toJson(info));
//		return result.toString();
//	}
//
//	private static Map<String, Integer> sortByComparator(
//			Map<String, Integer> unsortMap, final boolean order) 
//	{
//		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(
//				unsortMap.entrySet());
//		// Sorting the list based on values
//		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
//			public int compare(Entry<String, Integer> o1,
//					Entry<String, Integer> o2) {
//				if (order) {
//					return o1.getValue().compareTo(o2.getValue());
//				} else {
//					return o2.getValue().compareTo(o1.getValue());
//
//				}
//			}
//		});
//		// Maintaining insertion order with the help of LinkedList
//		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
//		for (Entry<String, Integer> entry : list) {
//			sortedMap.put(entry.getKey(), entry.getValue());
//		}
//
//		return sortedMap;
//	}
//}
