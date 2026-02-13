package com.delimce.aibroker.infrastructure.controllers.llm;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delimce.aibroker.application.llm.ModelListService;
import com.delimce.aibroker.domain.dto.ApiResponse;
import com.delimce.aibroker.infrastructure.controllers.BaseController;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/llm")
public class ModelController extends BaseController {

    private final ModelListService modelListService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse> list() {
        try {
            return ResponseEntity.ok(responseOk(modelListService.execute()));
        } catch (IllegalArgumentException e) {
            return illegalArgumentExceptionResponse(e);
        } catch (Exception e) {
            return unhandledExceptionResponse(e);
        }
    }

}
