package com.example.service;

import com.example.constant.ErrorCode;
import com.example.domain.dto.request.SkillCreateRequest;
import com.example.domain.dto.request.SkillUpdateRequest;
import com.example.domain.dto.response.SkillResponse;
import com.example.domain.entity.Skill;
import com.example.exception.AppException;
import com.example.filter.FilterParser;
import com.example.filter.SkillSpecification;
import com.example.mapper.SkillMapper;
import com.example.pagination.PageRequest;
import com.example.pagination.PageResponse;
import com.example.pagination.PagingMeta;
import com.example.repository.SkillRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class SkillService {

    private static final Logger log = Logger.getLogger(SkillService.class);

    @Inject
    SkillRepository skillRepository;

    @Inject
    SkillMapper skillMapper;

    @Inject
    FilterParser filterParser;

    @Transactional
    public SkillResponse create(SkillCreateRequest request) {
        log.info("Creating skill: " + request.name);

        if (skillRepository.existsByName(request.name)) {
            throw new AppException(ErrorCode.SKILL_ALREADY_EXISTS.code,
                    ErrorCode.SKILL_ALREADY_EXISTS.message);
        }

        Skill skill = skillMapper.toEntity(request);
        skillRepository.persist(skill);

        log.info("Skill created with ID: " + skill.id);
        return skillMapper.toDto(skill);
    }

    public SkillResponse getById(Long id) {
        Skill skill = skillRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_FOUND.code,
                        ErrorCode.SKILL_NOT_FOUND.message));
        return skillMapper.toDto(skill);
    }

    @Transactional
    public SkillResponse update(Long id, SkillUpdateRequest request) {
        log.info("Updating skill: " + id);

        Skill skill = skillRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_FOUND.code,
                        ErrorCode.SKILL_NOT_FOUND.message));

        if (request.name != null && !request.name.equals(skill.name)) {
            if (skillRepository.existsByNameAndNotId(request.name, id)) {
                throw new AppException(ErrorCode.SKILL_ALREADY_EXISTS.code,
                        ErrorCode.SKILL_ALREADY_EXISTS.message);
            }
        }

        skillMapper.updateEntity(skill, request);
        skillRepository.persist(skill);

        log.info("Skill updated: " + id);
        return skillMapper.toDto(skill);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting skill: " + id);

        Skill skill = skillRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_FOUND.code,
                        ErrorCode.SKILL_NOT_FOUND.message));

        skill.softDelete();
        skillRepository.persist(skill);

        log.info("Skill soft deleted: " + id);
    }

    public PageResponse<SkillResponse> getAll(PageRequest pageRequest) {
        log.info("Getting skills - page: " + pageRequest.getPage() + ", size: " + pageRequest.getSize());

        var filter = filterParser.parse(pageRequest.getFilter());
        var queryResult = SkillSpecification.buildQuery(filter, pageRequest.getKeyword());
        Sort sort = SkillSpecification.buildSort(pageRequest.getSortBy(), pageRequest.isAscending());

        int offset = pageRequest.getOffset();
        int limit = pageRequest.getSize();

        List<Skill> skills = skillRepository.findWithFilter(
                queryResult.query, queryResult.params, sort, offset, limit);
        long total = skillRepository.countWithFilter(queryResult.query, queryResult.params);

        PagingMeta meta = new PagingMeta(
                total,
                (int) Math.ceil((double) total / pageRequest.getSize()),
                pageRequest.getPage(),
                pageRequest.getSize(),
                pageRequest.getSortBy(),
                pageRequest.getDirection()
        );

        List<SkillResponse> items = skillMapper.toDtoList(skills);
        return PageResponse.of(meta, items);
    }
}