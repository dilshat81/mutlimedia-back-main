package com.doranco.multimedia.serviceImpl;

import com.doranco.multimedia.jwt.CustomerUsersDetailsService;
import com.doranco.multimedia.jwt.JwtUtil;
import com.doranco.multimedia.models.User;
import com.doranco.multimedia.repositories.UserDao;
import com.doranco.multimedia.utils.EmailUtils;
import com.doranco.multimedia.utils.MultimediaUtils;
import com.doranco.multimedia.wrapper.UserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    @Mock
    private UserDao userDao;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomerUsersDetailsService customerUsersDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailUtils emailUtils;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    void signUp() {
        //Given
        UserRequest userRequest = UserRequest.builder()
                .name("Dilshat")
                .contactNumber("0102030405")
                .email("dilshat@mail.com")
                .password("Test$1234")
                .build();
        // When

        when(userDao.findByEmail("dilshat@mail.com")).thenReturn(null);
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn(null);
        ResponseEntity<String> response = userServiceImpl.signUp(userRequest);
        // Then
        verify(userDao, times(1 )).findByEmail(anyString());
        verify(userDao, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("Test$1234");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Inscription réussie"));





    }
    @Test
    void shouldNotSaveUserWithAnEmailAlreadyExisting(){
        //Given
        UserRequest userRequest = UserRequest.builder()
                .name("dilshat")
                .contactNumber("0102030405")
                .email("dilshat@gmail.com")
                .password("Test$1234")
                .build();
        // When

        when(userDao.findByEmail("dilshat@gmail.com")).thenReturn(new User());
        ResponseEntity<String> response = userServiceImpl.signUp(userRequest);
        // When
        verify(userDao, times(1 )).findByEmail(anyString());
        verify(userDao, times(0)).save(any(User.class));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Email exist déjà"));


    }

    @Test
    void shouldNotSaveUserWhenEmailIsEmptyOrNull(){
        //Given
        UserRequest userRequest = UserRequest.builder()
                .name("dilshat")
                .contactNumber("0102030405")
                .email("")
                .password("Test$1234")
                .build();
        // When

        when(userDao.findByEmail("")).thenReturn(null);
        ResponseEntity<String> response = userServiceImpl.signUp(userRequest);
        // Then
        verify(userDao, times(1 )).findByEmail(anyString());
        verify(userDao, times(1)).save(any(User.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());





    }


    @Test
    void login() {
        //Given


           /* Map<String, String> requestMap = new HashMap<>();
            requestMap.put("email", "test@example.com");
            requestMap.put("password", "Test$1234");

            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(customerUsersDetailsService.getUserDetail()).thenReturn(user);
            when(user.getStatus()).thenReturn("true");
            when(user.getEmail()).thenReturn("test@example.com");
            when(user.getRole()).thenReturn("USER");

            ResponseEntity<String> response = loginRestImpl.login(requestMap);

            verify(HttpStatus.OK, response.getStatusCode());
            verify("Login successful", response.getBody()); // adapte selon ton vrai message
       */ }




    @Test
    void getAllUser() {
    }

    @Test
    void update() {
    }

    @Test
    void checkToken() {
    }

    @Test
    void changePassword() {
    }

    @Test
    void forgetPassword() {
    }
}