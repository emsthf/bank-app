package shop.sol.bank.config.jwt;

// SECRET 노출되면 안되는 데이터(나중에 꼭 환경변수로 설정해줘야 한다.)
public interface JwtVO {

    public static final String SECRET = "MySecretKey";  // H256 (대칭키)
    public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;  // 일주일
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
}
