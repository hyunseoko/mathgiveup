package hanium.smath.Community.service;

import hanium.smath.Community.dto.CommentRequest;
import hanium.smath.Community.dto.CommentResponse;
import hanium.smath.Community.entity.Comment;
import hanium.smath.Community.entity.Post;
import hanium.smath.Community.repository.CommentRepository;
import hanium.smath.Community.repository.PostRepository;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, MemberRepository memberRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    public CompletableFuture<String> saveComment(CommentRequest request, String loginId) {
        return CompletableFuture.supplyAsync(() -> {
            Member member = memberRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new RuntimeException("Member not found"));

            Post post = postRepository.findById(request.getPostId())
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            Comment comment = Comment.builder()
                    .content(request.getContent())
                    .member(member)
                    .post(post)
                    .createdTime(LocalDateTime.now())
                    .updatedTime(LocalDateTime.now())
                    .build();

            commentRepository.save(comment);
            return comment.getUpdatedTime().toString();
        });
    }

    public CompletableFuture<List<CommentResponse>> getCommentsByMemberId(String loginId) {
        return CompletableFuture.supplyAsync(() -> {
            Member member = memberRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new RuntimeException("Member not found"));

            List<Comment> comments = commentRepository.findByMember(member);

            return comments.stream().map(comment -> CommentResponse.builder()
                    .commentId(comment.getComment_id())
                    .content(comment.getContent())
                    .postId(comment.getPost().getIdPost())
                    .loginId(comment.getMember().getLoginId())
                    .nickname(comment.getMember().getNickname())
                    .createTime(comment.getCreatedTime().toString())
                    .updateTime(comment.getUpdatedTime().toString())
                    .build()).collect(Collectors.toList());
        });
    }

    public CompletableFuture<List<CommentResponse>> getCommentsByPostId(Long postId) {
        return CompletableFuture.supplyAsync(() -> {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            List<Comment> comments = commentRepository.findByPost(post);

            return comments.stream().map(comment -> CommentResponse.builder()
                    .commentId(comment.getComment_id())
                    .content(comment.getContent())
                    .postId(comment.getPost().getIdPost())
                    .loginId(comment.getMember().getLoginId())
                    .nickname(comment.getMember().getNickname())
                    .createTime(comment.getCreatedTime().toString())
                    .updateTime(comment.getUpdatedTime().toString())
                    .build()).collect(Collectors.toList());
        });
    }

    public CompletableFuture<String> updateComment(Long commentId, CommentRequest request, String loginId) {
        return CompletableFuture.supplyAsync(() -> {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new RuntimeException("Comment not found"));

            if (!comment.getMember().getLoginId().equals(loginId)) {
                throw new RuntimeException("You are not authorized to update this comment");
            }

            comment.setContent(request.getContent());
            comment.setUpdatedTime(LocalDateTime.now());
            commentRepository.save(comment);

            return comment.getUpdatedTime().toString();
        });
    }

    public CompletableFuture<Void> deleteComment(Long commentId, String loginId) {
        return CompletableFuture.runAsync(() -> {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new RuntimeException("Comment not found"));

            if (!comment.getMember().getLoginId().equals(loginId)) {
                throw new RuntimeException("You are not authorized to delete this comment");
            }

            commentRepository.delete(comment);
        });
    }
}
