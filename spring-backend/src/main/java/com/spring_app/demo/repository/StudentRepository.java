package com.spring_app.demo.repository;

import com.spring_app.demo.model.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class StudentRepository {
	private final JdbcTemplate jdbcTemplate;

	public StudentRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private final RowMapper<Student> studentRowMapper = (rs, rowNum) -> new Student(
		rs.getInt("id"),
		rs.getString("name"),
		rs.getString("email"),
		rs.getString("course")
	);

	public Student create(Student student) {
		String sql = "INSERT INTO students (name, email, course) VALUES (?, ?, ?) RETURNING id";
		Integer id = jdbcTemplate.queryForObject(
			sql,
			Integer.class,
			student.getName(),
			student.getEmail(),
			student.getCourse()
		);
		student.setId(id);
		return student;
	}

	public List<Student> findAll() {
		String sql = "SELECT id, name, email, course FROM students ORDER BY id";
		return jdbcTemplate.query(sql, studentRowMapper);
	}

	public Optional<Student> findById(Integer id) {
		String sql = "SELECT id, name, email, course FROM students WHERE id = ?";
		List<Student> results = jdbcTemplate.query(sql, studentRowMapper, id);
		return results.stream().findFirst();
	}

	public boolean update(Integer id, Student student) {
		String sql = "UPDATE students SET name = ?, email = ?, course = ? WHERE id = ?";
		int updatedRows = jdbcTemplate.update(sql,
			student.getName(),
			student.getEmail(),
			student.getCourse(),
			id
		);
		return updatedRows > 0;
	}

	public boolean deleteById(Integer id) {
		String sql = "DELETE FROM students WHERE id = ?";
		int deletedRows = jdbcTemplate.update(sql, id);
		return deletedRows > 0;
	}
}
