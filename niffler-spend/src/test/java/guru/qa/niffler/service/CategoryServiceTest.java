package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private final String username = "Artur";
    private CategoryEntity mainCategoryEntity;

    @BeforeEach
    void init() {
        mainCategoryEntity = new CategoryEntity();
        mainCategoryEntity.setId(UUID.randomUUID());
        mainCategoryEntity.setUsername(username);
        mainCategoryEntity.setName("Категория");
        mainCategoryEntity.setArchived(true);
    }

    @Test
    void categoryNotFoundExceptionShouldBeThrown(@Mock CategoryRepository categoryRepository) {
        final String username = "not_found";
        final UUID id = UUID.randomUUID();

        when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.empty());

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                "",
                username,
                true
        );

        CategoryNotFoundException ex = assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.update(categoryJson)
        );
        Assertions.assertEquals(
                "Can`t find category by id: '" + id + "'",
                ex.getMessage()
        );
    }

    @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
    @ParameterizedTest
    void categoryNameArchivedShouldBeDenied(String catName, @Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();

        when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.of(
                        cat
                ));

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                catName,
                username,
                true
        );

        InvalidCategoryNameException ex = assertThrows(
                InvalidCategoryNameException.class,
                () -> categoryService.update(categoryJson)
        );
        Assertions.assertEquals(
                "Can`t add category with name: '" + catName + "'",
                ex.getMessage()
        );
    }

    @Test
    void onlyTwoFieldsShouldBeUpdated(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();
        cat.setId(id);
        cat.setUsername(username);
        cat.setName("Магазины");
        cat.setArchived(false);

        when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.of(
                        cat
                ));
        when(categoryRepository.save(any(CategoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                "Бары",
                username,
                true
        );

        categoryService.update(categoryJson);
        ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
        verify(categoryRepository).save(argumentCaptor.capture());
        assertEquals("Бары", argumentCaptor.getValue().getName());
        assertEquals("duck", argumentCaptor.getValue().getUsername());
        assertTrue(argumentCaptor.getValue().isArchived());
        assertEquals(id, argumentCaptor.getValue().getId());
    }

    @Test
    void getAllCategoriesIsFilteredByExcludeArchived(@NonNull @Mock CategoryRepository categoryRepository) {

        final CategoryEntity categoryEntity1 = new CategoryEntity();
        categoryEntity1.setId(UUID.randomUUID());
        categoryEntity1.setUsername(username);
        categoryEntity1.setName("Категория 1");
        categoryEntity1.setArchived(false);

        final CategoryEntity categoryEntity2 = new CategoryEntity();
        categoryEntity2.setId(UUID.randomUUID());
        categoryEntity2.setUsername(username);
        categoryEntity2.setName("Категория 2");
        categoryEntity2.setArchived(false);

        final CategoryEntity thirdCat = new CategoryEntity();
        categoryEntity2.setId(UUID.randomUUID());
        categoryEntity2.setUsername(username);
        categoryEntity2.setName("Категория 3");
        categoryEntity2.setArchived(true);

        when(categoryRepository
                .findAllByUsernameOrderByName(eq(username)))
                .thenReturn(List.of(categoryEntity1, categoryEntity2, thirdCat));

        CategoryService categoryService = new CategoryService(categoryRepository);
        List<CategoryJson> result = categoryService.getAllCategories(username, true);

        assertEquals(2, result.size());
        assertFalse(result.getFirst().archived());
        assertFalse(result.getLast().archived());
    }

    @Test
    void updateCategoryShouldThrowTooManyCategoriesExceptionIfMaxCategoriesSizeIsExceeded(@NonNull @Mock CategoryRepository categoryRepository) {
        final long categoryCount = 8;

        mainCategoryEntity.setArchived(true);

        final CategoryJson categoryJson = new CategoryJson(
                mainCategoryEntity.getId(),
                mainCategoryEntity.getName(),
                username,
                false
        );

        when(categoryRepository
                .findByUsernameAndId(eq(
                                username),
                        eq(mainCategoryEntity.getId())
                ))
                .thenReturn(Optional.of(mainCategoryEntity));
        when(categoryRepository
                .countByUsernameAndArchived(
                        eq(username),
                        eq(false))
        )
                .thenReturn(categoryCount);

        CategoryService categoryService = new CategoryService(categoryRepository);

        final TooManyCategoriesException exception = assertThrows(TooManyCategoriesException.class,
                () -> categoryService.update(categoryJson));
        assertEquals("Can`t unarchive category for user: '" + username + "'", exception.getMessage());
    }

    @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIVed"})
    @ParameterizedTest
    void saveShouldThrowExceptionInCaseOfArchivedCategoryName(String categoryName, @Mock CategoryRepository categoryRepository) {

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                UUID.randomUUID(),
                categoryName,
                username,
                true
        );

        InvalidCategoryNameException ex = assertThrows(
                InvalidCategoryNameException.class,
                () -> categoryService.save(categoryJson)
        );
        Assertions.assertEquals(
                "Can`t add category with name: '" + categoryName + "'", ex.getMessage());
    }

    @Test
    void saveShouldThrowExceptionInCaseOfMaxCategoriesSizeIsExceeded(@NonNull @Mock CategoryRepository categoryRepository) {
        final long categoryCount = 8;

        final CategoryJson catJson = new CategoryJson(
                mainCategoryEntity.getId(),
                mainCategoryEntity.getName(),
                username,
                false
        );

        when(categoryRepository
                .countByUsernameAndArchived(eq(username), eq(false)))
                .thenReturn(categoryCount);

        CategoryService categoryService = new CategoryService(categoryRepository);

        final TooManyCategoriesException ex = assertThrows(TooManyCategoriesException.class,
                () -> categoryService.save(catJson));
        assertEquals("Can`t add over than 8 categories for user: '" + username + "'", ex.getMessage());
    }

}