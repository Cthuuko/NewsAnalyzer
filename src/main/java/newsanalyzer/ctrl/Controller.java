package newsanalyzer.ctrl;

import newsapi.NewsApi;
import newsapi.beans.Article;
import newsapi.beans.NewsReponse;
import newsapi.downloader.ParallelDownloader;
import newsapi.downloader.SequentialDownloader;
import newsapi.error.NewsAnalyzerException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Controller {

    public static final String APIKEY = "8faa40daa44941d68eaaef41abe0821e";

    private List<Article> results;
    private List<String> lastSearchUrls = new ArrayList<>();
    private SequentialDownloader seq = new SequentialDownloader();
    private ParallelDownloader par = new ParallelDownloader();

    public void process(NewsApi newsApi) throws NewsAnalyzerException {
        System.out.println("Start process");
        lastSearchUrls = new ArrayList<>();
        try {
            NewsReponse newsResponse = newsApi.getNews();
            List<Article> articles = newsResponse.getArticles();
            for (Article article : articles) {
                // Avoid news links with query parameters
                if (!article.getUrl().contains("?")) {
                    lastSearchUrls.add(article.getUrl());
                }
                downloadContent(newsApi, article);
            }
            results = articles;
        } catch (NullPointerException e) {
            throw new NewsAnalyzerException("There is an error retrieving the data. Please check your internet connection or the search configurations.");
        }

    }


    public void printData() throws NewsAnalyzerException {

        // Courtesy to the following links:
        // https://stackoverflow.com/questions/47842083/java-8-collections-max-size-with-count
        // https://stackoverflow.com/questions/5911174/finding-key-associated-with-max-value-in-a-java-map
        // https://www.baeldung.com/java-collectors-tomap
        // https://stackoverflow.com/questions/32312876/ignore-duplicates-when-producing-map-using-streams

        // Analysing the results
        if (!results.isEmpty()) {
            String providerWithMostArticles = getProviderWithMostArticles(results);
            String shortestAuthorName = getShortestAuthorName(results);
            List<String> sortedTitlesByAlphabet = getSortedTitlesByAlphabet(results);
            List<String> sortedTitlesByLength = getSortedTitlesByLength(results);

            System.out.println(System.lineSeparator() + " Article count: " + results.size());
            System.out.println(System.lineSeparator() + " Provider with most articles: " + providerWithMostArticles);
            System.out.println(System.lineSeparator() + " Author with shortest name: " + shortestAuthorName);
            System.out.println(System.lineSeparator() + " Articles sorted by alphabet: ");
            sortedTitlesByAlphabet.forEach(System.out::println);
            System.out.println(System.lineSeparator() + " Articles sorted by length: ");
            sortedTitlesByLength.forEach(System.out::println);
        } else {
            System.out.println("No results found.");
        }


        System.out.println("End process");
    }

    public void executeDownloaders() {
        if (lastSearchUrls.isEmpty()) {
            System.out.println("There is no search history available.");
        } else {
            seq.process(lastSearchUrls);
            par.process(lastSearchUrls);
        }
    }

    private String getProviderWithMostArticles(List<Article> articles) throws NewsAnalyzerException {
        return articles.stream()
                .collect(Collectors.groupingBy(article -> article.getSource().getName(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new NewsAnalyzerException("There was a problem with the retrieved articles."))
                .getKey();
    }

    private String getShortestAuthorName(List<Article> articles) throws NewsAnalyzerException {
        return articles.stream()
                .filter(article -> article.getAuthor() != null)
                .collect(Collectors.toMap(Article::getAuthor, article -> article.getAuthor().length(), (author, duplicateAuthor) -> author))
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .orElseThrow(() -> new NewsAnalyzerException("There was a problem with the retrieved articles."))
                .getKey();
    }

    private List<String> getSortedTitlesByAlphabet(List<Article> articles) {
        return articles.stream()
                .sorted(Comparator.comparing(Article::getTitle))
                .map(Article::getTitle)
                .collect(Collectors.toList());
    }

    private List<String> getSortedTitlesByLength(List<Article> articles) {
        return articles.stream()
                .sorted(Comparator.comparing(article -> article.getTitle().length()))
                .map(Article::getTitle)
                .collect(Collectors.toList());
    }

    private void downloadContent(NewsApi newsApi, Article article) throws NewsAnalyzerException {
        try {
            article.setContent(newsApi.requestData(article.getUrl()));
        } catch (Exception e) {
            throw new NewsAnalyzerException("There's been an error with downloading the articles.");
        }
    }
}
