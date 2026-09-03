package com.yueyunzhi.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_follow")
@IdClass(UserFollowId.class)
public class UserFollow {
    
    @Id
    @Column(name = "follower_id")
    private Long followerId;
    
    @Id
    @Column(name = "following_id")
    private Long followingId;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    public UserFollow() {}
    
    public UserFollow(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.createTime = LocalDateTime.now();
    }
}