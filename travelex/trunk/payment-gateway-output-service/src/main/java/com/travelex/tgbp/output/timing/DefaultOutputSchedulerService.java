package com.travelex.tgbp.output.timing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osoa.sca.annotations.Property;
import org.sca4j.api.annotation.Resource;
import org.sca4j.api.annotation.scope.Composite;
import org.sca4j.timer.spi.TimerService;

/**
 * Default implementation for {@link OutputSchedulerService}
 */
@Composite
public class DefaultOutputSchedulerService implements OutputSchedulerService {

    @Resource(mappedName = "TransactionalTimerService") protected TimerService trxTimerService;

    @Property(required = true) protected long initialStartupDelayInSeconds;
    @Property(required = true) protected long outputFrequencyInSeconds;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private static final String OUTPUT_TRIGGER_HTTP_URL = "http://localhost:7001/tgbp/startOutput.do";

    /**
     * {@inheritDoc}
     */
    @Override
    public String startOutputScheduler() {
        if (!isRunning.get()) {
            trxTimerService.scheduleWithFixedDelay(
                    getOutputJob()
                    , 0
                    , TimeUnit.MILLISECONDS.convert(outputFrequencyInSeconds, TimeUnit.SECONDS)
                    , TimeUnit.MILLISECONDS);
            isRunning.set(true);
            return "Success";
        } else {
            return "Already scheduled";
        }
    }

    /*
     * Prepare output process job
     */
    private Runnable getOutputJob() {

        return new Runnable() {

            public void run() {
                try {
                    System.out.println("Output process triggered, result " + sendRequest(OUTPUT_TRIGGER_HTTP_URL));
                } catch (Throwable throwable) {
                    System.out.println("Problem in output trigger");
                    throwable.printStackTrace();
                }
            }

            private String sendRequest(final String urlString) throws Exception {
                final URL url = new URL(urlString);
                final URLConnection urlConnection = url.openConnection();

                final InputStream inputStream = urlConnection.getInputStream();
                final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                final StringBuilder stringBuilder = new StringBuilder();
                String nextLine = bufferedReader.readLine();
                while (nextLine != null) {
                    stringBuilder.append(nextLine);
                    nextLine = bufferedReader.readLine();
                }

                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();

                final String response = stringBuilder.toString();
                return response;
            }
        };
    }

}
