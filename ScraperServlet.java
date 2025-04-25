import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@WebServlet("/scrape")
public class ScrapeServlet extends HttpServlet {

    static class ScrapedItem {
        String type;
        String content;

        ScrapedItem(String type, String content) {
            this.type = type;
            this.content = content;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        HttpSession session = request.getSession();
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        if (visitCount == null) visitCount = 0;
        session.setAttribute("visitCount", visitCount + 1);

        String url = request.getParameter("url");
        String[] options = request.getParameterValues("options");

        List<ScrapedItem> scrapedData = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url).get();

            if (options != null) {
                for (String option : options) {
                    switch (option) {
                        case "title":
                            scrapedData.add(new ScrapedItem("Title", doc.title()));
                            break;
                        case "links":
                            Elements links = doc.select("a[href]");
                            for (Element link : links) {
                                scrapedData.add(new ScrapedItem("Link", link.attr("abs:href")));
                            }
                            break;
                        case "images":
                            Elements images = doc.select("img[src]");
                            for (Element img : images) {
                                scrapedData.add(new ScrapedItem("Image", img.attr("abs:src")));
                            }
                            break;
                    }
                }
            }


            Gson gson = new Gson();
            String json = gson.toJson(scrapedData);

            request.setAttribute("scrapedData", scrapedData);
            request.setAttribute("visitCount", visitCount + 1);
            request.setAttribute("jsonData", json);


            RequestDispatcher dispatcher = request.getRequestDispatcher("result.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<p>Error scraping the page: " + e.getMessage() + "</p>");
        }
    }
}
