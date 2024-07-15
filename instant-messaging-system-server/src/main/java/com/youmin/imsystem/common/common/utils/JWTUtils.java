package com.youmin.imsystem.common.common.utils;



import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Component
public class JWTUtils {

    @Value("${imsystem.jwt.secret}")
    private String secret;
    private static final String UID = "UID";
    private static final String CREATE_TIME = "CREATE_TIME";

    //create jwt token
    public String createToken(Long uid){
        String token = JWT.create()
                .withClaim(UID, uid)
                .withClaim(CREATE_TIME, new Date())
                .sign(Algorithm.HMAC256(secret));
        return token;
    }

    //decode jwt to get claims
    public Map<String, Claim> verifyToken(String token){
        if(Objects.isNull(token)){
            return null;
        }
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
        DecodedJWT verify = verifier.verify(token);
        return verify.getClaims();
    }


    //get uid based on provided token
    public Long getUidOrNull(String token){
        return Optional.ofNullable(verifyToken(token))
                .map(map->map.get(UID))
                .map(Claim::asLong)
                .orElse(null);

    }
}
