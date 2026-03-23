package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.entity.Truyen;
import com.truyen.webtruyen.entity.TruyenGenre;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.enums.PublishStatus;
import com.truyen.webtruyen.entity.enums.StoryStatus;
import com.truyen.webtruyen.entity.enums.UserRole;
import com.truyen.webtruyen.entity.id.TruyenGenreId;
import com.truyen.webtruyen.exception.GlobalExceptionHandler;
import com.truyen.webtruyen.repository.ChapterRepository;
import com.truyen.webtruyen.repository.CommentRepository;
import com.truyen.webtruyen.repository.FavoriteRepository;
import com.truyen.webtruyen.repository.GenreRepository;
import com.truyen.webtruyen.repository.RatingRepository;
import com.truyen.webtruyen.repository.ReadingHistoryRepository;
import com.truyen.webtruyen.repository.TruyenGenreRepository;
import com.truyen.webtruyen.repository.TruyenRepository;
import com.truyen.webtruyen.security.jwt.JwtService;
import com.truyen.webtruyen.service.CurrentUserService;
import com.truyen.webtruyen.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TruyenController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration.class,
                org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration.class,
                org.springframework.boot.transaction.autoconfigure.TransactionAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class})
class TruyenControllerDetailsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TruyenRepository truyenRepository;

    @MockitoBean
    private CurrentUserService currentUserService;

    @MockitoBean
    private ChapterRepository chapterRepository;

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private FavoriteRepository favoriteRepository;

    @MockitoBean
    private RatingRepository ratingRepository;

    @MockitoBean
    private TruyenGenreRepository truyenGenreRepository;

    @MockitoBean
    private ReadingHistoryRepository readingHistoryRepository;

    @MockitoBean
    private GenreRepository genreRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createTruyenWithGenresCreatesLinks() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setRole(UserRole.USER);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(genreRepository.countByIdIn(eq(Set.of(1L, 3L)))).thenReturn(2L);

        when(truyenRepository.save(any(Truyen.class))).thenAnswer(inv -> {
            Truyen t = inv.getArgument(0);
            t.setId(99L);
            return t;
        });

        mockMvc.perform(post("/api/truyen")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Test",
                                  "slug": "test",
                                  "genreIds": [1, 3],
                                  "publishStatus": "ONGOING"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.authorId").value(1))
                .andExpect(jsonPath("$.authorName").value("alice"));

        verify(truyenGenreRepository, times(1)).saveAll(any());
    }

    @Test
    void updateDetailsReplacesGenresAndUpdatesFields() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.USER);

        Truyen existing = new Truyen();
        existing.setId(10L);
        existing.setAuthorId(1L);
        existing.setTitle("Old title");
        existing.setSlug("old-slug");
        existing.setStatus(StoryStatus.APPROVED);
        existing.setPublishStatus(PublishStatus.ONGOING);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(currentUserService.isAdmin(user)).thenReturn(false);
        when(truyenRepository.findById(10L)).thenReturn(Optional.of(existing));

        when(genreRepository.countByIdIn(eq(Set.of(2L, 3L)))).thenReturn(2L);

        TruyenGenre g2 = new TruyenGenre();
        g2.setId(new TruyenGenreId(10L, 2L));
        TruyenGenre g3 = new TruyenGenre();
        g3.setId(new TruyenGenreId(10L, 3L));
        when(truyenGenreRepository.findByIdTruyenId(10L)).thenReturn(List.of(g2, g3));

        when(truyenRepository.save(any(Truyen.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(patch("/api/truyen/10/details")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "New title",
                                  "description": "Mo ta",
                                  "coverImage": "https://example.com/cover.jpg",
                                  "publishStatus": "COMPLETED",
                                  "genreIds": [2, 3]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.truyen.id").value(10))
                .andExpect(jsonPath("$.truyen.title").value("New title"))
                .andExpect(jsonPath("$.truyen.publishStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.truyen.status").value("PENDING"))
                .andExpect(jsonPath("$.genreIds.length()").value(2))
                .andExpect(jsonPath("$.genreIds[0]").value(2))
                .andExpect(jsonPath("$.genreIds[1]").value(3));
    }

    @Test
    void updateDetailsForbiddenWhenNotOwnerOrAdmin() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setRole(UserRole.USER);

        Truyen existing = new Truyen();
        existing.setId(10L);
        existing.setAuthorId(1L);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(currentUserService.isAdmin(user)).thenReturn(false);
        when(truyenRepository.findById(10L)).thenReturn(Optional.of(existing));

        mockMvc.perform(patch("/api/truyen/10/details")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "description": "x" }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("You cannot edit this story"));
    }
}
