package ru.megaplan.jira.plugins.history.search;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.util.lang.Pair;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/21/12
 * Time: 10:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistorySearchRequestImpl implements HistorySearchRequest {

    private static final Logger log = Logger.getLogger(HistorySearchRequestImpl.class);

    private Iterable<ChangeHistoryItem> changeHistoryItems;
    private final Issue issue;

    private Set<String> whos;
    private Set<String> whats;
    private Set<String> froms;
    private Set<String> tos;
    private Pair<Date, Date> when;

    private ChangeHistoryManager changeHistoryManager;

    private HistorySearchRequestImpl(Issue issue) {
        this.issue = issue;
    }
    private HistorySearchRequestImpl() {
        issue = null;
    }

    private void init() {
        whos = new HashSet<String>();
        whats = new HashSet<String>();
        froms = new HashSet<String>();
        tos = new HashSet<String>();
        when = null;
        changeHistoryItems = changeHistoryManager.getAllChangeItems(issue);
    }

    @Override
    public HistorySearchRequest setWho(final String user) {
        if (user == null) throw new IllegalArgumentException("user cannot be null");
        whos.add(user);
        return this;
    }

    @Override
    public HistorySearchRequest setWhat(final String field) {
        if (!StringUtils.isNotBlank(field)) throw new IllegalArgumentException("passed fieldname : " + field + " is not valid");
        whats.add(field);
        return this;
    }

    @Override
    public HistorySearchRequest setFrom(String value) {
        if (!StringUtils.isNotBlank(value)) throw new IllegalArgumentException("passed value : " + value + " is not valid");
        froms.add(value);
        return this;
    }

    @Override
    public HistorySearchRequest setTo(String value) {
        if (!StringUtils.isNotBlank(value)) throw new IllegalArgumentException("passed value : " + value + " is not valid");
        tos.add(value);
        return this;
    }

    @Override
    public HistorySearchRequest setWhen(Date from, Date to) {
        if (from == null || to == null) throw new IllegalArgumentException("each date must be not null");
        if (from.after(to)) throw new IllegalArgumentException("from must be before to");
        when = Pair.of(from,to);
        return this;
    }

    @Override
    public List<ChangeHistoryItem> find() {
        List<ChangeHistoryItem> result = Lists.newArrayList(changeHistoryItems);
        Iterator<ChangeHistoryItem> iterator = result.iterator();
        if (when != null) {
            while (iterator.hasNext()) {
                Date created = iterator.next().getCreated();
                if (created.before(when.first()) || created.after(when.second()))
                    iterator.remove();
            }
            iterator = result.iterator();
        }
        if (whos.size() != 0) {
            while (iterator.hasNext()) {
                ChangeHistoryItem item = iterator.next();
                if (!whos.contains(item.getUser())) iterator.remove();
            }
            iterator = result.iterator();
        }
        if (whats.size() != 0) {
            while (iterator.hasNext()) {
                ChangeHistoryItem item = iterator.next();
                if (!whats.contains(item.getField())) iterator.remove();
            }
        }
        if (froms.size() != 0) {
            while (iterator.hasNext()) {
                ChangeHistoryItem item = iterator.next();
                if (!CollectionUtils.containsAny(froms, item.getFroms().values()))
                    iterator.remove();
            }
            iterator = result.iterator();
        }
        if (tos.size() != 0) {
            while (iterator.hasNext()) {
                ChangeHistoryItem item = iterator.next();
                if (!CollectionUtils.containsAny(tos, item.getTos().values()))
                    iterator.remove();
            }
            iterator = result.iterator();
        }
        return result;
    }

    @Override
    public void clear() {
        init();
    }


    public static class Builder {
        private HistorySearchRequestImpl historySearchRequest;
        private boolean wasBuiltOnce = false;
        public Builder(Issue issue) {
            this.historySearchRequest = new HistorySearchRequestImpl(issue);

        }
        public void setChangeHistoryManager(ChangeHistoryManager changeHistoryManager) {
            historySearchRequest.changeHistoryManager = changeHistoryManager;
        }
        public HistorySearchRequestImpl build() {
            if (wasBuiltOnce) throw new RuntimeException("can't build two times");
            wasBuiltOnce = true;
            historySearchRequest.init();
            return historySearchRequest;
        }
    }

    private boolean changeHistoryItemNotNull(ChangeHistoryItem changeHistoryItem) {
        if (!changeHistoryItemNotNull(changeHistoryItem)) return false;
        if (changeHistoryItem == null) {
            log.warn("changeHistoryItem is null. this is not supposed");
            return false;
        }
        return true;
    }

}
