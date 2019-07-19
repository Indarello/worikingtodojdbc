package com.sample.controllers;

import com.sample.entities.User;
import com.sample.payloads.ApiResponse;
import com.sample.payloads.AuthenticationResponse;
import com.sample.payloads.Login;
import com.sample.services.SecurityService;
import com.sample.services.UserService;
import com.sample.token.TokenProvider;
import com.sample.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

/**
 * Контроллер для работы с пользователем (регистрация, авторизация).
 * RestAPI, обеспечивает обработку запросов внешнего приложения.
 */
@RestController
public class UserController {

    public static final Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserValidator userValidator;


    @PostMapping(path="/registration", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> registration(@RequestBody Login login){

        String token = "Token is not generated, please sign in using /login";
        logger.info("Registration started");
        User user = new User(login.getUsername(), login.getPassword());

        ApiResponse valid = userValidator.validateRegistration(login);

        if(valid.getSuccess()){
            userService.save(user);

            try {
                token = tokenProvider.createToken(securityService.autoLogin(user.getUsername(), user.getPassword()));
            } catch (Exception e){
                logger.info(e.getMessage());
                return new ResponseEntity(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity(new AuthenticationResponse(token)
                    ,HttpStatus.OK);
        }

        return new ResponseEntity(
                    valid, HttpStatus.BAD_REQUEST);
    }


    @CrossOrigin("/*")
    @PostMapping(path="/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<?> login(@RequestBody Login login){

        String token;

        boolean valid = userValidator.validateAuthorization(login);

        logger.info("Login started");
        if(valid == false){
            logger.info("Username is not found");
           return new ResponseEntity(
                   new ApiResponse(false,"Username is not found"), HttpStatus.BAD_REQUEST);
        }

        token = tokenProvider.createToken(securityService.autoLogin(login.getUsername(),login.getPassword()));

        return new ResponseEntity(new AuthenticationResponse(token),HttpStatus.OK);
    }

}
