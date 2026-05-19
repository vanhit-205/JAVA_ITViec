package com.example.service;

import com.example.constant.ErrorCode;
import com.example.domain.dto.request.SubscriberCreateRequest;
import com.example.domain.dto.request.SubscriberUpdateRequest;
import com.example.domain.dto.response.SubscriberResponse;
import com.example.domain.entity.Skill;
import com.example.domain.entity.Subscriber;
import com.example.exception.AppException;
import com.example.filter.FilterParser;
import com.example.filter.SubscriberSpecification;
import com.example.mapper.SubscriberMapper;
import com.example.pagination.PageRequest;
import com.example.pagination.PageResponse;
import com.example.pagination.PagingMeta;
import com.example.repository.SkillRepository;
import com.example.repository.SubscriberRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SubscriberService {

    private static final Logger log = Logger.getLogger(SubscriberService.class);

    @Inject
    SubscriberRepository subscriberRepository;

    @Inject
    SkillRepository skillRepository;

    @Inject
    SubscriberMapper subscriberMapper;

    @Inject
    FilterParser filterParser;

    @Transactional
    public SubscriberResponse create(SubscriberCreateRequest request, Long currentUserId) {
        log.info("Creating subscriber: " + request.email);

        // Validate email uniqueness
        if (subscriberRepository.existsByEmail(request.email)) {
            throw new AppException(ErrorCode.SUBSCRIBER_EMAIL_EXISTS.code, ErrorCode.SUBSCRIBER_EMAIL_EXISTS.message);
        }

        // Validate skills exist
        List<Skill> skills = new ArrayList<>();
        if (request.skillIds != null && !request.skillIds.isEmpty()) {
            skills = skillRepository.find("id IN ?1 AND deleted = false", request.skillIds).list();
            if (skills.size() != request.skillIds.size()) {
                throw new AppException(ErrorCode.SKILL_NOT_FOUND_FOR_SUBSCRIBER.code,
                        ErrorCode.SKILL_NOT_FOUND_FOR_SUBSCRIBER.message);
            }
        }

        // Create subscriber
        Subscriber subscriber = subscriberMapper.toEntity(request);
        subscriber.skills = skills;
        subscriber.deleted = false;
        subscriber.enabled = true;
        subscriber.createdBy = currentUserId;
        subscriber.updatedBy = currentUserId;

        subscriberRepository.persist(subscriber);
        subscriberRepository.flush();

        // Reload with skills
        subscriber = subscriberRepository.findById(subscriber.id);

        log.info("Subscriber created with ID: " + subscriber.id);
        return subscriberMapper.toDto(subscriber);
    }

    public SubscriberResponse getById(Long id) {
        Subscriber subscriber = subscriberRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIBER_NOT_FOUND.code, ErrorCode.SUBSCRIBER_NOT_FOUND.message));
        return subscriberMapper.toDto(subscriber);
    }

    @Transactional
    public SubscriberResponse update(Long id, SubscriberUpdateRequest request, Long currentUserId) {
        log.info("Updating subscriber: " + id);

        Subscriber subscriber = subscriberRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIBER_NOT_FOUND.code, ErrorCode.SUBSCRIBER_NOT_FOUND.message));

        // Validate email uniqueness
        if (request.email != null && !request.email.equals(subscriber.email)) {
            if (subscriberRepository.existsByEmailAndNotId(request.email, id)) {
                throw new AppException(ErrorCode.SUBSCRIBER_EMAIL_EXISTS.code, ErrorCode.SUBSCRIBER_EMAIL_EXISTS.message);
            }
        }

        // Update skills if provided
        if (request.skillIds != null) {
            List<Skill> skills = skillRepository.find("id IN ?1 AND deleted = false", request.skillIds).list();
            if (skills.size() != request.skillIds.size()) {
                throw new AppException(ErrorCode.SKILL_NOT_FOUND_FOR_SUBSCRIBER.code,
                        ErrorCode.SKILL_NOT_FOUND_FOR_SUBSCRIBER.message);
            }
            subscriber.skills = skills;
        }

        // Update fields
        subscriberMapper.updateEntity(subscriber, request);
        subscriber.updatedBy = currentUserId;
        subscriberRepository.persist(subscriber);

        log.info("Subscriber updated: " + id);
        return subscriberMapper.toDto(subscriber);
    }

    @Transactional
    public void delete(Long id, Long currentUserId) {
        log.info("Deleting subscriber: " + id);

        Subscriber subscriber = subscriberRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIBER_NOT_FOUND.code, ErrorCode.SUBSCRIBER_NOT_FOUND.message));

        // Soft delete
        subscriber.softDelete();
        subscriber.updatedBy = currentUserId;
        subscriberRepository.persist(subscriber);

        log.info("Subscriber soft deleted: " + id);
    }

    @Transactional
    public SubscriberResponse enable(Long id, Long currentUserId) {
        log.info("Enabling subscriber: " + id);

        Subscriber subscriber = subscriberRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIBER_NOT_FOUND.code, ErrorCode.SUBSCRIBER_NOT_FOUND.message));

        subscriber.enable();
        subscriber.updatedBy = currentUserId;
        subscriberRepository.persist(subscriber);

        log.info("Subscriber enabled: " + id);
        return subscriberMapper.toDto(subscriber);
    }

    @Transactional
    public SubscriberResponse disable(Long id, Long currentUserId) {
        log.info("Disabling subscriber: " + id);

        Subscriber subscriber = subscriberRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIBER_NOT_FOUND.code, ErrorCode.SUBSCRIBER_NOT_FOUND.message));

        subscriber.disable();
        subscriber.updatedBy = currentUserId;
        subscriberRepository.persist(subscriber);

        log.info("Subscriber disabled: " + id);
        return subscriberMapper.toDto(subscriber);
    }

    @Transactional
    public void updateLastSent(Long id) {
        Subscriber subscriber = subscriberRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIBER_NOT_FOUND.code, ErrorCode.SUBSCRIBER_NOT_FOUND.message));

        subscriber.updateLastSent();
        subscriberRepository.persist(subscriber);
    }

    public PageResponse<SubscriberResponse> getAll(PageRequest pageRequest) {
        log.info("Getting subscribers - page: " + pageRequest.getPage() + ", size: " + pageRequest.getSize());

        var filter = filterParser.parse(pageRequest.getFilter());
        var queryResult = SubscriberSpecification.buildQuery(filter, pageRequest.getKeyword());
        Sort sort = SubscriberSpecification.buildSort(pageRequest.getSortBy(), pageRequest.isAscending());

        int offset = pageRequest.getOffset();
        int limit = pageRequest.getSize();

        List<Subscriber> subscribers = subscriberRepository.findWithFilter(
                queryResult.query, queryResult.params, sort, offset, limit);
        long total = subscriberRepository.countWithFilter(queryResult.query, queryResult.params);

        PagingMeta meta = new PagingMeta(
                total,
                (int) Math.ceil((double) total / pageRequest.getSize()),
                pageRequest.getPage(),
                pageRequest.getSize(),
                pageRequest.getSortBy(),
                pageRequest.getDirection()
        );

        List<SubscriberResponse> items = subscriberMapper.toDtoList(subscribers);
        return PageResponse.of(meta, items);
    }

    public List<Subscriber> getAllActiveEnabled() {
        return subscriberRepository.findAllEnabled(Sort.descending("createdAt"));
    }
}
