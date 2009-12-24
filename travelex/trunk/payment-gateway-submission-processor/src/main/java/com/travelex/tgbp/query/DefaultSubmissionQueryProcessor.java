package com.travelex.tgbp.query;

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
             sb.append("<vdate>");sb.append(String.valueOf(record[2]));sb.append("</vdate>");
             sb.append("<fname>");sb.append(String.valueOf(record[3]));sb.append("</fname>");
             sb.append("</instruction>");
         }
        sb.append("</Data>");
        return sb.toString();
    }

}
