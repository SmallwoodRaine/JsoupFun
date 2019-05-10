/*
 * This file web scrapes all neccessary data EXCEPT images and am currently 
 * waiting on apartments.com data to be stored in Firebase so I can check 
 * if the address (found in the url) already exists in the database before 
 * storing it.
 * 
 * I have another version of this code that I am using for practicing firebase.  
 */

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

    public static void main(String[] args) 
		    throws InterruptedException, IOException {
        
	// Used for temp bans
        // TimeUnit.MINUTES.sleep(10);
        
	Strings urls = new Strings();
        
	for(int i = 1; i<= 12; i++) {
	    String initialLink = 
		    "https://www.zillow.com/la-jolla-san-diego-ca/rentals/"; 
            
	    // Find all apartment urls in initialLink
	    urls = grabUrls(urls, initialLink + i + "_p/");
            TimeUnit.SECONDS.sleep(30);
        }
	// Testing
        for(String url: urls)
            System.out.println(url);
        
	// TimeUnit.MINUTES.sleep(5);
        // Explore all urls and output there data to a csv FOR NOW until 
	// we get firebase working then output to firebase json objects
	urlsDataExplorer(urls);

        System.out.println("finished");
    }
	
    	// This function explores all urls and CURRENTLY outputs data to a csv
    private static void urlsDataExplorer(Strings urls) 
		    throws IOException, InterruptedException {
        
	Document doc;
        FileOutputStream out = new FileOutputStream("zillow.csv");
        PrintWriter csv = new PrintWriter(out);
        csv.println("name, price, bed, laundry," +
			" heating, cooling, pets, parking, url");
        for (String url : urls) {
            TimeUnit.SECONDS.sleep(30);
 	
	// Devins code copy and pasted
	    // the large number for timeout protects against web socket timeouts
            doc = Jsoup.connect(url).timeout(999999999).get();

            Elements address = doc.select(".ds-address-container");

            Elements bed = doc.select(".ds-bed-bath-living-area-container");

            Elements miscDetails = doc.select(".ds-home-fact-list-item");

            Elements price = doc.select(".ds-price");	
	
            // Address, bed num, and price loop
            for (int i = 1; i < address.size(); i++) {

                // changing this because you can't change the delimeter 
		// for a CSv without using an API/library
                String add = address.get(i).text().replace(',', '|');
                String pri = price.get(i).text().replace(',', '|');
                String bbb = bed.get(i).text().replace(',', '|');

                csv.print((add + "," + pri + "," + bbb)); //write out on the same line
            }

            // All the misc details like pets and parking space
            for (int i = 0; i < miscDetails.size(); i++) {
                
		String misc = (miscDetails.get(i).text()).replace(',', '|');
                csv.print(miscDetails.get(i).text() + ','); //continue writing out on the same line
            }

            csv.println(url); // prints the url at the same line 
	    		      // and then makes a newline
        }

        csv.close();
        System.out.println("Finished");
        }
	// Emd of Devins code




	// This function finds all links that link to apartment details and if it find
	// s a link for a building (which contains many individual rooms recursively 
	// calls the building and grabs link to each room and stores in urls.
    private static Strings grabUrls(Strings urls, String urlToSearch){

        try{
            TimeUnit.SECONDS.sleep(30);
            Document document = Jsoup.connect(urlToSearch).get();
            Elements links = document.select("a[href*=/homedetails/]");
            links.addAll(document.select("a[href*=/b/]"));
            for(Element link: links)
            {
		// grab url from elemment
                String url = link.attr("abs:href");
                
		// worthless links
		if(url.contains("browse"))
                    continue;

		// is a "building" 
                else if(url.contains("/b/")){
                    urls = grabUrls(urls, url);
                }
                
		// useable link
		else if(url.contains("/homedetails/") && !urls.contains(url) ){
                    urls.add(url);
                }
            }
        }
        catch (IOException e){
         e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return urls;
    }

}
// To lazy to type ArrayList<String> everytime LOL
class Strings extends ArrayList<String>{}


