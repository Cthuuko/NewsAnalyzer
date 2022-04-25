package newsapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import newsapi.beans.NewsReponse;
import newsapi.enums.*;
import newsapi.error.NewsAnalyzerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NewsApi {

    public static final String DELIMITER = "&";

    public static final String NEWS_API_URL = "http://newsapi.org/v2/%s?q=%s&apiKey=%s";

    private Endpoint endpoint;
    private String q;
    private String qInTitle;
    private Country sourceCountry;
    private Category sourceCategory;
    private String domains;
    private String excludeDomains;
    private String from;
    private String to;
    private Language language;
    private SortBy sortBy;
    private String pageSize;
    private String page;
    private String apiKey;

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public String getQ() {
        return q;
    }

    public String getqInTitle() {
        return qInTitle;
    }

    public Country getSourceCountry() {
        return sourceCountry;
    }

    public Category getSourceCategory() {
        return sourceCategory;
    }

    public String getDomains() {
        return domains;
    }

    public String getExcludeDomains() {
        return excludeDomains;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Language getLanguage() {
        return language;
    }

    public SortBy getSortBy() {
        return sortBy;
    }

    public String getPageSize() {
        return pageSize;
    }

    public String getPage() {
        return page;
    }

    public String getApiKey() {
        return apiKey;
    }

    public NewsApi(String q, String qInTitle, Country sourceCountry, Category sourceCategory, String domains, String excludeDomains, String from, String to, Language language, SortBy sortBy, String pageSize, String page, String apiKey, Endpoint endpoint) {
        this.q = q;
        this.qInTitle = qInTitle;
        this.sourceCountry = sourceCountry;
        this.sourceCategory = sourceCategory;
        this.domains = domains;
        this.excludeDomains = excludeDomains;
        this.from = from;
        this.to = to;
        this.language = language;
        this.sortBy = sortBy;
        this.pageSize = pageSize;
        this.page = page;
        this.apiKey = apiKey;
        this.endpoint = endpoint;
    }

    public String requestData(String url) throws NewsAnalyzerException {
        System.out.println("URL: " + url);
        URL obj;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            throw new NewsAnalyzerException("The current URL is malformed. Please check the URL");
        }
        HttpURLConnection con;
        StringBuilder response = new StringBuilder();
        try {
            con = (HttpURLConnection) obj.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            throw new NewsAnalyzerException("Response could not be read. Please check your internet connection or click the URL for further information.");
        }
        return response.toString();
    }

    protected String buildURL() throws NewsAnalyzerException {
        try {
            String urlbase = String.format(NEWS_API_URL, getEndpoint().getValue(), getQ(), getApiKey());
            StringBuilder sb = new StringBuilder(urlbase);

            if (getFrom() != null) {
                sb.append(DELIMITER).append("from=").append(getFrom());
            }
            if (getTo() != null) {
                sb.append(DELIMITER).append("to=").append(getTo());
            }
            if (getPage() != null) {
                sb.append(DELIMITER).append("page=").append(getPage());
            }
            if (getPageSize() != null) {
                sb.append(DELIMITER).append("pageSize=").append(getPageSize());
            }
            if (getLanguage() != null) {
                sb.append(DELIMITER).append("language=").append(getLanguage());
            }
            if (getSourceCountry() != null) {
                sb.append(DELIMITER).append("country=").append(getSourceCountry());
            }
            if (getSourceCategory() != null) {
                sb.append(DELIMITER).append("category=").append(getSourceCategory());
            }
            if (getDomains() != null) {
                sb.append(DELIMITER).append("domains=").append(getDomains());
            }
            if (getExcludeDomains() != null) {
                sb.append(DELIMITER).append("excludeDomains=").append(getExcludeDomains());
            }
            if (getqInTitle() != null) {
                sb.append(DELIMITER).append("qInTitle=").append(getqInTitle());
            }
            if (getSortBy() != null) {
                sb.append(DELIMITER).append("sortBy=").append(getSortBy());
            }
            return sb.toString();
        } catch (NullPointerException e) {
            throw new NewsAnalyzerException("The given configuration is invalid. Please check your search configuration.");
        }

    }

    public NewsReponse getNews() throws NewsAnalyzerException {
        NewsReponse newsReponse = null;
        String jsonResponse = requestData(buildURL());
        if (jsonResponse != null && !jsonResponse.isEmpty()) {

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                newsReponse = objectMapper.readValue(jsonResponse, NewsReponse.class);
                if (!"ok".equals(newsReponse.getStatus())) {
                    throw new NewsAnalyzerException("There is an error with the response. Check your internet connectivity. HTTP Status: " + newsReponse.getStatus());
                }
            } catch (JsonProcessingException e) {
                throw new NewsAnalyzerException("The JSON could not be processed. Please check the response.");
            }
        }
        return newsReponse;
    }
}