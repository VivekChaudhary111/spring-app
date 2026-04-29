package com.spring_app.demo.service;

import com.spring_app.demo.model.Student;
import com.spring_app.demo.repository.StudentRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class StudentService {
	private final StudentRepository studentRepository;

	public StudentService(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}

	public Student create(Student student) {
		return studentRepository.create(student);
	}

	public List<Student> findAll() {
		return studentRepository.findAll();
	}

	public Optional<Student> findById(Integer id) {
		return studentRepository.findById(id);
	}

	public boolean update(Integer id, Student student) {
		return studentRepository.update(id, student);
	}

	public boolean deleteById(Integer id) {
		return studentRepository.deleteById(id);
	}
}
