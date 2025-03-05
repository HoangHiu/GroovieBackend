package org.myapp.groovie.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtService {
    private final String secretKey = "f1d0c1f174fcd2855520e11667ec2be0b0a404f9760c2fe167b4b4a22cb64f65b6b1046bcf79a0731d2d95fa18ee0f264e70970ff6512b73684fda53fc00cc0203c6144107163e207cafb3b8e9b7ebfa4bc38b61fc2678cbdff1eee4f08c9f75c31081515bbc959528d82cbfc0ab368bcae20e7943c8a2c519c9a4f2613594f18532e60307d15dbbbde37bb851765f90e7054ab7f69b78e0120713926805d3a94e81a42a37871e316aabfe173bc07b06ce9a1bbf346b2fbb094f07e6e31e9a5ebca01774401c7c07efaad6aca2141a20861eac4328d522293bd5a809c4ebeda340605b24fe9045e99f9903239fc2e3890bda25d0e7fcfce7a439283187771277";

    private SecretKey getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, List<String> roles) throws ApiCallException {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("roles",roles);

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 30 * 1000L))
                .and()
                .signWith(this.getKey())
                .compact();
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
}
