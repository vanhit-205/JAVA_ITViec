package com.example.filter;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic Query Builder for Subscriber
 */
public class SubscriberSpecification {

    /**
     * Build dynamic query from FilterExpression
     */
    public static QueryResult buildQuery(FilterExpression filter, String keyword) {
        List<String> conditions = new ArrayList<>();
        Parameters params = new Parameters();

        conditions.add("deleted = false");

        if (keyword != null && !keyword.trim().isEmpty()) {
            conditions.add("(name LIKE :kw_name OR email LIKE :kw_email)");
            params.and("kw_name", "%" + keyword.trim() + "%");
            params.and("kw_email", "%" + keyword.trim() + "%");
        }

        if (filter != null && !filter.isEmpty()) {
            buildConditions(filter, conditions, params);
        }

        return new QueryResult(String.join(" AND ", conditions), params);
    }

    private static void buildConditions(FilterExpression filter, List<String> conditions, Parameters params) {
        for (SearchCriteria criteria : filter.criteria) {
            conditions.add(buildSingleCondition(criteria, params));
        }

        for (FilterExpression group : filter.expressions) {
            List<String> groupConditions = new ArrayList<>();
            Parameters groupParams = new Parameters();

            for (SearchCriteria criteria : group.criteria) {
                groupConditions.add(buildSingleCondition(criteria, groupParams));
            }

            if (!groupConditions.isEmpty()) {
                String operator = group.logicalOperator == FilterExpression.LogicalOperator.OR ? " OR " : " AND ";
                conditions.add("(" + String.join(operator, groupConditions) + ")");
            }
        }
    }

    private static String buildSingleCondition(SearchCriteria criteria, Parameters params) {
        String field = mapFieldName(criteria.field);
        long timestamp = System.nanoTime();
        String paramName = field + "_" + timestamp;

        Object value = criteria.value;
        switch (criteria.operator) {
            case EQUALS:
                params.and(paramName, value);
                return field + " = :" + paramName;
            case NOT_EQUALS:
                params.and(paramName, value);
                return field + " != :" + paramName;
            case CONTAINS:
                params.and(paramName, "%" + value + "%");
                return field + " LIKE :" + paramName;
            case STARTS_WITH:
                params.and(paramName, value + "%");
                return field + " LIKE :" + paramName;
            case ENDS_WITH:
                params.and(paramName, "%" + value);
                return field + " LIKE :" + paramName;
            case GREATER_THAN:
                params.and(paramName, value);
                return field + " > :" + paramName;
            case LESS_THAN:
                params.and(paramName, value);
                return field + " < :" + paramName;
            case GREATER_THAN_OR_EQUALS:
                params.and(paramName, value);
                return field + " >= :" + paramName;
            case LESS_THAN_OR_EQUALS:
                params.and(paramName, value);
                return field + " <= :" + paramName;
            default:
                params.and(paramName, value);
                return field + " = :" + paramName;
        }
    }

    private static String mapFieldName(String field) {
        String f = field.toLowerCase();
        if (f.equals("id")) return "id";
        if (f.equals("name")) return "name";
        if (f.equals("email")) return "email";
        if (f.equals("enabled")) return "enabled";
        if (f.equals("deleted")) return "deleted";
        if (f.equals("createdat") || f.equals("created_at")) return "createdAt";
        if (f.equals("updatedat") || f.equals("updated_at")) return "updatedAt";
        if (f.equals("createdby") || f.equals("created_by")) return "createdBy";
        if (f.equals("updatedby") || f.equals("updated_by")) return "updatedBy";
        if (f.equals("lastsentat") || f.equals("last_sent_at")) return "lastSentAt";
        return field;
    }

    public static Sort buildSort(String sortBy, boolean ascending) {
        String validField = getValidSortField(sortBy);
        if (ascending) {
            return Sort.by(validField);
        } else {
            return Sort.by(validField, Sort.Direction.Descending);
        }
    }

    private static String getValidSortField(String sortBy) {
        if (sortBy == null) return "createdAt";
        String s = sortBy.toLowerCase();
        if (s.equals("id")) return "id";
        if (s.equals("name")) return "name";
        if (s.equals("email")) return "email";
        if (s.equals("enabled")) return "enabled";
        if (s.equals("createdat") || s.equals("created_at")) return "createdAt";
        if (s.equals("updatedat") || s.equals("updated_at")) return "updatedAt";
        if (s.equals("lastsentat") || s.equals("last_sent_at")) return "lastSentAt";
        return "createdAt";
    }

    /**
     * Result class containing query and parameters
     */
    public static class QueryResult {
        public String query;
        public Parameters params;

        public QueryResult(String query, Parameters params) {
            this.query = query;
            this.params = params;
        }
    }
}