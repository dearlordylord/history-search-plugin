package ru.megaplan.jira.plugins.history.search;

import com.atlassian.core.util.map.EasyMap;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.plugin.util.collect.Function;
import com.opensymphony.workflow.loader.StepDescriptor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/21/12
 * Time: 10:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistorySearchManagerImpl implements HistorySearchManager {

    private final ChangeHistoryManager changeHistoryManager;

    HistorySearchManagerImpl(ChangeHistoryManager changeHistoryManager) {
        this.changeHistoryManager = changeHistoryManager;
    }

    @Override
    public HistorySearchRequest getSearchRequest(Issue issue) {
        HistorySearchRequestImpl.Builder builder = new HistorySearchRequestImpl.Builder(issue);
        builder.setChangeHistoryManager(changeHistoryManager);
        return builder.build();
    }

    @Override
    public Function<ChangeLogRequest, String> getFindInChangeLogFunction() {
        Function<ChangeLogRequest, String> findChangeLog = new Function<ChangeLogRequest, String>() {

            private final String CHANGEITEM = "ChangeItem";
            private final String OLDSTRING = "oldstring";
            private final String NEWSTRING = "newstring";
            private final String OLDVALUE = "oldvalue";
            private final String NEWVALUE = "newvalue";
            private final String FIELD = "field";
            private final String ID = "id";
            private final String GROUP = "group";

            private final Logger logger = Logger.getLogger(this.getClass());

            @Override
            public String get(ChangeLogRequest changeLogRequest) {
                Logger log = changeLogRequest.getLog()!=null?changeLogRequest.getLog():logger;
                String result = null;
                List<GenericValue> changeItems = safeGetChangeItems(changeLogRequest.getChangeLog(), log);
                if (changeItems == null) return null;
                Iterator<GenericValue> it = changeItems.iterator();
                while(it.hasNext()){
                    GenericValue changeGv = it.next();
                    String p = changeGv.getString(FIELD);
                    if(p.equalsIgnoreCase(changeLogRequest.getFieldName())) {
                        String oldString;
                        String newString;
                        if (changeLogRequest.isKey()) {
                            oldString = changeGv.getString(OLDVALUE);
                            newString = changeGv.getString(NEWVALUE);
                        } else {
                            oldString = changeGv.getString(OLDSTRING);
                            newString = changeGv.getString(NEWSTRING);
                        }

                        if (StringUtils.isEmpty(oldString)) result = newString;
                        else {
                            if (oldString.equals(newString)) {
                                result = null;
                            }
                            else {
                                if (changeLogRequest.isOld())
                                    result = oldString;
                                else result = newString;
                            }
                        }
                        break;
                    }
                }
                return result;
            }
            private List<GenericValue> safeGetChangeItems(GenericValue changeLog, Logger log) {
                if (changeLog == null) return null;
                List<GenericValue> changeItems = null;
                try {
                    changeItems = changeLog.internalDelegator.findByAnd(CHANGEITEM, EasyMap.build(GROUP, changeLog.get(ID)));
                } catch (GenericEntityException e) {
                    log.error("some generic exception in genericvalue",e);
                    return null;
                }
                return changeItems;
            }
        };
        return findChangeLog;
    }



}
