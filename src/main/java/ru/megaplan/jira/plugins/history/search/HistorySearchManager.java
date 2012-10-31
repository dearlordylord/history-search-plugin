package ru.megaplan.jira.plugins.history.search;

import com.atlassian.jira.issue.Issue;
import com.atlassian.plugin.util.collect.Function;
import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericValue;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/21/12
 * Time: 10:29 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HistorySearchManager {

    HistorySearchRequest getSearchRequest(Issue issue);

    Function<ChangeLogRequest, String> getFindInChangeLogFunction();

    public static class ChangeLogRequest {

        public final static boolean ISKEYDEFAULT = false;
        public final static boolean ISOLDDEFAULT = true;

        GenericValue changeLog;
        String fieldName;
        boolean isOld;
        boolean isKey;
        private Logger log;

        public ChangeLogRequest(GenericValue changeLog, String fieldName) {
            this.changeLog = changeLog;
            this.fieldName = fieldName;
            this.isOld = ISOLDDEFAULT;
            this.isKey = ISKEYDEFAULT;
        }

        public ChangeLogRequest(GenericValue changeLog, String fieldName, boolean old) {
            this.changeLog = changeLog;
            this.fieldName = fieldName;
            isOld = old;
            this.isKey = ISKEYDEFAULT;
        }

        public ChangeLogRequest(GenericValue changeLog, String fieldName, boolean old, boolean isKey) {
            this.changeLog = changeLog;
            this.fieldName = fieldName;
            isOld = old;
            this.isKey = isKey;
        }

        public GenericValue getChangeLog() {
            return changeLog;
        }

        public void setChangeLog(GenericValue changeLog) {
            this.changeLog = changeLog;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public boolean isOld() {
            return isOld;
        }

        public void setOld(boolean old) {
            isOld = old;
        }

        public boolean isKey() {
            return isKey;
        }

        public void setKey(boolean key) {
            isKey = key;
        }

        public Logger getLog() {
            return log;
        }

        public void setLog(Logger log) {
            this.log = log;
        }



    }

}
