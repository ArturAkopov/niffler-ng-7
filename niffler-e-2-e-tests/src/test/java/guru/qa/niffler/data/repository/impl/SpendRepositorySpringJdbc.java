package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.jdbc.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendRepositorySpringJdbc implements SpendRepository {

    private static final Config config = Config.getInstance();

    private final SpendDao spendDao = new SpendDaoSpringJdbc();
    private final CategoryDao categoryDao = new CategoryDaoSpringJdbc();

    @Nonnull
    @Override
    public SpendEntity createSpend(SpendEntity spendEntity) {
        final UUID categoryId = spendEntity.getCategory().getId();
        if (categoryId == null && categoryDao.findCategoryById(categoryId).isEmpty()) {
            spendEntity.setCategory(
                    categoryDao.create(spendEntity.getCategory())
            );
        }
        return spendDao.create(spendEntity);
    }

    @Nonnull
    @Override
    public SpendEntity update(SpendEntity spend) {
        spendDao.update(spend);
        categoryDao.update(spend.getCategory());
        return spend;
    }

    @Nonnull
    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryDao.create(category);
    }

    @Nonnull
    @Override
    public CategoryEntity updateCategory(CategoryEntity categoryEntity) {
        categoryDao.update(categoryEntity);
        return categoryEntity;
    }

    @Nonnull
    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findCategoryById(id);
    }

    @Nonnull
    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(config.spendJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM category WHERE username = ? and name = ?",
                        CategoryEntityRowMapper.instance,
                        username,
                        name
                )
        );
    }

    @Nonnull
    @Override
    public Optional<SpendEntity> findSpendByUsernameAndSpendDescription(String username, String description) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(config.spendJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM spend WHERE username = ? and description = ?",
                        SpendEntityRowMapper.instance,
                        username,
                        description
                )
        );
    }

    @Nonnull
    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        return spendDao.findSpendById(id);
    }

    @Nonnull
    @Override
    public List<SpendEntity> findAllSpendByUsername(String username) {
        return spendDao.findAllByUsername(username);
    }

    @Nonnull
    @Override
    public List<SpendEntity> findAllSpend() {
        return spendDao.findAll();
    }

    @Override
    public void remove(SpendEntity spendEntity) {
        spendDao.deleteSpend(spendEntity);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        categoryDao.deleteCategory(category);
    }
}
