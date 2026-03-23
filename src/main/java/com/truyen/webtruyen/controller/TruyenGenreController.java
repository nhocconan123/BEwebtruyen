package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.entity.TruyenGenre;
import com.truyen.webtruyen.entity.id.TruyenGenreId;
import com.truyen.webtruyen.repository.TruyenGenreRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/truyen-genres")
public class TruyenGenreController {

    private final TruyenGenreRepository truyenGenreRepository;

    public TruyenGenreController(TruyenGenreRepository truyenGenreRepository) {
        this.truyenGenreRepository = truyenGenreRepository;
    }

    @GetMapping("/truyen/{truyenId}")
    public List<TruyenGenre> getByTruyen(@PathVariable Long truyenId) {
        return truyenGenreRepository.findByIdTruyenId(truyenId);
    }

    @PostMapping
    public TruyenGenre create(@RequestParam Long truyenId, @RequestParam Long genreId) {
        TruyenGenre truyenGenre = new TruyenGenre();
        truyenGenre.setId(new TruyenGenreId(truyenId, genreId));
        return truyenGenreRepository.save(truyenGenre);
    }

    @DeleteMapping
    public void delete(@RequestParam Long truyenId, @RequestParam Long genreId) {
        truyenGenreRepository.deleteById(new TruyenGenreId(truyenId, genreId));
    }
}
