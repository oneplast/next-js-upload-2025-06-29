package com.ll.domain.post.post.service;

import com.ll.domain.member.member.entity.Member;
import com.ll.domain.post.post.entity.Post;
import com.ll.domain.post.post.repository.PostRepository;
import com.ll.global.search.PostSearchKeywordTypeV1;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public long count() {
        return postRepository.count();
    }

    public Post write(Member author, String title, String content, boolean published, boolean listed) {
        Post post = Post.builder()
                .author(author)
                .title(title)
                .content(content)
                .published(published)
                .listed(listed)
                .build();

        return postRepository.save(post);
    }

    public List<Post> findAllByOrderByIdDesc() {
        return postRepository.findAllByOrderByIdDesc();
    }

    public Optional<Post> findById(long id) {
        return postRepository.findById(id);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public void modify(Post post, String title, String content, boolean published, boolean listed) {
        post.setTitle(title);
        post.setContent(content);
        post.setPublished(published);
        post.setListed(listed);
    }

    public void flush() {
        postRepository.flush();
    }

    public Optional<Post> findLatest() {
        return this.postRepository.findFirstByOrderByIdDesc();
    }

    public Page<Post> findByListedPaged(boolean listed, int page, int pageSize) {
        return findByListedPaged(listed, null, null, page, pageSize);
    }

    public Page<Post> findByListedPaged(
            boolean listed,
            PostSearchKeywordTypeV1 searchKeywordType,
            String searchKeyword,
            int page,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        return postRepository.findByKw(searchKeywordType, searchKeyword, null, null, listed, pageable);
    }

    public Page<Post> findByAuthorPaged(Member author, int page, int pageSize) {
        return findByAuthorPaged(author, null, null, page, pageSize);
    }

    public Page<Post> findByAuthorPaged(
            Member author,
            PostSearchKeywordTypeV1 searchKeywordType,
            String searchKeyword,
            int page,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        return postRepository.findByKw(searchKeywordType, searchKeyword, author, null, null, pageable);
    }
}
