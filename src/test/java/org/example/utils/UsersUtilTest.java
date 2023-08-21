package org.example.utils;

import org.example.TestDataProvider;
import org.example.config.TestConfig;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@ContextConfiguration(classes = TestConfig.class)
public class UsersUtilTest {

    @InjectMocks
    private UsersUtil usersUtil;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private TestDataProvider testDataProvider;

    @BeforeMethod
    public void setUp() {

        testDataProvider = new TestDataProvider();

        if (securityContext == null) {
            MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(securityContext, authentication);
        }

        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testGetCurrentUser() {

        // authorized
        for (var user : testDataProvider.getUsers()) {
            when(authentication.getPrincipal()).thenReturn(user);
            assertEquals(usersUtil.getCurrentUser(), user);
        }
        verify(securityContext, times(testDataProvider.getUsers().size())).getAuthentication();
        verify(authentication, times(testDataProvider.getUsers().size())).isAuthenticated();

        Mockito.reset(securityContext, authentication);

        // unauthorized
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authentication.getPrincipal()).thenReturn(null);
        assertNull(usersUtil.getCurrentUser());

        verify(securityContext, times(1)).getAuthentication();

    }
}