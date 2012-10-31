package ru.megaplan.jira.plugins.history.search;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/22/12
 * Time: 12:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class HistorySearchTestAction extends JiraWebActionSupport {

    private final HistorySearchManager historySearchManager;
    private final IssueManager issueManager;
    private final UserManager userManager;

    private String issueKey;
    private String[] from;
    private String[] to;
    private String[] what;
    private String whenFrom;
    private String whenTo;
    private String[] who;

    private List<ChangeHistoryItem> changeHistoryItems;

    HistorySearchTestAction(HistorySearchManager historySearchManager, IssueManager issueManager,
                            UserManager userManager) {
        this.historySearchManager = historySearchManager;
        this.issueManager = issueManager;
        this.userManager = userManager;

    }

    @Override
    protected String doExecute() {
        Issue issue = issueManager.getIssueObject(issueKey);
        if (issueKey != null && issue == null) {
            addErrorMessage("issue : " + issueKey + " not found");
            return ERROR;
        } else if (issue==null) {
            return SUCCESS;
        }
        HistorySearchRequest searchRequest = historySearchManager.getSearchRequest(issue);
        if (who != null) {
            for (String whoo : who) {
                if (StringUtils.isNotEmpty(whoo)) {
                    User user = userManager.getUser(whoo);
                    if (user != null) {
                        searchRequest.setWho(whoo);
                    } else {
                        addErrorMessage("user : " + user + " not found");
                    }
                }
            }
        }
        if (from != null) {
            for (String froom : from) {
                if (StringUtils.isNotBlank(froom))
                    searchRequest.setFrom(froom);
            }

        }
        if (to != null) {
            for (String too : to) {
                if (StringUtils.isNotBlank(too))
                    searchRequest.setTo(too);
            }
        }
        if (what != null) {
            for (String whaat : what) {
                if (StringUtils.isNotBlank(whaat))
                    searchRequest.setWhat(whaat);
            }
        }
        changeHistoryItems = searchRequest.find();
        //if (whenFrom != null)
        //
        if (hasAnyErrors()) return ERROR;
        return SUCCESS;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String[] getFrom() {
        return from;
    }

    public void setFrom(String[] from) {
        this.from = from;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String[] getWhat() {
        return what;
    }

    public void setWhat(String[] what) {
        this.what = what;
    }

    public String getWhenFrom() {
        return whenFrom;
    }

    public void setWhenFrom(String whenFrom) {
        this.whenFrom = whenFrom;
    }

    public String getWhenTo() {
        return whenTo;
    }

    public void setWhenTo(String whenTo) {
        this.whenTo = whenTo;
    }

    public String[] getWho() {
        return who;
    }

    public void setWho(String[] who) {
        this.who = who;
    }

    public List<ChangeHistoryItem> getChangeHistoryItems() {
        return changeHistoryItems;
    }

    public void setChangeHistoryItems(List<ChangeHistoryItem> changeHistoryItems) {
        this.changeHistoryItems = changeHistoryItems;
    }
}
