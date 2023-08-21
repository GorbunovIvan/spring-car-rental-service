package org.example.security;

import org.example.TestDataProvider;
import org.example.config.TestConfig;
import org.example.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

@ContextConfiguration(classes = TestConfig.class)
public class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    private TestDataProvider testDataProvider;

    @BeforeMethod
    public void setUp() {

        testDataProvider = new TestDataProvider();

        if (userRepository == null) {
            MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(userRepository);
        }

        when(userRepository.findByUsernameEagerly("")).thenReturn(Optional.empty());

        for (var user : testDataProvider.getUsers()) {
            when(userRepository.findByUsernameEagerly(user.getUsername())).thenReturn(Optional.of(user));
        }
    }

    @Test
    public void testLoadUserByUsername() {

        for (var user : testDataProvider.getUsers()) {
            assertEquals(userDetailsService.loadUserByUsername(user.getUsername()), user);
            verify(userRepository, times(1)).findByUsernameEagerly(user.getUsername());
        }

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(""));
    }
}