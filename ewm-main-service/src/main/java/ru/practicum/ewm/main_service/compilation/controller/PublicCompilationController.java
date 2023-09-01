package ru.practicum.ewm.main_service.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.compilation.dto.CompilationDto;
import ru.practicum.ewm.main_service.compilation.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilationsEvents(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                      @RequestParam(name = "from", defaultValue = "0") int from,
                                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("MainService: Get compilations of events, pinned={}, from={}, size={}", pinned, from, size);
        return compilationService.findCompilationsEvents(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable("compId") long compId) {
        log.info("MainService: Get compilation by Id, id={}", compId);
        return compilationService.findCompilationById(compId);
    }

}