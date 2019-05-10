import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

// I use this file to check when I am temp banned
public class jsoupPractice {

    public static void main(String[] args){
        Document document;
        try {
//            for (int i = 0; i < 300; i++) {
                document = Jsoup.connect("https://www.zillow.com/la-jolla-san-diego-ca/rentals/1_p/").timeout(10000).get();
            Elements linksToCards = document.select("a[href*=/homedetails/]");
                System.out.println(linksToCards.size());
                for (Element link : linksToCards) {
                    System.out.println(link.attr("abs:href"));
//                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
