import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScraper{

    public static void main(String[] args) throws IOException {

        final String url = "https://www.bbc.com";

        PageData pageData = scrapePageData(url);
        System.out.println("Title: " + pageData.getTitle());
        System.out.println("\nHeadings:");
        for (String heading : pageData.getHeadings()) {
            System.out.println(heading);
        }

        System.out.println("\nLinks:");
        for (String link : pageData.getLinks()) {
            System.out.println(link);
        }

        System.out.println("\nNews Articles:");
        List<NewsArticle> articles = scrapeNewsArticles(url);
        articles.stream().limit(10).forEach(System.out::println);
    }

    private static PageData scrapePageData(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        PageData data = new PageData();
        data.setTitle(doc.title());
        
        List<String> headings = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Elements hTags = doc.select("h" + i);
            for (Element el : hTags) {
                headings.add("H" + i + ": " + el.text());
            }
        }
        data.setHeadings(headings);

        List<String> links = new ArrayList<>();
        Elements linkTags = doc.select("a[href]");
        for (Element link : linkTags) {
            links.add(link.absUrl("href"));
        }
        data.setLinks(links);

        return data;
    }

    private static List<NewsArticle> scrapeNewsArticles(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        List<NewsArticle> articles = new ArrayList<>();

        Elements articleBlocks = doc.select("article");

        for (Element article : articleBlocks) {
            String title = article.select("h3, h2, h1").text();
            String date = article.select("time").attr("datetime");
            String author = article.select("[rel=author]").text();

            if (!title.isEmpty()) {
                NewsArticle news = new NewsArticle();
                news.setHeadline(title);
                news.setDate(date);
                news.setAuthor(author.isEmpty() ? "Unknown" : author);
                articles.add(news);
            }
        }

        return articles;
    }

    static class PageData {
        private String title;
        private List<String> headings;
        private List<String> links;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getHeadings() {
            return headings;
        }

        public void setHeadings(List<String> headings) {
            this.headings = headings;
        }

        public List<String> getLinks() {
            return links;
        }

        public void setLinks(List<String> links) {
            this.links = links;
        }
    }

    static class NewsArticle {
        private String headline;
        private String date;
        private String author;

        public String getHeadline() {
            return headline;
        }

        public void setHeadline(String headline) {
            this.headline = headline;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        @Override
        public String toString() {
            return String.format("Headline: %s\nDate: %s\nAuthor: %s\n", headline, date, author);
        }
    }
}
