package com.abhi.BloggingSite_Backend.Controller;

import com.abhi.BloggingSite_Backend.Model.Blog;
import com.abhi.BloggingSite_Backend.Model.Users;
import com.abhi.BloggingSite_Backend.Repository.BlogRepository;
import com.abhi.BloggingSite_Backend.Repository.UserRepository;
import com.abhi.BloggingSite_Backend.Services.BlogService;
import com.abhi.BloggingSite_Backend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin(origins = "http://localhost:3000")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<List<Blog>> getAllBlogsByCurrentUser(@RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);

        Optional<Users> user = Optional.ofNullable(userRepository.findByUsername(username));
        if (user.isPresent()) {
            List<Blog> blogs = blogService.getAllBlogsByUser(user.get().getId());
            return new ResponseEntity<>(blogs, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if user not found
    }

    @PostMapping
    public ResponseEntity<Blog> createBlog(@RequestBody Blog blog, @RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);

        Optional<Users> user = Optional.ofNullable(userRepository.findByUsername(username));

        if (user.isPresent()) {
            blog.setUser(user.get());
            Blog savedBlog = blogService.createBlog(blog);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBlog);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{blogId}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long blogId, @RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);

        Optional<Users> user = Optional.ofNullable(userRepository.findByUsername(username));
        if (user.isPresent()) {
            blogService.deleteBlog(blogId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long blogId, @RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);

        Optional<Users> user = Optional.ofNullable(userRepository.findByUsername(username));
        if (user.isPresent()) {
            Optional<Blog> blog = blogRepository.findById(blogId);

            if (blog.isPresent()) {
                return new ResponseEntity<>(blog.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/{blogId}")
    public ResponseEntity<Blog> updateBlog(@PathVariable Long blogId, @RequestBody Blog blogDetails, @RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);

        Optional<Users> user = Optional.ofNullable(userRepository.findByUsername(username));
        if (user.isPresent()) {
            // Attempt to update the blog
            Blog updatedBlog = blogService.updateBlog(blogId, blogDetails);

            // If the blog doesn't exist, return 404 Not Found
            if (updatedBlog == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.status(HttpStatus.OK).body(updatedBlog);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/report/{userId}")
    public ResponseEntity<Map<String, Integer>> getTop5FrequentWords(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        try {
            String username = extractUsernameFromToken(token);

            Users user = userRepository.findByUsername(username);

            if (user != null) {
                Map<String, Integer> frequentWords = blogService.getTop5FrequentWords(userId);
                return new ResponseEntity<>(frequentWords, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    private String extractUsernameFromToken(String token) {
        String jwtToken = token.replace("Bearer ", "").trim();  // Remove 'Bearer ' part
        return userService.extractUsername(jwtToken);  // Extract username using userService
    }

}
