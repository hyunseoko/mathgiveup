package hanium.smath.Community.controller;

import hanium.smath.Community.dto.CommentRequest;
import hanium.smath.Community.dto.CommentResponse;
import hanium.smath.Community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/community/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 작성
    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createComment(@RequestBody CommentRequest request, Authentication authentication) {
        String memberId = authentication.getName(); // JWT에서 인증된 사용자 ID 가져오기

        System.out.println("Creating comment for postId: " + request.getPostId());
        return commentService.saveComment(request, memberId)
                .thenApply(updateTime -> ResponseEntity.ok(updateTime))
                .exceptionally(ex -> {
                    System.err.println("Error in createComment: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }

    // 내 댓글 불러오기
    @GetMapping("/my")
    public CompletableFuture<ResponseEntity<List<CommentResponse>>> getMyComments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName(); // JWT에서 가져온 사용자 ID

        System.out.println("Retrieving comments for memberId: " + memberId);

        return commentService.getCommentsByMemberId(memberId)
                .thenApply(comments -> ResponseEntity.ok(comments))
                .exceptionally(ex -> {
                    System.err.println("Error in getMyComments: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }

    // 해당하는 특정 게시물들 불러오기
    @GetMapping("/post/{postId}")
    public CompletableFuture<ResponseEntity<List<CommentResponse>>> getCommentsByPostId(@PathVariable Long postId) {
        System.out.println("Retrieving comments for postId: " + postId);
        return commentService.getCommentsByPostId(postId)
                .thenApply(comments -> ResponseEntity.ok(comments))
                .exceptionally(ex -> {
                    System.err.println("Error in getCommentsByPostId: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public CompletableFuture<ResponseEntity<String>> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest request, Authentication authentication) {
        String memberId = authentication.getName(); // JWT에서 인증된 사용자 ID 가져오기

        System.out.println("Updating commentId: " + commentId);
        return commentService.updateComment(commentId, request, memberId)
                .thenApply(updateTime -> ResponseEntity.ok(updateTime))
                .exceptionally(ex -> {
                    System.err.println("Error in updateComment: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public CompletableFuture<ResponseEntity<Void>> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        String memberId = authentication.getName(); // JWT에서 인증된 사용자 ID 가져오기

        System.out.println("Deleting commentId: " + commentId);
        return commentService.deleteComment(commentId, memberId)
                .thenApply(v -> ResponseEntity.noContent().<Void>build())
                .exceptionally(ex -> {
                    System.err.println("Error in deleteComment: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }
}
