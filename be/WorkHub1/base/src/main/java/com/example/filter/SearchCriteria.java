package com.example.filter;

/**
 * SearchCriteria - Represents a single filter condition
 */
public class SearchCriteria {

    public String field;
    public Object value;
    public Operator operator;

    public enum Operator {
        EQUALS,
        NOT_EQUALS,
        CONTAINS,
        STARTS_WITH,
        ENDS_WITH,
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_OR_EQUALS,
        LESS_THAN_OR_EQUALS,
        IN,
        NOT_IN,
        IS_NULL,
        IS_NOT_NULL
    }

    public SearchCriteria() {}

    public SearchCriteria(String field, Object value, Operator operator) {
        this.field = field;
        this.value = value;
        this.operator = operator;
    }

    public static SearchCriteria eq(String field, Object value) {
        return new SearchCriteria(field, value, Operator.EQUALS);
    }

    public static SearchCriteria ne(String field, Object value) {
        return new SearchCriteria(field, value, Operator.NOT_EQUALS);
    }

    public static SearchCriteria contains(String field, Object value) {
        return new SearchCriteria(field, value, Operator.CONTAINS);
    }

    public static SearchCriteria startsWith(String field, Object value) {
        return new SearchCriteria(field, value, Operator.STARTS_WITH);
    }

    public static SearchCriteria endsWith(String field, Object value) {
        return new SearchCriteria(field, value, Operator.ENDS_WITH);
    }

    public static SearchCriteria gt(String field, Object value) {
        return new SearchCriteria(field, value, Operator.GREATER_THAN);
    }

    public static SearchCriteria lt(String field, Object value) {
        return new SearchCriteria(field, value, Operator.LESS_THAN);
    }

    public String getField() { return field; }
    public Object getValue() { return value; }
    public Operator getOperator() { return operator; }
}
