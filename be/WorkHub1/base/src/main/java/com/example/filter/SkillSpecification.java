package com.example.filter;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic Query Builder for Skill
 */
public class SkillSpecification {

    public static QueryResult buildQuery(FilterExpression filter, String keyword) {
        List<String> conditions = new ArrayList<>();
        Parameters params = new Parameters();

        conditions.add("deleted = false");

        if (keyword != null && !keyword.trim().isEmpty()) {
            conditions.add("(name LIKE :kw_name OR level LIKE :kw_level)");
            params.and("kw_name", "%" + keyword.trim() + "%");
            params.and("kw_level", "%" + keyword.trim() + "%");
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
        String field = criteria.field;
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

    public static Sort buildSort(String sortBy, boolean ascending) {
        String validField = getValidSortField(sortBy);
        return ascending ? Sort.by(validField) : Sort.by(validField, Sort.Direction.Descending);
    }

    private static String getValidSortField(String sortBy) {
        if (sortBy == null) return "createdAt";
        switch (sortBy.toLowerCase()) {
            case "id": return "id";
            case "name": return "name";
            case "level": return "level";
            case "createdat": case "created_at": return "createdAt";
            case "updatedat": case "updated_at": return "updatedAt";
            case "createdby": case "created_by": return "createdBy";
            case "updatedby": case "updated_by": return "updatedBy";
            default: return "createdAt";
        }
    }

    public static class QueryResult {
        public String query;
        public Parameters params;

        public QueryResult(String query, Parameters params) {
            this.query = query;
            this.params = params;
        }
    }
}