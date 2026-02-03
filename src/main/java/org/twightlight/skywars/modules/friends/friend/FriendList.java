package org.twightlight.skywars.modules.friends.friend;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FriendList {
    private List<Friend> friends;

    public FriendList(List<Friend> friends) {
        this.friends = friends;
    }

    public int getFriendsListSize() {
        return this.friends.size();
    }

    public boolean contains(Player user) {
        return this.friends.contains(new Friend(user.getUniqueId()));
    }

    void addFriend(Friend friend) {
        this.friends.add(friend);
    }

    public void removeFriend(Friend friend) {
        this.friends.remove(friend);
    }

    public void removeFriend(OfflinePlayer player) {
        this.friends.removeIf(friend -> {
            return friend.getUniqueId().equals(player.getUniqueId());
        });
    }

    public void clear() {
        this.friends.clear();
    }

    public List<Friend> getAllFriends() {
        return this.friends;
    }

    public List<Friend> getFriends(int page) {
        List<Friend> friends = new ArrayList<>();
        if (this.friends.isEmpty())
            return friends;
        int pageSize = 8;
        for (int i = ((page - 1) * pageSize > this.friends.size()) ? 0 : ((page - 1) * pageSize); i < ((pageSize * page > this.friends.size()) ? this.friends.size() : (pageSize * page)); i++)
            friends.add(this.friends.get(i));
        return friends;
    }
}
