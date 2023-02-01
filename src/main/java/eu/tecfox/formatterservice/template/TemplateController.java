/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.tecfox.formatterservice.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.tecfox.formatterservice.exception.ApiRequestException;
import eu.tecfox.formatterservice.exception.BindingResultErrorFormatter;
import eu.tecfox.formatterservice.template.models.Template;
import eu.tecfox.formatterservice.template.models.ValidTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

// import static org.mockito.Mockito.description;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that holds all endpoints related to templates.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/template")
@Api(tags = {"Template Controller"})
@Tag(name = "Template Controller", description = "All endpoints related to the Template object.")
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get the template.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "The template."),
        @ApiResponse(code = 404, message = "No template found."),
    })
    public ResponseEntity<Template> getTemplate() {
        Template template = templateService.getTemplate().orElseThrow(() -> new ApiRequestException("No template found."));
        return ResponseEntity.ok().body(template);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Replace template with new one.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "The saved template.")
    })
    public ResponseEntity<Template> saveTemplate(@RequestBody @Validated(ValidTemplate.class) Template template, BindingResult bindingResult)
        throws JsonProcessingException {
        if (bindingResult.hasErrors()) {
            throw new ApiRequestException(BindingResultErrorFormatter.getErrorMessagesAsJson(bindingResult));
        }
        return ResponseEntity.ok().body(templateService.saveTemplate(template));
    }
}
