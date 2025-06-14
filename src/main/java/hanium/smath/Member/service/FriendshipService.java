package hanium.smath.Member.service;

import hanium.smath.Member.entity.Friendship;
import hanium.smath.Member.repository.FriendshipRepository;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public FriendshipService(FriendshipRepository friendshipRepository, MemberRepository memberRepository) {
        this.friendshipRepository = friendshipRepository;
        this.memberRepository = memberRepository;
    }

    public Friendship addFriend(String memberLoginId, String friendLoginId) {
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Member friend = memberRepository.findByLoginId(friendLoginId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        if (friendshipRepository.findByMemberAndFriend(member, friend).isPresent()) {
            throw new RuntimeException("Friendship already exists");
        }

        Friendship friendship = Friendship.builder()
                .member(member)
                .friend(friend)
                .status("PENDING")
                .build();

        return friendshipRepository.save(friendship);
    }

    public void acceptFriendRequest(String memberLoginId, String friendLoginId) {
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Member friend = memberRepository.findByLoginId(friendLoginId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        Friendship friendship = friendshipRepository.findByMemberAndFriend(friend, member)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        if ("PENDING".equals(friendship.getStatus())) {
            friendship.setStatus("ACCEPTED");
            friendshipRepository.save(friendship);
        }
    }

    public void cancelFriendRequest(String memberLoginId, String friendLoginId) {
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Member friend = memberRepository.findByLoginId(friendLoginId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        Friendship friendship = friendshipRepository.findByMemberAndFriend(member, friend)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        if ("PENDING".equals(friendship.getStatus())) {
            friendshipRepository.delete(friendship);
        } else {
            throw new RuntimeException("Cannot cancel a non-pending friend request");
        }
    }

    public void rejectFriendRequest(String memberLoginId, String friendLoginId) {
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Member friend = memberRepository.findByLoginId(friendLoginId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        Friendship friendship = friendshipRepository.findByMemberAndFriend(friend, member)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        if ("PENDING".equals(friendship.getStatus())) {
            friendship.setStatus("REJECTED");
            friendshipRepository.save(friendship);
        } else {
            throw new RuntimeException("Cannot reject a non-pending friend request");
        }
    }

    public List<Friendship> getFriends(String memberLoginId) {
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return friendshipRepository.findByMember(member);
    }
}
