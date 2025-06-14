package hanium.smath.Member.controller;

import hanium.smath.Member.entity.Friendship;
import hanium.smath.Member.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendshipController {

    private final FriendshipService friendshipService;

    @Autowired
    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addFriend(@RequestParam String memberLoginId, @RequestParam String friendLoginId) {
        friendshipService.addFriend(memberLoginId, friendLoginId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> acceptFriendRequest(@RequestParam String memberLoginId, @RequestParam String friendLoginId) {
        friendshipService.acceptFriendRequest(memberLoginId, friendLoginId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelFriendRequest(@RequestParam String memberLoginId, @RequestParam String friendLoginId) {
        friendshipService.cancelFriendRequest(memberLoginId, friendLoginId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject")
    public ResponseEntity<Void> rejectFriendRequest(@RequestParam String memberLoginId, @RequestParam String friendLoginId) {
        friendshipService.rejectFriendRequest(memberLoginId, friendLoginId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<Friendship>> getFriends(@RequestParam String memberLoginId) {
        return ResponseEntity.ok(friendshipService.getFriends(memberLoginId));
    }
}
