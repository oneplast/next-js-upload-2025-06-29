package com.ll.domain.member.member.repository;

import static com.ll.domain.member.member.entity.QMember.member;

import com.ll.domain.member.member.entity.Member;
import com.ll.global.search.MemberSearchKeywordTypeV1;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Member> findByKw(MemberSearchKeywordTypeV1 kwType, String kw, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (kw != null && !kw.isBlank()) {
            applyKeywordFilter(kwType, kw, builder);
        }

        JPAQuery<Member> membersQuery = createMembersQuery(builder);
        applySorting(pageable, membersQuery);

        membersQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());

        JPAQuery<Long> totalQuery = createTotalQuery(builder);

        return PageableExecutionUtils.getPage(membersQuery.fetch(), pageable, totalQuery::fetchOne);
    }

    private void applyKeywordFilter(MemberSearchKeywordTypeV1 kwType, String kw, BooleanBuilder builder) {
        switch (kwType) {
            case username -> builder.and(member.username.containsIgnoreCase(kw));
            case nickname -> builder.and(member.nickname.containsIgnoreCase(kw));
            default -> builder.and(
                    member.username.containsIgnoreCase(kw)
                            .or(member.nickname.containsIgnoreCase(kw))
            );
        }
    }

    private JPAQuery<Member> createMembersQuery(BooleanBuilder builder) {
        return jpaQueryFactory
                .select(member)
                .from(member)
                .where(builder);
    }

    private void applySorting(Pageable pageable, JPAQuery<Member> membersQuery) {
        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(member.getType(), member.getMetadata());
            membersQuery.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(o.getProperty())));
        }
    }

    private JPAQuery<Long> createTotalQuery(BooleanBuilder builder) {
        return jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(builder);
    }
}
