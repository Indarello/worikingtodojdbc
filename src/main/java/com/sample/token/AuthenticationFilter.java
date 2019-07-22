package com.sample.token;

import com.sample.services.Speedingservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
@Order(1)
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());
    private List<UserDetails> userdetails  = new ArrayList<UserDetails>();

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private Speedingservice speedingservice;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try{
            logger.info("time for filter 1:" + System.nanoTime());
            String jwt = getJwtFromRequest(httpServletRequest);
            logger.info("time for filter 2:" + System.nanoTime());
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromJWT(jwt);
                logger.info("time for filter 3:" + System.nanoTime());

                UserDetails userDetails = null;
                List<UserDetails> userDet = new ArrayList<UserDetails>();
                boolean founded = false;
                for(UserDetails tempdet: userdetails) {
                    if (tempdet.getUsername().equals(username)) {
                        userDetails = tempdet;
                        founded = true;
                        break;
                    }
                }
                if(!founded) {
                    UserDetails TempDetails = userDetailsService.loadUserByUsername(username);
                    userdetails.add(TempDetails);
                    userDetails = TempDetails;
                }
                logger.info("User details: " + userDetails);
                logger.info("time for filter 4:" + System.nanoTime());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                logger.info("time for filter 5:" + System.nanoTime());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                logger.info("time for filter 6:" + System.nanoTime());
                logger.info("authentication in filter");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.info("Could not set user authentication in security context: "+ ex.getMessage());
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

    private String getJwtFromRequest(HttpServletRequest request){
        logger.info("Getting JWT from request");
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken)&&bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7,bearerToken.length());
        }
        return null;
    }



}
