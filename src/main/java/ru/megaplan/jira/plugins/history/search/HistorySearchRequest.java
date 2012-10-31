package ru.megaplan.jira.plugins.history.search;

import com.atlassian.jira.issue.changehistory.ChangeHistoryItem;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/21/12
 * Time: 10:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HistorySearchRequest {
    HistorySearchRequest setWho(String user);
    HistorySearchRequest setWhat(String field);
    HistorySearchRequest setFrom(String value);
    HistorySearchRequest setTo(String value);
    HistorySearchRequest setWhen(Date from, Date to);
    List<ChangeHistoryItem> find();
    void clear();
}
