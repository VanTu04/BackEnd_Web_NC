package com.vawndev.spring_boot_readnovel.Services.Impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.SubscriptionResponse;
import com.vawndev.spring_boot_readnovel.Entities.Role;
import com.vawndev.spring_boot_readnovel.Entities.Subscription;
import com.vawndev.spring_boot_readnovel.Entities.SubscriptionPlans;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Enum.SUBSCRIPTION_TYPE;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.RoleRepository;
import com.vawndev.spring_boot_readnovel.Repositories.SubscriptionPlansRepository;
import com.vawndev.spring_boot_readnovel.Repositories.SubscriptionRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionPlansRepository subscriptionPlansRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TokenHelper tokenHelper;

    private User mockUser;
    private SubscriptionPlans mockPlan;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId("user-id-123");
        mockUser.setBalance(BigDecimal.valueOf(100));
        mockUser.setRoles(new HashSet<>());

        mockPlan = new SubscriptionPlans();
        mockPlan.setId("plan-id-123");
        mockPlan.setPrice(BigDecimal.valueOf(50));
        mockPlan.setExpired(30L);
        mockPlan.setType(SUBSCRIPTION_TYPE.PREMIUM.name());
    }

    // 1. upgradeSubscription: thành công, user chưa đăng ký, đủ tiền
    @Test
    void upgradeSubscription_success() {
        SubscriptionRequest req = new SubscriptionRequest();
        req.setId_plan("plan-id-123");

        when(tokenHelper.getUserO2Auth()).thenReturn(mockUser);
        when(subscriptionRepository.findByUserId(mockUser.getId())).thenReturn(Optional.empty());
        when(subscriptionPlansRepository.findById("plan-id-123")).thenReturn(Optional.of(mockPlan));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        subscriptionService.upgradeSubscription(req);

        verify(userRepository).save(argThat(user -> user.getBalance().compareTo(BigDecimal.valueOf(50)) == 0));
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    // 2. upgradeSubscription: user đã có subscription chưa hết hạn → lỗi
    @Test
    void upgradeSubscription_conflict_subscription() {
        SubscriptionRequest req = new SubscriptionRequest();
        req.setId_plan("plan-id-123");

        Subscription existingSub = new Subscription();
        existingSub.setExpiredAt(Instant.now().plusSeconds(3600)); // chưa hết hạn

        when(tokenHelper.getUserO2Auth()).thenReturn(mockUser);
        when(subscriptionRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(existingSub));

        AppException ex = assertThrows(AppException.class, () -> subscriptionService.upgradeSubscription(req));
        assertEquals(ErrorCode.CONFLICT_SUBSCRIPTION, ex.getErrorCode());
    }

    // 3. upgradeSubscription: không đủ tiền → lỗi
    @Test
    void upgradeSubscription_insufficient_balance() {
        mockUser.setBalance(BigDecimal.valueOf(10));
        SubscriptionRequest req = new SubscriptionRequest();
        req.setId_plan("plan-id-123");

        when(tokenHelper.getUserO2Auth()).thenReturn(mockUser);
        when(subscriptionRepository.findByUserId(mockUser.getId())).thenReturn(Optional.empty());
        when(subscriptionPlansRepository.findById("plan-id-123")).thenReturn(Optional.of(mockPlan));

        AppException ex = assertThrows(AppException.class, () -> subscriptionService.upgradeSubscription(req));
        assertEquals(ErrorCode.FAILED_PAYMENT, ex.getErrorCode());
    }

    // 4. upgradeSubscription: plan không tồn tại → lỗi
    @Test
    void upgradeSubscription_planNotFound() {
        SubscriptionRequest req = new SubscriptionRequest();
        req.setId_plan("nonexistent-plan-id");

        when(tokenHelper.getUserO2Auth()).thenReturn(mockUser);
        when(subscriptionRepository.findByUserId(mockUser.getId())).thenReturn(Optional.empty());
        when(subscriptionPlansRepository.findById("nonexistent-plan-id")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> subscriptionService.upgradeSubscription(req));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    // 5. getSubscription: chưa có subscription → trả null fields
    @Test
    void getSubscription_empty() {
        when(tokenHelper.getUserO2Auth()).thenReturn(mockUser);
        when(subscriptionRepository.findByUserId(mockUser.getId())).thenReturn(Optional.empty());

        SubscriptionResponse res = subscriptionService.getSubscription();
        assertNull(res.getType());
        assertNull(res.getExpiredAt());
    }

    // 6. getSubscription: có subscription → trả dữ liệu đúng
    @Test
    void getSubscription_success() {
        Subscription sub = new Subscription();
        sub.setExpiredAt(Instant.now());
        sub.setPlan(mockPlan);

        when(tokenHelper.getUserO2Auth()).thenReturn(mockUser);
        when(subscriptionRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(sub));

        SubscriptionResponse res = subscriptionService.getSubscription();
        assertEquals(SUBSCRIPTION_TYPE.PREMIUM.name(), res.getType());
        assertNotNull(res.getExpiredAt());
    }

    // 7. upgradeRole: user đã có role AUTHOR → lỗi
    @Test
    void upgradeRole_already_author() {
        Role author = new Role();
        author.setName("AUTHOR");
        mockUser.getRoles().add(author);

        when(tokenHelper.getUserO2Auth()).thenReturn(mockUser);

        AppException ex = assertThrows(AppException.class, () -> subscriptionService.upgradeRole());
        assertEquals(ErrorCode.CONFLICT, ex.getErrorCode());
    }

    // 8. upgradeRole: không đủ tiền → lỗi
    @Test
    void upgradeRole_insufficient_balance() {
        mockUser.setBalance(BigDecimal.valueOf(10));
        mockUser.setRoles(new HashSet<>());

        Role authorRole = new Role();
        authorRole.setName("AUTHOR");

        when(tokenHelper.getUserO2Auth()).thenReturn(mockUser);
        when(roleRepository.findByName("AUTHOR")).thenReturn(Optional.of(authorRole));

        AppException ex = assertThrows(AppException.class, () -> subscriptionService.upgradeRole());
        assertEquals(ErrorCode.FAILED_PAYMENT, ex.getErrorCode());
    }

    // 9. upgradeRole: thành công
    @Test
    void upgradeRole_success() {
        mockUser.setBalance(BigDecimal.valueOf(100));
        mockUser.setRoles(new HashSet<>());

        Role authorRole = new Role();
        authorRole.setName("AUTHOR");

        when(tokenHelper.getUserO2Auth()).thenReturn(mockUser);
        when(roleRepository.findByName("AUTHOR")).thenReturn(Optional.of(authorRole));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        subscriptionService.upgradeRole();

        verify(userRepository).save(mockUser);
        assertTrue(mockUser.getRoles().contains(authorRole));
        assertEquals(BigDecimal.ZERO, mockUser.getBalance());
    }

    // 10. resetExpiredSubscriptions: có subscription hết hạn → xóa và remove role
    // AUTHOR
    @Test
    void resetExpiredSubscriptions_success() {
        Subscription expiredSub = new Subscription();
        expiredSub.setExpiredAt(Instant.now().minusSeconds(1000));
        User user = new User();
        user.setRoles(new HashSet<>());
        Role authorRole = new Role();
        authorRole.setName("AUTHOR");
        user.getRoles().add(authorRole);
        expiredSub.setUser(user);

        when(subscriptionRepository.findByExpiredAtBefore(Instant.now())).thenReturn(java.util.List.of(expiredSub));
        when(userRepository.save(user)).thenAnswer(i -> i.getArgument(0));

        subscriptionService.resetExpiredSubscriptions();

        verify(subscriptionRepository).delete(expiredSub);
        verify(userRepository).save(user);
        assertTrue(user.getRoles().isEmpty());
    }
}
