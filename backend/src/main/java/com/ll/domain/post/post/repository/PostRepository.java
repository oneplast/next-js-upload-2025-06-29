package com.ll.domain.post.post.repository;

import com.ll.domain.post.post.entity.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    List<Post> findAllByOrderByIdDesc();
    Optional<Post> findFirstByOrderByIdDesc();
}
