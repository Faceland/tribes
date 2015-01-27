package me.topplethenun.tribes.managers;

import com.google.common.base.Preconditions;
import me.topplethenun.tribes.data.Member;
import org.nunnerycode.kern.shade.google.common.base.Optional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MemberManager {

    private final Map<UUID, Member> memberMap;

    public MemberManager() {
        memberMap = new ConcurrentHashMap<>();
    }

    public void addMember(Member member) {
        Preconditions.checkNotNull(member);
        Preconditions.checkState(!memberMap.containsKey(member.getUniqueId()));
        memberMap.put(member.getUniqueId(), member);
    }

    public void removeMember(Member member) {
        Preconditions.checkNotNull(member);
        Preconditions.checkState(memberMap.containsKey(member.getUniqueId()));
        memberMap.remove(member.getUniqueId());
    }

    public void removeMember(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        Preconditions.checkState(memberMap.containsKey(uuid));
        memberMap.remove(uuid);
    }

    public boolean hasMember(Member member) {
        Preconditions.checkNotNull(member);
        return memberMap.containsKey(member.getUniqueId());
    }

    public boolean hasMember(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return memberMap.containsKey(uuid);
    }

    public Set<Member> getMembers() {
        return new HashSet<>(memberMap.values());
    }

    public Optional<Member> getMember(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return memberMap.containsKey(uuid) ? Optional.of(memberMap.get(uuid)) : Optional.<Member>absent();
    }

}
