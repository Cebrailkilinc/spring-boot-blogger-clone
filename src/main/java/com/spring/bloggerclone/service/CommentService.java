package com.spring.bloggerclone.service;

import com.spring.bloggerclone.model.Comment;
import com.spring.bloggerclone.model.Post;
import com.spring.bloggerclone.model.User;
import com.spring.bloggerclone.repository.CommentRepository;
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
public class CommentService implements ICommentService
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    protected User getCurrentUser()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(()-> new UsernameNotFoundException("username not found"));
    }

    @Override
    public Comment createComment(Comment comment, Long postId)
    {
        User currentUser = getCurrentUser();
        comment.setCommentCreateTime(LocalDateTime.now());
        comment.setUser(currentUser);
        List<Comment> usersComments = currentUser.getComments();
        usersComments.add(comment);
        Post post = postRepository.getById(postId);
        comment.setPost(post);
        List<Comment> postsComments = post.getComments();
        postsComments.add(comment);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId)
    {
        User currentUser = getCurrentUser();
        Comment currentComment = commentRepository.getById(commentId);
        Post commentedPost = currentComment.getPost();
        User postOwner = commentedPost.getUser();
        User commentOwner = commentRepository.getById(commentId).getUser();
        if(currentUser == postOwner || currentUser == commentOwner)
        {
            commentRepository.deleteById(commentId);
        }
        else
            throw new RuntimeException("You can't delete this comment!!!");
    }

    @Override
    public Comment updateComment(Long commentId, Comment comment)
    {
        User currentUser = getCurrentUser();
        Comment commentToEdit = commentRepository.getById(commentId);
        User commentOwner = commentToEdit.getUser();
        if(currentUser == commentOwner)
        {
            commentToEdit.setCommentBody(comment.getCommentBody());
            return commentToEdit;
        }
        else
            throw new RuntimeException("You can't update this comment!!!");
    }
}
