package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.entity.Genre;
import com.truyen.webtruyen.repository.GenreRepository;
import com.truyen.webtruyen.util.SlugUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreRepository genreRepository;

    public GenreController(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @GetMapping
    public List<Genre> getAll() {
        return genreRepository.findAll();
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Genre not found"));
    }

    @GetMapping("/slug/{slug}")
    public Genre getBySlug(@PathVariable String slug) {
        String wanted = slug == null ? "" : slug.trim().toLowerCase();
        return genreRepository.findAll().stream()
                .filter(g -> SlugUtil.slugify(g.getName()).equals(wanted))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Genre not found"));
    }

    @PostMapping
    public Genre create(@RequestBody Map<String, String> payload) {
        String name = normalizeName(payload.get("name"));
        if (name == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Genre name is required");
        }
        if (genreRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(CONFLICT, "Genre name already exists");
        }

        Genre genre = new Genre();
        genre.setName(name);
        return genreRepository.save(genre);
    }

    @PutMapping("/{id}")
    public Genre update(@PathVariable Long id, @RequestBody Genre genre) {
        if (!genreRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Genre not found");
        }
        String normalizedName = normalizeName(genre.getName());
        if (normalizedName == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Genre name is required");
        }
        if (genreRepository.existsByNameIgnoreCase(normalizedName)) {
            Genre existingGenre = genreRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Genre not found"));
            if (!existingGenre.getName().equalsIgnoreCase(normalizedName)) {
                throw new ResponseStatusException(CONFLICT, "Genre name already exists");
            }
        }

        genre.setId(id);
        genre.setName(normalizedName);
        return genreRepository.save(genre);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        if (!genreRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Genre not found");
        }
        genreRepository.deleteById(id);
    }

    private String normalizeName(String name) {
        if (name == null) {
            return null;
        }
        String trimmed = name.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
