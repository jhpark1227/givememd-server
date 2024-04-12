package junhyeok.giveme.user.dao;


public interface RefreshTokenDao {
    void save(Long id, String token);

    String findById(Long id);

    void delete(Long id);
}
