package com.abhi.BloggingSite_Backend.Repository;

import com.abhi.BloggingSite_Backend.Model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
     List<Blog> findByUserId(Long userId);
}
