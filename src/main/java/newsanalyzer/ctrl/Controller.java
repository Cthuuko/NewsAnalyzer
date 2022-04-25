package newsanalyzer.ctrl;

import newsapi.NewsApi;
import newsapi.beans.Article;
import newsapi.beans.NewsReponse;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

                // Courtesy to the following links:
                // https://stackoverflow.com/questions/47842083/java-8-collections-max-size-with-count
                // https://stackoverflow.com/questions/5911174/finding-key-associated-with-max-value-in-a-java-map
                // https://www.baeldung.com/java-collectors-tomap
                // https://stackoverflow.com/questions/32312876/ignore-duplicates-when-producing-map-using-streams

                String providerWithMostArticles = getProviderWithMostArticles(articles);
                String shortestAuthorName = getShortestAuthorName(articles);
                List<String> sortedByLengthAndAlphabet = getSortedTitles(articles);

                System.out.println(System.lineSeparator() + " Article count: " + articles.size());
                System.out.println(System.lineSeparator() + " Provider with most articles: " + providerWithMostArticles);
                System.out.println(System.lineSeparator() + " Author with shortest name: " + shortestAuthorName);
                System.out.println(System.lineSeparator() + " Articles sorted by length and title: ");
                sortedByLengthAndAlphabet.forEach(System.out::println);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new NewsAnalyzerException("Something went wrong. Please check your search configuration and the validity of your API key");
        }


        //TODO implement methods for analysis

        System.out.println("End process");
    }


    public Object getData(NewsApi newsApi) {
        return newsApi.getNews();
    }

    private String getProviderWithMostArticles(List<Article> articles) {
        return articles.stream()
                .collect(Collectors.groupingBy(article -> article.getSource().getName(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(NoSuchElementException::new)
                .getKey();
    }

    private String getShortestAuthorName(List<Article> articles) {
        return articles.stream()
                .filter(article -> article.getAuthor() != null)
                .collect(Collectors.toMap(Article::getAuthor, article -> article.getAuthor().length(), (author, duplicateAuthor) -> author))
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .orElseThrow(NoSuchElementException::new)
                .getKey();
    }

    private List<String> getSortedTitles(List<Article> articles) {
        return articles.stream()
                .sorted(Comparator.comparing(Article::getTitle))
                .map(Article::getTitle)
                .collect(Collectors.toList());
    }
}
