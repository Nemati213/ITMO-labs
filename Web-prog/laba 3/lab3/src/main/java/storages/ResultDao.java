package storages;

import jakarta.servlet.ServletContext;
import models.ResultRecord;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResultDao {

    private static final String HISTORY_ATTRIBUTE = "historyList";

    public void addResult(ServletContext context, ResultRecord record) {
        List<ResultRecord> history = getHistoryList(context);
        history.add(record);
    }

    public List<ResultRecord> getHistoryList(ServletContext context) {
        Object historyObject = context.getAttribute(HISTORY_ATTRIBUTE);

        if (historyObject == null) {
            synchronized (context) {
                historyObject = context.getAttribute(HISTORY_ATTRIBUTE);
                if (historyObject == null) {
                    historyObject = new CopyOnWriteArrayList<ResultRecord>();
                    context.setAttribute(HISTORY_ATTRIBUTE, historyObject);
                }
            }
        }

        return (List<ResultRecord>) historyObject;
    }
}