package com.travelex.tgbp.query;

import java.io.File;

import org.joda.time.LocalDate;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.store.service.api.DataStore;

/**
 *
 */
public class DefaultSubmissionQueryProcessor implements SubmissionQueryProcessor {

    @Reference protected DataStore dataStore;

    public String getInstructionDataByMsgId(String messageId) {
          return prepareResultXml(dataStore.getInstructionDataByMssgId(messageId));
    }

    private String prepareResultXml(Object[] resultSet) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Data>");
         for (Object object : resultSet) {
             Object[] record = (Object[]) object;
             sb.append("<instruction>");
             sb.append("<curr>");sb.append(String.valueOf(record[0]));sb.append("</curr>");
             sb.append("<amount>");sb.append(String.valueOf(record[1]));sb.append("</amount>");
             sb.append("<vdate>");sb.append(new LocalDate(String.valueOf(record[2])).toString("dd-MMM-yyyy"));sb.append("</vdate>");
             sb.append("<targetacc>");sb.append(String.valueOf(record[3]));sb.append("</targetacc>");
             String status = String.valueOf(record[4]);
             sb.append("<status>");sb.append(status);sb.append("</status>");
             String scheme = String.valueOf(record[5]);
             if(scheme.equals("null")){
            	 scheme = "";
             }
             sb.append("<scheme>");sb.append(scheme);sb.append("</scheme>");
             String fileName = String.valueOf(record[6]);
             if(fileName.equals("null")){
            	 fileName = "";
             }
             sb.append("<fname>");sb.append(fileName);sb.append("</fname>");                          
             String ackMode = status.equals("SENT") && new File("C:/tgbp-outbound-files/" + fileName).exists()  ? "ACK" : "NACK";
             sb.append("<ackmode>");sb.append(ackMode);sb.append("</ackmode>");

             byte[] data = (byte[]) record[7];
             sb.append("<data>");sb.append(data != null ? new String(data) : "");sb.append("</data>");
             sb.append("</instruction>");
         }
        sb.append("</Data>");
        return sb.toString();
    }

}
