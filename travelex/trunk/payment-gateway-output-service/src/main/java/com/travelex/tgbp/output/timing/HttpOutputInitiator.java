package com.travelex.tgbp.output.timing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

public final class HttpOutputInitiator {

    private static final String URL = "http://localhost:7001/tgbp/startOutput.do";

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        while (true) {
           sendRequest(URL);
           TimeUnit.SECONDS.sleep(10);
        }
    }

    /**
     * Sends request using given HTTP URL string
     */
    private static String sendRequest(final String urlString) throws Exception {
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
        System.out.println("Output initiated, result " + response);
        return response;
    }

}
