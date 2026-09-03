package com.yueyunzhi.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserFollowId implements Serializable {
    
    private Long followerId;
    private Long followingId;
    
    public UserFollowId() {}
    
    public UserFollowId(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
