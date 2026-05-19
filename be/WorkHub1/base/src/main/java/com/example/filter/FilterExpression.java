package com.example.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter expression representing a group of conditions
 */
public class FilterExpression {

    public LogicalOperator logicalOperator = LogicalOperator.AND;
    public List<FilterExpression> expressions = new ArrayList<>();
    public List<SearchCriteria> criteria = new ArrayList<>();

    public enum LogicalOperator {
        AND, OR
    }

    public FilterExpression() {}

    public FilterExpression(LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public void add(SearchCriteria criteria) {
        this.criteria.add(criteria);
    }

    public void addGroup(FilterExpression group) {
        this.expressions.add(group);
    }

    public boolean isEmpty() {
        return criteria.isEmpty() && expressions.isEmpty();
    }

    public static FilterExpression and() {
        return new FilterExpression(LogicalOperator.AND);
    }

    public static FilterExpression or() {
        return new FilterExpression(LogicalOperator.OR);
    }
}
