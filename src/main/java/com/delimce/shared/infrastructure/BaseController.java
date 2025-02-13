package com.delimce.shared.infrastructure;

import org.springframework.boot.jackson.JsonComponent;

import org.springframework.web.bind.annotation.RestController;

import com.delimce.shared.domain.ControllerInterface;

import org.springframework.web.bind.annotation.RequestMapping;

import io.micrometer.common.lang.Nullable;



@RestController
@RequestMapping("/v1")
public class BaseController implements ControllerInterface {

/*     public JsonComponent responseOk(Object data, int status, @Nullable String msg) {

    } */

}
