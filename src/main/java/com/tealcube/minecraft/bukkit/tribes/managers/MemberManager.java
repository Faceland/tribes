/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.tealcube.minecraft.bukkit.tribes.managers;

import com.google.common.base.Preconditions;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Optional;
import com.tealcube.minecraft.bukkit.tribes.data.Member;

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

    public Set<Member> getMembersWithTribe(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        Set<Member> members = new HashSet<>();
        for (Member member : getMembers()) {
            if (member.getTribe() != null && member.getTribe().equals(uuid)) {
                members.add(member);
            }
        }
        return members;
    }

}
