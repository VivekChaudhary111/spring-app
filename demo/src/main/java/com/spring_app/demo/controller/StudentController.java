package com.spring_app.demo.controller;

import com.spring_app.demo.dto.ApiResponse;
import com.spring_app.demo.model.Student;
import com.spring_app.demo.service.StudentService;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/students")
public class StudentController {
	private final StudentService studentService;

	public StudentController(StudentService studentService) {
		this.studentService = studentService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse> createStudent(@RequestBody Student student) {
		if (!isValid(student)) {
			return ResponseEntity.badRequest()
				.body(new ApiResponse(false, "Name, email, and course are required.", null));
		}
		try {
			Student createdStudent = studentService.create(student);
			return ResponseEntity.created(URI.create("/students/" + createdStudent.getId()))
				.body(new ApiResponse(true, "Student created successfully.", createdStudent));
		} catch (DataAccessException ex) {
			return ResponseEntity.internalServerError()
				.body(new ApiResponse(false, "Database error while creating student.", null));
		}
	}

	@GetMapping
	public ResponseEntity<ApiResponse> getAllStudents() {
		List<Student> students = studentService.findAll();
		return ResponseEntity.ok(new ApiResponse(true, "Students retrieved successfully.", students));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse> getStudentById(@PathVariable Integer id) {
		Optional<Student> student = studentService.findById(id);
		return student
			.map(value -> ResponseEntity.ok(new ApiResponse(true, "Student found.", value)))
			.orElseGet(() -> ResponseEntity.status(404)
				.body(new ApiResponse(false, "Student not found.", null)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse> updateStudent(@PathVariable Integer id, @RequestBody Student student) {
		if (!isValid(student)) {
			return ResponseEntity.badRequest()
				.body(new ApiResponse(false, "Name, email, and course are required.", null));
		}
		student.setId(id);
		try {
			boolean updated = studentService.update(id, student);
			if (!updated) {
				return ResponseEntity.status(404)
					.body(new ApiResponse(false, "Student not found.", null));
			}
			return studentService.findById(id)
				.map(value -> ResponseEntity.ok(new ApiResponse(true, "Student updated successfully.", value)))
				.orElseGet(() -> ResponseEntity.status(404)
					.body(new ApiResponse(false, "Student not found.", null)));
		} catch (DataAccessException ex) {
			return ResponseEntity.internalServerError()
				.body(new ApiResponse(false, "Database error while updating student.", null));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse> deleteStudent(@PathVariable Integer id) {
		try {
			boolean deleted = studentService.deleteById(id);
			if (!deleted) {
				return ResponseEntity.status(404)
					.body(new ApiResponse(false, "Student not found.", null));
			}
			return ResponseEntity.ok(new ApiResponse(true, "Student deleted successfully.", null));
		} catch (DataAccessException ex) {
			return ResponseEntity.internalServerError()
				.body(new ApiResponse(false, "Database error while deleting student.", null));
		}
	}

	private boolean isValid(Student student) {
		if (student == null) {
			return false;
		}
		return !isBlank(student.getName())
			&& !isBlank(student.getEmail())
			&& !isBlank(student.getCourse());
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
