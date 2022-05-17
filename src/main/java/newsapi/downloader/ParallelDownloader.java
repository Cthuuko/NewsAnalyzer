package newsapi.downloader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ParallelDownloader extends Downloader {

    private final ExecutorService executorService = Executors.newFixedThreadPool(16);
    private final List<Future<Integer>> list = new ArrayList<>();

    @Override
    public int process(List<String> urls) {
        long startTime = System.nanoTime();

        int count = 0;
        for (String url : urls) {
            list.add(executorService.submit(() -> saveUrl2File(url) != null ? 1 : 0));
        }

        try {
            for (Future<Integer> f : list) {
                count += f.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Parallel: " + TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) + " ms");
        return count;
    }
}
