package newsapi.downloader;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SequentialDownloader extends Downloader {

    @Override
    public int process(List<String> urls) {
        long startTime = System.nanoTime();
        int count = 0;
        for (String url : urls) {
            String fileName = saveUrl2File(url);
            if (fileName != null)
                count++;
        }

        System.out.println("Sequential: " + TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) + " seconds");
        return count;
    }
}
