package com.yueyunzhi.service;

import com.yueyunzhi.entity.User;
import com.yueyunzhi.entity.UserFollow;
import com.yueyunzhi.repository.UserFollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserFollowService {
    
    @Autowired
    private UserFollowRepository userFollowRepository;
    
    @Transactional
    public boolean toggleFollow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            return false;
        }
        
        boolean alreadyFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
        
        if (alreadyFollowing) {
            userFollowRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
            return false;
        } else {
            UserFollow userFollow = new UserFollow(followerId, followingId);
            userFollowRepository.save(userFollow);
            return true;
        }
    }
    
    public long getFollowerCount(Long userId) {
        return userFollowRepository.countFollowersByUserId(userId);
    }
    
    public long getFollowingCount(Long userId) {
        return userFollowRepository.countFollowingByUserId(userId);
    }
    
    public boolean isFollowing(Long followerId, Long followingId) {
        return userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    /** 双方互相关注 */
    public boolean isMutualFollow(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null || userId1.equals(userId2)) {
            return false;
        }
        return userFollowRepository.existsByFollowerIdAndFollowingId(userId1, userId2)
                && userFollowRepository.existsByFollowerIdAndFollowingId(userId2, userId1);
    }

    /** 与当前用户互关的好友 ID 列表 */
    public List<Long> findMutualFriendIds(Long userId) {
        return userFollowRepository.findMutualFriendIds(userId);
    }
    
    public List<User> getFollowers(Long userId) {
        return userFollowRepository.findFollowersByUserId(userId);
    }
    
    public List<User> getFollowing(Long userId) {
        return userFollowRepository.findFollowingByUserId(userId);
    }
}