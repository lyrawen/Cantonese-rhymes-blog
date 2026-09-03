package com.yueyunzhi.repository;

import com.yueyunzhi.entity.User;
import com.yueyunzhi.entity.UserFollow;
import com.yueyunzhi.entity.UserFollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserFollowRepository extends JpaRepository<UserFollow, UserFollowId> {
    
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    @Query("SELECT COUNT(*) FROM UserFollow WHERE followingId = :userId")
    long countFollowersByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(*) FROM UserFollow WHERE followerId = :userId")
    long countFollowingByUserId(@Param("userId") Long userId);
    
    @Query("SELECT u FROM User u WHERE u.userId IN (SELECT uf.followerId FROM UserFollow uf WHERE uf.followingId = :userId)")
    List<User> findFollowersByUserId(@Param("userId") Long userId);
    
    @Query(value = "SELECT following_id FROM user_follow GROUP BY following_id ORDER BY COUNT(*) DESC LIMIT 3", nativeQuery = true)
    List<Long> findTop3UserIdsByFollowerCount();
    
    @Query("SELECT u FROM User u WHERE u.userId IN (SELECT uf.followingId FROM UserFollow uf WHERE uf.followerId = :userId)")
    List<User> findFollowingByUserId(@Param("userId") Long userId);

    @Query("SELECT uf1.followingId FROM UserFollow uf1 WHERE uf1.followerId = :userId AND uf1.followingId <> :userId " +
           "AND EXISTS (SELECT 1 FROM UserFollow uf2 WHERE uf2.followerId = uf1.followingId AND uf2.followingId = :userId)")
    List<Long> findMutualFriendIds(@Param("userId") Long userId);
}