package hanium.smath.Community.controller;

import hanium.smath.Community.dto.PostRequest;
import hanium.smath.Community.dto.PostResponse;
import hanium.smath.Community.entity.Post;
import hanium.smath.Community.service.PostService;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.MemberRepository;
import hanium.smath.Member.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/community/posts")
public class PostController {

    private final PostService postService;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public PostController(PostService postService, MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.postService = postService;
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    // 게시물 작성
    @PostMapping
    public CompletableFuture<ResponseEntity<PostResponse>> createPost(@RequestBody PostRequest postRequest, Authentication authentication) {
        String loginId = authentication.getName();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .member(member)
                .build();

        return postService.savePost(post)
                .thenApply(savedPost -> {
                    PostResponse response = PostResponse.builder()
                            .id(savedPost.getIdPost())
                            .title(savedPost.getTitle())
                            .content(savedPost.getContent())
                            .build();

                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> ResponseEntity.status(500).build());
    }

    // 전체 게시물 불러오기
    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<List<PostResponse>>> getAllPosts() {
        return postService.getAllPosts()
                .thenApply(posts -> {
                    System.out.println("Total posts retrieved: " + posts.size());
                    return ResponseEntity.ok(posts);
                })
                .exceptionally(ex -> {
                    System.err.println("Error in getAllPosts: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }

    // 내 게시물 불러오기
    @GetMapping("/my")
    public CompletableFuture<ResponseEntity<List<PostResponse>>> getMyPosts(@AuthenticationPrincipal UserDetails userDetails) {
        String loginId = userDetails.getUsername();
        System.out.println("Retrieving posts for loginId: " + loginId);

        return postService.getPostsByLoginId(loginId)
                .thenApply(posts -> {
                    System.out.println("Posts retrieved: " + posts.size());
                    return ResponseEntity.ok(posts);
                })
                .exceptionally(ex -> {
                    System.err.println("Error in getMyPosts: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }

    // 게시물 수정
    @PatchMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> updatePost(@PathVariable("id") Long postId, @RequestBody PostRequest postRequest, Authentication authentication) {
        String loginId = authentication.getName();
        return postService.updatePost(postId, postRequest, loginId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(500).build());
    }

    // 게시물 삭제
    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deletePost(@PathVariable("id") Long postId, Authentication authentication) {
        String loginId = authentication.getName();
        return postService.deletePost(postId, loginId)
                .thenApply(v -> ResponseEntity.noContent().<Void>build())  // <Void> 명시적으로 추가
                .exceptionally(ex -> {
                    System.err.println("Error in deletePost: " + ex.getMessage());
                    return ResponseEntity.status(500).<Void>build();  // <Void> 명시적으로 추가
                });
    }
}