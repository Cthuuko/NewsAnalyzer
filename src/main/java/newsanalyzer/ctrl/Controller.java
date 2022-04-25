package newsanalyzer.ctrl;

import newsapi.NewsApi;
import newsapi.beans.Article;
import newsapi.beans.NewsReponse;

import java.util.List;

public class Controller {

    public static final String APIKEY = "8faa40daa44941d68eaaef41abe0821e";

    public void process(NewsApi newsApi) throws NewsAnalyzerException {
        System.out.println("Start process");

        //TODO implement Error handling

        //TODO load the news based on the parameters
        try {
            NewsReponse newsResponse = (NewsReponse) getData(newsApi);
            if (newsResponse != null) {
                List<Article> articles = newsResponse.getArticles();
                articles.forEach(article -> System.out.println(article.toString()));
            }
        } catch (Exception e) {
            throw new NewsAnalyzerException("Something went wrong. Please check your search configuration and the validity of your API key");
        }


        //TODO implement methods for analysis

        System.out.println("End process");
    }


    public Object getData(NewsApi newsApi) {
        return newsApi.getNews();
    }
}
