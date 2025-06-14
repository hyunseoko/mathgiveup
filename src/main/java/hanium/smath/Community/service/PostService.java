package hanium.smath.Community.service;

import hanium.smath.Community.dto.PostRequest;
import hanium.smath.Community.dto.PostResponse;
import hanium.smath.Community.entity.Post;
import hanium.smath.Community.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 게시글 저장
    public CompletableFuture<Post> savePost(Post post) {
        return CompletableFuture.supplyAsync(() -> postRepository.save(post));
    }

    // 게시글 불러오기 - 전체
    public CompletableFuture<List<PostResponse>> getAllPosts() {
        return CompletableFuture.supplyAsync(() -> {
            List<Post> posts = postRepository.findAll();  // 모든 게시물 가져오기
            return posts.stream().map(post -> PostResponse.builder()
                            .id(post.getIdPost())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .build())
                    .collect(Collectors.toList());
        });
    }

    // 게시글 불러오기 - 아이디 찾아서
    public CompletableFuture<List<PostResponse>> getPostsByLoginId(String login_id) {
        return CompletableFuture.supplyAsync(() -> {
            List<Post> posts = postRepository.findByMemberLoginId(login_id);
            return posts.stream().map(post -> PostResponse.builder()
                            .id(post.getIdPost())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .build())
                    .collect(Collectors.toList());
        });
    }

    // 글 수정
    public CompletableFuture<String> updatePost(Long postId, PostRequest request, String loginId) {
        return CompletableFuture.supplyAsync(() -> {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

            // 게시글 작성자와 로그인한 사용자가 동일한지 확인
            if (!post.getMember().getLoginId().equals(loginId)) {
                throw new IllegalArgumentException("You are not allowed to edit this post.");
            }

            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            postRepository.save(post);

            return post.getUpdatedTime().toString();
        });
    }

    // 글 삭제 메서드
    public CompletableFuture<Void> deletePost(Long postId, String loginId) {
        return CompletableFuture.runAsync(() -> {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

            // 게시글 작성자와 로그인한 사용자가 동일한지 확인
            if (!post.getMember().getLoginId().equals(loginId)) {
                throw new IllegalArgumentException("You are not allowed to delete this post.");
            }

            postRepository.delete(post);
        });
    }
}
