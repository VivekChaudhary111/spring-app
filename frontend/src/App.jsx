import { useEffect, useState } from 'react'

const API_BASE = 'http://localhost:7000'

const emptyForm = {
  name: '',
  email: '',
  course: '',
}

export default function App() {
  const [students, setStudents] = useState([])
  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [status, setStatus] = useState({ type: 'idle', message: '' })
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    fetchStudents()
  }, [])

  const fetchStudents = async () => {
    setLoading(true)
    try {
      const response = await fetch(`${API_BASE}/students`)
      const payload = await response.json()
      if (!response.ok || payload.success === false) {
        throw new Error(payload.message || 'Failed to load students')
      }
      setStudents(Array.isArray(payload.data) ? payload.data : [])
      setStatus({ type: 'success', message: 'Students loaded.' })
    } catch (error) {
      setStatus({ type: 'error', message: error.message })
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  const resetForm = () => {
    setForm(emptyForm)
    setEditingId(null)
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    if (!form.name || !form.email || !form.course) {
      setStatus({ type: 'error', message: 'Fill in name, email, and course.' })
      return
    }

    const isEdit = editingId !== null
    const url = isEdit ? `${API_BASE}/students/${editingId}` : `${API_BASE}/students`
    const method = isEdit ? 'PUT' : 'POST'

    try {
      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form),
      })
      const payload = await response.json()
      if (!response.ok || payload.success === false) {
        throw new Error(payload.message || 'Request failed')
      }

      setStatus({ type: 'success', message: payload.message })
      resetForm()
      await fetchStudents()
    } catch (error) {
      setStatus({ type: 'error', message: error.message })
    }
  }

  const handleEdit = (student) => {
    setForm({
      name: student.name || '',
      email: student.email || '',
      course: student.course || '',
    })
    setEditingId(student.id)
  }

  const handleDelete = async (id) => {
    try {
      const response = await fetch(`${API_BASE}/students/${id}`, { method: 'DELETE' })
      const payload = await response.json()
      if (!response.ok || payload.success === false) {
        throw new Error(payload.message || 'Delete failed')
      }
      setStatus({ type: 'success', message: payload.message })
      await fetchStudents()
    } catch (error) {
      setStatus({ type: 'error', message: error.message })
    }
  }

  return (
    <div className="page">
      <header className="header">
        <div>
          <p className="eyebrow">Student Manager</p>
          <h1>CRUD dashboard</h1>
          <p className="subtitle">Simple UI to create, edit, and manage students.</p>
        </div>
      </header>

      <main className="content">
        <section className="card">
          <h2>{editingId ? 'Update student' : 'Add student'}</h2>
          <form onSubmit={handleSubmit} className="form">
            <label>
              Name
              <input
                type="text"
                name="name"
                value={form.name}
                onChange={handleChange}
                placeholder="Student name"
              />
            </label>
            <label>
              Email
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="student@example.com"
              />
            </label>
            <label>
              Course
              <input
                type="text"
                name="course"
                value={form.course}
                onChange={handleChange}
                placeholder="Course"
              />
            </label>

            <div className="actions">
              <button type="submit" className="primary">
                {editingId ? 'Update' : 'Create'}
              </button>
              {editingId && (
                <button type="button" className="ghost" onClick={resetForm}>
                  Cancel
                </button>
              )}
            </div>
          </form>
        </section>

        <section className="card">
          <div className="card-header">
            <h2>Students</h2>
            <button type="button" className="ghost" onClick={fetchStudents} disabled={loading}>
              {loading ? 'Loading...' : 'Refresh'}
            </button>
          </div>

          {status.message && (
            <div className={`status ${status.type}`} role="status">
              {status.message}
            </div>
          )}

          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Course</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {students.length === 0 ? (
                  <tr>
                    <td colSpan="5" className="empty">
                      No students yet.
                    </td>
                  </tr>
                ) : (
                  students.map((student) => (
                    <tr key={student.id}>
                      <td>{student.id}</td>
                      <td>{student.name}</td>
                      <td>{student.email}</td>
                      <td>{student.course}</td>
                      <td className="row-actions">
                        <button type="button" className="ghost" onClick={() => handleEdit(student)}>
                          Edit
                        </button>
                        <button type="button" className="danger" onClick={() => handleDelete(student.id)}>
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </div>
  )
}
