import java.io.*;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.*;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import org.bouncycastle.util.io.pem.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm;

public class AppleAPIService {
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		try (PemReader pemReader = new PemReader(new FileReader("AuthKey_5QRFG5C59Q.p8"))) {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PemObject pemObj = pemReader.readPemObject();
            byte[] content = pemObj.getContent();
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(content);
            ECPrivateKey privateKey = (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
            String token = JWT.create()
	            .withKeyId("5QRFG5C59Q")
	            .withIssuer("69a6de82-111e-47e3-e053-5b8c7c11a4d1")
	            .withIssuedAt(new Date())
	            .withExpiresAt(new Date(System.currentTimeMillis() + 1199L))
	            .withClaim("scope", Collections.singletonList("GET /v1/apps"))
	            .withJWTId(UUID.randomUUID().toString())
	            .withAudience("appstoreconnect-v1")
	            .sign(Algorithm.ECDSA256(privateKey));
            System.out.println("JWT token: " + token);
            
            //token = "eyJraWQiOiJVNU5COU43QjlQIiwiYWxnIjoiRVMyNTYiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiI2OWE2ZGU4Mi0xMjFlLTQ4ZTMtZTA1My01YjhjN2MxMWE0ZDEiLCJpYXQiOjE3MDQ1NTI2ODMsImV4cCI6MTcwNDU1MjY4OCwibmJmIjoxNzA0NTUyNjg0LCJqdGkiOiI5N2VmYjc5NS0wNjgxLTQ5MmEtOWFiYy0zMjcyOTRlYjdjMzEiLCJhdWQiOiJhcHBzdG9yZWNvbm5lY3QtdjEifQ.6y0SSO2dVLFQMje-ykVcBRfaYwULi0QANCKIIU7eWjTyM86RyTub9595MqSi3IrVa6eazYsCJC63UaJm_txJ_Q";
            // Create headers with Authorization
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            System.out.println("headers: " + headers);

            // Create HttpEntity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make GET request using RestTemplate
            ResponseEntity<String> response = new RestTemplate().exchange(
                "https://api.appstoreconnect.apple.com/v1/apps",
                HttpMethod.GET, entity, String.class);
            
            // Handle the response
			if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                System.out.println("Response: " + responseBody);
            } else {
                System.out.println("Error: " + response.getStatusCodeValue());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	}
}
