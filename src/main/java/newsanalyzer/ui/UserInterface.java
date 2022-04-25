package newsanalyzer.ui;


import newsanalyzer.ctrl.Controller;
import newsapi.NewsApi;
import newsapi.NewsApiBuilder;
import newsapi.enums.Category;
import newsapi.enums.Country;
import newsapi.enums.Endpoint;
import newsapi.error.NewsAnalyzerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInterface {

    private Controller ctrl = new Controller();

    public void getDataFromCtrl1() {
        System.out.println("Search corona news");

        NewsApi newsApi = new NewsApiBuilder()
                .setApiKey(Controller.APIKEY)
                .setQ("corona")
                .setEndPoint(Endpoint.TOP_HEADLINES)
                .setSourceCountry(Country.at)
                .setSourceCategory(Category.health)
                .createNewsApi();

        readData(newsApi);
    }

    public void getDataFromCtrl2() {
        System.out.println("Search chess news");

        NewsApi newsApi = new NewsApiBuilder()
                .setApiKey(Controller.APIKEY)
                .setQ("chess")
                .setEndPoint(Endpoint.EVERYTHING)
                .setFrom("2022-03-25")
                .createNewsApi();
        readData(newsApi);
    }

    public void getDataFromCtrl3() {
        System.out.println("Search all news");

        NewsApi newsApi = new NewsApiBuilder()
                .setApiKey(Controller.APIKEY)
                .setEndPoint(Endpoint.EVERYTHING)
                .createNewsApi();
        readData(newsApi);
    }

    public void getDataFromCustomInput() {
        System.out.println("Search with errors in config");

        System.out.println("Type in your query: (keep it empty to search every kind of news)");
        String query = readLine();
        if (query.equals("")) query = null;

        Endpoint endpoint = null;

        while (endpoint == null) {
            System.out.println("Type in your preferred type of news (Type a or b)");
            System.out.println("a) Everything");
            System.out.println("b) Headlines");

            String choice = readLine();

            if (choice.equals("a")) {
                endpoint = Endpoint.EVERYTHING;
            } else if (choice.equals("b")) {
                endpoint = Endpoint.TOP_HEADLINES;
            } else {
                System.out.println("Your input is invalid. Please try again");
            }
        }

        NewsApi newsApi = new NewsApiBuilder()
                .setApiKey(Controller.APIKEY)
                .setQ(query)
                .setEndPoint(endpoint)
                .createNewsApi();
        readData(newsApi);
    }

    public void start() {
        Menu<Runnable> menu = new Menu<>("User Interfacx");
        menu.setTitel("Wählen Sie aus:");
        menu.insert("a", "Search Top News about Corona", this::getDataFromCtrl1);
        menu.insert("b", "Search Chess news from March 2022 until today", this::getDataFromCtrl2);
        menu.insert("c", "Search all news", this::getDataFromCtrl3);
        menu.insert("e", "Custom Input:", this::getDataFromCustomInput);
        menu.insert("q", "Quit", null);
        Runnable choice;
        while ((choice = menu.exec()) != null) {
            choice.run();
        }
        System.out.println("Program finished");
    }


    protected String readLine() {
        String value = "\0";
        BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            value = inReader.readLine();
        } catch (IOException ignored) {
        }
        return value.trim();
    }

    protected Double readDouble(int lowerlimit, int upperlimit) {
        Double number = null;
        while (number == null) {
            String str = this.readLine();
            try {
                number = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number:");
                continue;
            }
            if (number < lowerlimit) {
                System.out.println("Please enter a higher number:");
                number = null;
            } else if (number > upperlimit) {
                System.out.println("Please enter a lower number:");
                number = null;
            }
        }
        return number;
    }

    private void readData(NewsApi newsApi) {
        try {
            ctrl.process(newsApi);
            ctrl.printData();
        } catch (NewsAnalyzerException e) {
            System.out.println(e.getMessage());
        }
    }
}
