package newsapi;

import newsapi.beans.Article;
import newsapi.beans.NewsReponse;
import newsapi.enums.Category;
import newsapi.enums.Country;
import newsapi.enums.Endpoint;

import java.util.List;

public class NewsAPIExample {

    public static final String APIKEY = "8faa40daa44941d68eaaef41abe0821e";

    public static void main(String[] args) {

        NewsApi newsApi = new NewsApiBuilder()
                .setApiKey(APIKEY)
                .setQ("corona")
                .setEndPoint(Endpoint.TOP_HEADLINES)
                .setSourceCountry(Country.at)
                .setSourceCategory(Category.health)
                .createNewsApi();

        NewsReponse newsResponse = newsApi.getNews();
        if (newsResponse != null) {
            List<Article> articles = newsResponse.getArticles();
            articles.stream().forEach(article -> System.out.println(article.toString()));
        }

        newsApi = new NewsApiBuilder()
                .setApiKey(APIKEY)
                .setQ("corona")
                .setEndPoint(Endpoint.EVERYTHING)
                .setFrom("2020-03-20")
                .setExcludeDomains("Lifehacker.com")
                .createNewsApi();

        newsResponse = newsApi.getNews();
        if (newsResponse != null) {
            List<Article> articles = newsResponse.getArticles();
            articles.stream().forEach(article -> System.out.println(article.toString()));
        }
    }
}
