package org.twightlight.skywars.modules.friends.user;

import org.twightlight.skywars.modules.friends.friend.FriendList;

public class User {
    private boolean allowingRequests;

    private final FriendList friendList;

    private int friendLimit;

    User(FriendList friendList, boolean allowingRequests, int friendLimit) {
        this.allowingRequests = allowingRequests;
        this.friendLimit = friendLimit;
        this.friendList = friendList;
    }

    public void toggleRequests() {
        this.allowingRequests = !this.allowingRequests;
    }

    public boolean hasReachedFriendLimit() {
        return (getFriendList().getFriendsListSize() >= this.friendLimit);
    }

    public boolean isAllowingRequests() {
        return this.allowingRequests;
    }

    public FriendList getFriendList() {
        return this.friendList;
    }

    public int getFriendLimit() {
        return this.friendLimit;
    }
}
