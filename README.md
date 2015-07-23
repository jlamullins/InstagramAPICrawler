# InstagramAPICrawler
==============

Summary
--------------

This Java application uses the Instagram API to search for *tags* and *co-occurring* tags. For a given 
tag, (e.g. **#cat**) this application finds and counts the number of co-occurring tags (e.g. if **#cat** and **#dog** appear on the 
same photo they are considered co-occurring. The application will then query to find the co-occrring tags for the 
most frequent tags found with the search seed.

Example:
**#cat** is the search seed. 
**#dog**, **#catlady**, **#meow** are the three most frequent co-occurring tags found with **#cat**.
The application will then use **#dog**, **#catlady** and **#meow** as the search seed to find the tags that frequently
co-occur with these tags. This process continues for a given number of *degrees* (where **#cat** and **#dog** are 1 degree apart).

The result is written to a file (e.g. **instagramJSONData-catTAGS.txt**).

Code
--------------
The main class in this code is the *src.crawler.Crawler.java* class. This has the main and is where you can set
the various parameters.
The JSON object classes are found in the *src.objects* directory. These represent the different JSON objects returned
by the Instagram API.
The *src.tests* directory has the JUNIT tests for this project.

Resources
--------------
This application uses the Instagram API. You will need to set up an application on their website and get a 
client ID. (https://instagram.com/developer/)

This application used Google's GSON jar to convert JSON to Java objects (gson-2.3.1).
(https://github.com/google/gson)