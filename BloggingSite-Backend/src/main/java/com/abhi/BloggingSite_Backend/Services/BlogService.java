package com.abhi.BloggingSite_Backend.Services;

import com.abhi.BloggingSite_Backend.Model.Blog;
import com.abhi.BloggingSite_Backend.Repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    public List<Blog> getAllBlogsByUser(Long userId) {
        return blogRepository.findByUserId(userId);
    }
    public Blog createBlog(Blog blog) {
        return blogRepository.save(blog);
    }
    public void deleteBlog(Long blogId) {
        blogRepository.deleteById(blogId);
    }

    public Blog updateBlog(Long blogId, Blog blogDetails) {
        Optional<Blog> existingBlog = blogRepository.findById(blogId);

        if (existingBlog.isPresent()) {
            Blog blog = existingBlog.get();
            blog.setBlogName(blogDetails.getBlogName());
            blog.setBody(blogDetails.getBody());
            return blogRepository.save(blog);
        }

        return null;
    }

    public Map<String, Integer> getTop5FrequentWords(Long userId) {
        List<Blog> blogs = blogRepository.findByUserId(userId);
        Map<String, Integer> wordCount = new HashMap<>();

        // Count frequencies of each word
        for (Blog blog : blogs) {
            String[] words = blog.getBody().split("\\s+");
            for (String word : words) {
                word = word.toLowerCase().replaceAll("[^a-zA-Z]", "");
                if (!word.isEmpty()) {
                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                }
            }
        }

        // Sort by frequency and return top 5 entries
        return wordCount.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // Sort by value (frequency) in descending order
                .limit(5) // Get top 5
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

}
