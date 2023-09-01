package ru.practicum.ewm.main_service.compilation.service;

import ru.practicum.ewm.main_service.compilation.dto.CompilationDto;
import ru.practicum.ewm.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main_service.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> findCompilationsEvents(Boolean pinned, int from, int size);

    CompilationDto findCompilationById(long compId);

    CompilationDto saveCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(long compId);

    CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateCompilationRequest);

}