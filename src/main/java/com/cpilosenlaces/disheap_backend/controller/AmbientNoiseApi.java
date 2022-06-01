package com.cpilosenlaces.disheap_backend.controller;

import java.util.List;
import java.util.UUID;

import com.cpilosenlaces.disheap_backend.exception.BadRequestException;
import com.cpilosenlaces.disheap_backend.exception.ErrorResponse;
import com.cpilosenlaces.disheap_backend.exception.NotFoundException;
import com.cpilosenlaces.disheap_backend.model.AmbientNoise;
import com.cpilosenlaces.disheap_backend.model.dto.MeasureDTO;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Ambient noises", description = "Ambient noise API")
@Validated
@RestController
@RequestMapping(value = "/ambient-noises", produces = { MediaType.APPLICATION_JSON_VALUE })
@SecurityScheme(name = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public interface AmbientNoiseApi {

	@SecurityRequirements
	@SecurityRequirement(name = "bearer")
	@Operation(summary = "Get all ambient noises", operationId = "getAllAmbientNoise")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping
	public ResponseEntity<List<AmbientNoise>> getAll();

	@SecurityRequirements
	@SecurityRequirement(name = "bearer")
	@Operation(summary = "Get ambient noise by ID", operationId = "getById")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping("/{id}")
	public ResponseEntity<AmbientNoise> getById(
			@Parameter(description = "Ambient noise ID", required = true) @PathVariable UUID id)
			throws NotFoundException;

	@SecurityRequirements
	@SecurityRequirement(name = "bearer")
	@Operation(summary = "Get Ambient noises by disband ID", operationId = "getAmbientNoisesByDisbandId")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping("/disbands/{disbandId}")
	public ResponseEntity<List<AmbientNoise>> getByDisbandId(
			@Parameter(description = "Disband ID", required = true) @PathVariable UUID userId);

	@SecurityRequirements
	@SecurityRequirement(name = "bearer")
	@Operation(summary = "Get ambient noises by date between and disband ID", operationId = "getAmbientNoisesByDateBetweenAndDisbandId")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping("/date/between/disband/{disbandId}")
	public ResponseEntity<List<AmbientNoise>> getByDateBetweenAndDisbandId(
			@Parameter(description = "Min date", required = true) @RequestParam(value = "min date") long minDate,
			@Parameter(description = "Max date", required = true) @RequestParam(value = "max date") long maxDate,
			@Parameter(description = "Disband ID", required = true) @PathVariable UUID disbandId);

	@SecurityRequirements
	@SecurityRequirement(name = "bearer")
	@Operation(summary = "Get ambient noises by date between", operationId = "getAmbientNoisesByDateBetween")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping("/date/between")
	public ResponseEntity<List<AmbientNoise>> getByDateBetween(
			@Parameter(description = "Min date", required = true) @RequestParam(value = "min date") long minDate,
			@Parameter(description = "Max date", required = true) @RequestParam(value = "max date") long maxDate);

	@SecurityRequirements(value = {})
	@Operation(summary = "Save ambient noise", operationId = "saveAmbientNoise")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "CREATED"),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping
	public ResponseEntity<AmbientNoise> save(
			@Parameter(description = "Measure object", required = true) @RequestBody MeasureDTO measureDTO)
			throws NotFoundException, BadRequestException;

	@SecurityRequirements
	@SecurityRequirement(name = "bearer")
	@Operation(summary = "Delete ambient noises by disband ID", operationId = "deleteAmbientNoisesByDisbandId")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@DeleteMapping("/disbands/{disbandId}")
	public ResponseEntity<List<AmbientNoise>> deleteByDisbandId(
			@Parameter(description = "Disband id", required = true) @PathVariable UUID disbandId)
			throws NotFoundException;
}
