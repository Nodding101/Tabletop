import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException; 
import java.util.Scanner;
import java.util.regex.Matcher; // Required for regex matching
import java.util.regex.Pattern; // Required for regex matching

public class Tabletop {
    public static void main(String[] args) {
        float average = AverageBrackets();
        System.out.println("Average number of brackets per newsletter: " + average);        
    }

    static String ContentGetter(String urlString) throws IOException, URISyntaxException {
        String articlePattern = "(?s)<article.*?>(.*?)</article>"; 
        Pattern pattern = Pattern.compile(articlePattern);

        String rtn = "";
        try { 
            URI uri = new URI(urlString);
            URL url = uri.toURL();

            String fullHtmlContent;
            
            // 1. Read the entire content into a single String
            try (Scanner sc = new Scanner(url.openStream()).useDelimiter("\\A")) {
                 // Use useDelimiter("\\A") to read the entire stream into one token
                fullHtmlContent = sc.hasNext() ? sc.next() : "";
            }
            
            // 2. Use the regex pattern to find the content inside the <article> tag
            Matcher matcher = pattern.matcher(fullHtmlContent);
            
            if (matcher.find()) {
                // Group 1 contains the content between the opening and closing article tags
                String articleContentHtml = matcher.group(1);    
                rtn = articleContentHtml.replaceAll("<[^>]*>", "");            
            } else {
                System.out.println("Could not find an <article> tag on the page.");
            }

        } catch (URISyntaxException e) {
            System.err.println("Error: Invalid URI syntax: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error: Could not read from URL (Network/IO issue): " + e.getMessage());
        }
        
        return rtn;
    }

    static int BracketCounter(String content){
        int count = 0;
        for (char c : content.toCharArray()) {
            if (c == '(' || c == ')') {
                count++;
            }
        }
        return count;
    }

    static float AverageBrackets(){
        int start = 236;
        int end = 276;
        int totalBrackets = 0;
        int totalNewsletters = 0;
        
        for(int i = start; i <= end; i++){
            String url = "https://www.warwicktabletop.co.uk/newsletters/" + i + "/";
            try {
                String content = ContentGetter(url);
                if(content.isEmpty()) {
                    continue; // Skip if no content was found
                }
                int bracketCount = BracketCounter(content);
                System.out.println("Newsletter " + i + " has " + bracketCount + " brackets.");
                totalBrackets += bracketCount;
                totalNewsletters++;
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Total newsletters processed: " + totalNewsletters);
        System.out.println("Total brackets counted: " + totalBrackets);
        return (float) totalBrackets / totalNewsletters;
    }
}