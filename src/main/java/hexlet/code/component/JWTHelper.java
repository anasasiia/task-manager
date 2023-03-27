package hexlet.code.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.security.Key;

import io.jsonwebtoken.security.Keys;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.impl.TextCodec.BASE64;

@Component
public final class JWTHelper {
    private final long oneThousand = 1000;
    private final String secretKey = "3777217A25432A462D4A614E645267556B58703273357638782F413F4428472B";
    private final String issuer;
    private final Long expirationSec;
    private final Long clockSkewSec;
    private final Clock clock;

    public JWTHelper(@Value("${jwt.issuer:task_manager}") final String issuer1,
                     @Value("${jwt.expiration-sec:86400}") final Long expirationSec1,
                     @Value("${jwt.clock-skew-sec:300}") final Long clockSkewSec1) {
        this.issuer = issuer1;
        this.expirationSec = expirationSec1;
        this.clockSkewSec = clockSkewSec1;
        this.clock = DefaultClock.INSTANCE;
    }

    public String expiring(final Map<String, Object> attributes) {
        return Jwts.builder()
                .signWith(getSignInKey(), HS256)
                .setClaims(getClaims(attributes, expirationSec))
                .compact();
    }

    public Map<String, Object> verify(final String token) {
        return Jwts.parser()
                .requireIssuer(issuer)
                .setClock(clock)
                .setAllowedClockSkewSeconds(clockSkewSec)
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims getClaims(final Map<String, Object> attributes, final Long expiresInSec) {
        final Claims claims = Jwts.claims();
        claims.setIssuer(issuer);
        claims.setIssuedAt(clock.now());
        claims.putAll(attributes);
        if (expiresInSec > 0) {
            claims.setExpiration(new Date(System.currentTimeMillis() + expiresInSec * oneThousand));
        }
        return claims;
    }

    private Key getSignInKey() {
        byte[] keyBytes = BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
