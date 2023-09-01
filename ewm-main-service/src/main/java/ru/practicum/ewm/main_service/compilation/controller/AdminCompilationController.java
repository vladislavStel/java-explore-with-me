package ru.practicum.ewm.main_service.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.compilation.dto.CompilationDto;
import ru.practicum.ewm.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main_service.compilation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("MainService: Admin create compilation, compilationDto={}", newCompilationDto);
        return compilationService.saveCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") long compId) {
        log.info("MainService: Admin delete compilation, compilationId={}", compId);
        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto patchCompilation(@PathVariable("compId") long compId,
                                           @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("MainService: Admin patch compilation, compilationId={}, " +
                "patchCompilationDto={}", compId, updateCompilationRequest);
        return compilationService.updateCompilation(compId, updateCompilationRequest);
    }

}