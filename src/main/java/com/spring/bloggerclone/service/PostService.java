package com.spring.bloggerclone.service;

import com.spring.bloggerclone.model.Post;
import com.spring.bloggerclone.model.User;
import com.spring.bloggerclone.repository.PostRepository;
import com.spring.bloggerclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class PostService implements IPostService
{
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public List<Post> showAllPosts()
    {
        return postRepository.findAll();
    }

    @Override
    public Post createPost(Post post)
    {
        post.setPostCreateTime(LocalDateTime.now());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(()-> new UsernameNotFoundException("username not found"));
        post.setUser(user);
        List<Post> usersPosts = user.getPosts();
        usersPosts.add(post);

        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(()-> new UsernameNotFoundException("username not found"));
        User postOwner = postRepository.getById(postId).getUser();
        if(currentUser == postOwner)
        {
            postRepository.deleteById(postId);
        }
        else
            throw new RuntimeException("You can't delete this post!!!");
    }

    @Override
    public Post findByPostId(Long postId)
    {
        return postRepository.getById(postId);
    }

    @Override
    public Post editPost(Long postId, Post post)
    {
        Post postToEdit = postRepository.getById(postId);

        postToEdit.setPostTitle(post.getPostTitle());

        postToEdit.setPostBody(post.getPostBody());

        return postRepository.save(postToEdit);
    }
}
