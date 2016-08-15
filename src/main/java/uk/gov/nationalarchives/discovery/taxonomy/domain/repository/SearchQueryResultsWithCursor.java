package uk.gov.nationalarchives.discovery.taxonomy.domain.repository;

import java.util.List;

/**
 * Created by jcharlet on 8/15/16.
 */
public class SearchQueryResultsWithCursor {
    private List<String> results;
    private String cursor;

    public SearchQueryResultsWithCursor(List<String> results, String cursor) {
        this.results = results;
        this.cursor = cursor;
    }

    public List<String> getResults() {
        return results;
    }

    public String getCursor() {
        return cursor;
    }
}
