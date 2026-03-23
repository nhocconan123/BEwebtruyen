package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.entity.Chapter;
import com.truyen.webtruyen.entity.Truyen;
import com.truyen.webtruyen.entity.enums.StoryStatus;
import com.truyen.webtruyen.exception.GlobalExceptionHandler;
import com.truyen.webtruyen.repository.ChapterRepository;
import com.truyen.webtruyen.repository.TruyenRepository;
import com.truyen.webtruyen.security.jwt.JwtService;
import com.truyen.webtruyen.service.CurrentUserService;
import com.truyen.webtruyen.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReadController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class})
class ReadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TruyenRepository truyenRepository;

    @MockitoBean
    private ChapterRepository chapterRepository;

    @MockitoBean
    private CurrentUserService currentUserService;

    // Needed because SecurityConfig wires JwtAuthenticationFilter as a component.
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void readFirstChapterAllowsNullPrev() throws Exception {
        Truyen truyen = new Truyen();
        truyen.setId(1L);
        truyen.setSlug("nhat-kiem-van-co");
        truyen.setTitle("Nhat Kiem Van Co");
        truyen.setStatus(StoryStatus.APPROVED);

        Chapter chapter = new Chapter();
        chapter.setId(11L);
        chapter.setTruyenId(1L);
        chapter.setChapterNumber(1);
        chapter.setTitle("Chuong 1");
        chapter.setContent("Noi dung");

        when(truyenRepository.findBySlug("nhat-kiem-van-co")).thenReturn(Optional.of(truyen));
        when(chapterRepository.findByTruyenIdAndChapterNumber(1L, 1)).thenReturn(Optional.of(chapter));
        when(chapterRepository.findFirstByTruyenIdAndChapterNumberLessThanOrderByChapterNumberDesc(1L, 1)).thenReturn(Optional.empty());
        when(chapterRepository.findFirstByTruyenIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(1L, 1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/read/nhat-kiem-van-co/chuong/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.story.id").value(1))
                .andExpect(jsonPath("$.chapter.id").value(11))
                .andExpect(jsonPath("$.prevChapterNumber").value(nullValue()))
                .andExpect(jsonPath("$.nextChapterNumber").value(nullValue()));
    }
}
