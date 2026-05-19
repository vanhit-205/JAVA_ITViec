package com.example.filter;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter Parser - Converts filter string to FilterExpression
 *
 * Supported syntax:
 * - name:*fpt*       -> name contains fpt
 * - name:fpt*        -> name starts with fpt
 * - name:*fpt        -> name ends with fpt
 * - name:fpt         -> name equals fpt
 * - salary>1000      -> salary > 1000
 * - (name:*fpt*|name:*google*) -> OR group
 */
@ApplicationScoped
public class FilterParser {

    private static final Logger log = Logger.getLogger(FilterParser.class);

    private static final Pattern COMPARISON_PATTERN = Pattern.compile("(\\w+)(>=|<=|>|<)(.+)");
    private static final Pattern FIELD_VALUE_PATTERN = Pattern.compile("(\\w+):(.+)");

    public FilterExpression parse(String filterString) {
        if (filterString == null || filterString.trim().isEmpty()) {
            return FilterExpression.and();
        }

        try {
            FilterExpression root = FilterExpression.and();
            String[] parts = splitFilter(filterString);

            for (String part : parts) {
                part = part.trim();
                if (part.isEmpty()) continue;

                if (part.startsWith("(") && part.endsWith(")")) {
                    FilterExpression group = parseGroup(part.substring(1, part.length() - 1));
                    root.addGroup(group);
                } else {
                    SearchCriteria criteria = parseCondition(part);
                    if (criteria != null) {
                        root.add(criteria);
                    }
                }
            }

            return root;
        } catch (Exception e) {
            log.error("Failed to parse filter: " + filterString, e);
            return FilterExpression.and();
        }
    }

    private FilterExpression parseGroup(String groupString) {
        FilterExpression group = FilterExpression.or();
        String[] parts = groupString.split("\\|");

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;
            SearchCriteria criteria = parseCondition(part);
            if (criteria != null) {
                group.add(criteria);
            }
        }
        return group;
    }

    private SearchCriteria parseCondition(String condition) {
        Matcher comparisonMatcher = COMPARISON_PATTERN.matcher(condition);
        if (comparisonMatcher.matches()) {
            String field = comparisonMatcher.group(1);
            String op = comparisonMatcher.group(2);
            String value = comparisonMatcher.group(3).trim();
            return createComparisonCriteria(field, op, value);
        }

        Matcher fieldValueMatcher = FIELD_VALUE_PATTERN.matcher(condition);
        if (fieldValueMatcher.matches()) {
            String field = fieldValueMatcher.group(1);
            String value = fieldValueMatcher.group(2);
            return createWildcardCriteria(field, value.trim());
        }

        return null;
    }

    private SearchCriteria createComparisonCriteria(String field, String op, String value) {
        switch (op) {
            case ">": return new SearchCriteria(field, parseValue(value), SearchCriteria.Operator.GREATER_THAN);
            case "<": return new SearchCriteria(field, parseValue(value), SearchCriteria.Operator.LESS_THAN);
            case ">=": return new SearchCriteria(field, parseValue(value), SearchCriteria.Operator.GREATER_THAN_OR_EQUALS);
            case "<=": return new SearchCriteria(field, parseValue(value), SearchCriteria.Operator.LESS_THAN_OR_EQUALS);
            default: return new SearchCriteria(field, parseValue(value), SearchCriteria.Operator.EQUALS);
        }
    }

    private SearchCriteria createWildcardCriteria(String field, String value) {
        if (value.startsWith("*") && value.endsWith("*")) {
            return new SearchCriteria(field, value.substring(1, value.length() - 1), SearchCriteria.Operator.CONTAINS);
        } else if (value.endsWith("*")) {
            return new SearchCriteria(field, value.substring(0, value.length() - 1), SearchCriteria.Operator.STARTS_WITH);
        } else if (value.startsWith("*")) {
            return new SearchCriteria(field, value.substring(1), SearchCriteria.Operator.ENDS_WITH);
        } else {
            return new SearchCriteria(field, parseValue(value), SearchCriteria.Operator.EQUALS);
        }
    }

    private Object parseValue(String value) {
        if (value == null) return null;
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }
        try { return Integer.parseInt(value); } catch (NumberFormatException ignored) {}
        try { return Long.parseLong(value); } catch (NumberFormatException ignored) {}
        try { return Double.parseDouble(value); } catch (NumberFormatException ignored) {}
        return value;
    }

    private String[] splitFilter(String filterString) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;

        for (char c : filterString.toCharArray()) {
            if (c == '(') { depth++; current.append(c); }
            else if (c == ')') { depth--; current.append(c); }
            else if (c == ',' && depth == 0) {
                parts.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) parts.add(current.toString());
        return parts.toArray(new String[0]);
    }
}
