package com.doranco.multimedia.serviceImpl;

import com.doranco.multimedia.constents.MultimediaConstants;
import com.doranco.multimedia.jwt.CustomerUsersDetailsService;
import com.doranco.multimedia.jwt.JwtFilter;
import com.doranco.multimedia.jwt.JwtUtil;
import com.doranco.multimedia.models.User;
import com.doranco.multimedia.repositories.UserDao;
import com.doranco.multimedia.service.UserService;
import com.doranco.multimedia.utils.EmailUtils;
import com.doranco.multimedia.utils.MultimediaUtils;
import com.doranco.multimedia.wrapper.UserRequest;
import com.doranco.multimedia.wrapper.UserWrapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Value("${user.mail}")
    private String hostMail;

    @Autowired
    UserDao userDao;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUsersDetailsService customerUsersDetailsService;

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    @Override
    public ResponseEntity<String> signUp(UserRequest userRequest) {
        log.info("Inside signUp {}", userRequest);

        User user = userDao.findByEmail(userRequest.getEmail());
        if (Objects.isNull(user)) {
            userDao.save(getUserFromMap(userRequest));
            return MultimediaUtils.getResponseEntity("Inscription réussie", HttpStatus.OK);

        } else {
            return MultimediaUtils.getResponseEntity("Email exist déjà", HttpStatus.BAD_REQUEST);
        }


    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );

            if (auth.isAuthenticated()) {
                if (customerUsersDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUsersDetailsService.getUserDetail().getEmail(),
                                    customerUsersDetailsService.getUserDetail().getRole()) + "\"}",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"" + "Attendez la validation de l'administrateur." + "\"}", HttpStatus.BAD_REQUEST);
                }
            }

        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return new ResponseEntity<String>("{\"message\":\"" + "Identifiants invalides." + "\"}", HttpStatus.BAD_REQUEST);
    }


    private User getUserFromMap(UserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setContactNumber(userRequest.getContactNumber());
        user.setEmail(userRequest.getEmail());
        user.setPassword(encoder.encode(userRequest.getPassword()));
        user.setStatus("false");
        user.setRole("user");
        return user;

    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if (jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if (!optional.isEmpty()) {
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());
                    return MultimediaUtils.getResponseEntity("Mise à jour de l'utilisateur réussie", HttpStatus.OK);
                } else {
                    MultimediaUtils.getResponseEntity("l'utilisateur id n'existe pas", HttpStatus.OK);
                }
            } else {
                return MultimediaUtils.getResponseEntity(MultimediaConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true")) {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Compte validé", "User:- " + user + "\n is approved by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
        } else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account disabled", "User:- " + user + "\n is disabled by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
        }
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return MultimediaUtils.getResponseEntity("true", HttpStatus.OK);

    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
            if (!userObj.equals(null)) {
                if (encoder.matches(requestMap.get("oldPassword"), userObj.getPassword()) ){
                     userObj.setPassword(encoder.encode(requestMap.get("newPassword")));
                    userDao.save(userObj);
                    return MultimediaUtils.getResponseEntity("Mot de passe mise à jour avec succès", HttpStatus.OK);
                } else {
                    return MultimediaUtils.getResponseEntity("Ancien mot de passe incorrect", HttpStatus.BAD_REQUEST);
                }

            } else {
                return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgetPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {

                UUID uuid = UUID.randomUUID();
                String uniquePassword = uuid.toString();

                user.setPassword(encoder.encode(uniquePassword));
                userDao.save(user);
                emailUtils.forgotMail(hostMail, "Vous pouvez change votre mot de passe", uniquePassword);
                return MultimediaUtils.getResponseEntity("Vérifiez votre courrier pour les informations d'identification", HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
