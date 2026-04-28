/**
 * ============================================
 * ENROLLMENT SYSTEM — APPLICATION LOGIC
 * SPA controller consuming REST API /api/v1/*
 * ============================================
 */

const API = '/api/v1';

// ── State ──────────────────────────────────────
let currentPage = 'dashboard';
let studentsCache = [];
let coursesCache = [];
let professorsCache = [];

// ── Init ───────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  setupNavigation();
  navigateTo('dashboard');
});

// ── Navigation ─────────────────────────────────
function setupNavigation() {
  document.querySelectorAll('.nav-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      navigateTo(btn.dataset.page);
    });
  });
}

function navigateTo(page) {
  currentPage = page;

  // Update nav buttons
  document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
  const activeBtn = document.querySelector(`.nav-btn[data-page="${page}"]`);
  if (activeBtn) activeBtn.classList.add('active');

  // Update pages
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  const activePage = document.getElementById(`page-${page}`);
  if (activePage) activePage.classList.add('active');

  // Load data
  loadPageData(page);
}

async function loadPageData(page) {
  switch (page) {
    case 'dashboard':  await loadDashboard(); break;
    case 'students':   await loadStudents();  break;
    case 'courses':    await loadCourses();   break;
    case 'professors': await loadProfessors(); break;
    case 'enrollments': await loadEnrollments(); break;
    case 'teaching':   await loadTeaching();  break;
  }
}

// ── Dashboard ──────────────────────────────────
async function loadDashboard() {
  try {
    const [students, courses, professors, enrollments] = await Promise.all([
      fetchJSON(`${API}/students`),
      fetchJSON(`${API}/courses`),
      fetchJSON(`${API}/professors`),
      fetchJSON(`${API}/enrollments`),
    ]);

    document.getElementById('stat-students').textContent = students.length;
    document.getElementById('stat-courses').textContent = courses.length;
    document.getElementById('stat-professors').textContent = professors.length;
    document.getElementById('stat-enrollments').textContent = enrollments.length;

    // Recent enrollments
    renderRecentEnrollments(enrollments.slice(-5).reverse());
  } catch (err) {
    showToast('Error loading dashboard data', 'error');
  }
}

function renderRecentEnrollments(enrollments) {
  const container = document.getElementById('recent-enrollments');
  if (!enrollments.length) {
    container.innerHTML = `
      <div class="empty-state">
        <div class="icon">📋</div>
        <p>No enrollments yet</p>
      </div>`;
    return;
  }

  container.innerHTML = `
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>Student</th>
            <th>Course</th>
            <th>Price</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
          ${enrollments.map(e => `
            <tr>
              <td>${escapeHtml(e.studentName)}</td>
              <td>${escapeHtml(e.courseName)}</td>
              <td class="text-accent">$${parseFloat(e.price).toFixed(2)}</td>
              <td>${formatDate(e.enrolledAt)}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    </div>`;
}

// ── Students ───────────────────────────────────
async function loadStudents() {
  const container = document.getElementById('students-content');
  container.innerHTML = spinnerHTML();
  try {
    const students = await fetchJSON(`${API}/students`);
    studentsCache = students;

    if (!students.length) {
      container.innerHTML = emptyHTML('🎓', 'No students found');
      return;
    }

    container.innerHTML = `
      <div class="cards-grid">
        ${students.map(s => `
          <div class="data-card">
            <div class="data-card-header">
              <div class="data-card-title">👤 ${escapeHtml(s.firstName)} ${escapeHtml(s.lastName)}</div>
              <span class="data-card-badge badge-primary">Sem ${s.semester}</span>
            </div>
            <div class="data-card-body">
              <div class="data-field">
                <span class="data-field-label">ID</span>
                <span class="data-field-value">#${s.id}</span>
              </div>
              <div class="data-field">
                <span class="data-field-label">Age</span>
                <span class="data-field-value">${s.age} years</span>
              </div>
              <div class="data-field">
                <span class="data-field-label">Credits</span>
                <span class="data-field-value">${s.credits}</span>
              </div>
            </div>
          </div>
        `).join('')}
      </div>`;
  } catch (err) {
    container.innerHTML = emptyHTML('⚠️', 'Error loading students');
    showToast('Failed to load students', 'error');
  }
}

// ── Courses ────────────────────────────────────
async function loadCourses() {
  const container = document.getElementById('courses-content');
  container.innerHTML = spinnerHTML();
  try {
    const courses = await fetchJSON(`${API}/courses`);
    coursesCache = courses;
    renderCourses(courses, container);
  } catch (err) {
    container.innerHTML = emptyHTML('⚠️', 'Error loading courses');
    showToast('Failed to load courses', 'error');
  }
}

function renderCourses(courses, container) {
  if (!courses.length) {
    container.innerHTML = emptyHTML('📚', 'No courses found');
    return;
  }

  container.innerHTML = `
    <div class="cards-grid">
      ${courses.map(c => `
        <div class="data-card">
          <div class="data-card-header">
            <div class="data-card-title">📘 ${escapeHtml(c.name)}</div>
            <span class="data-card-badge badge-accent">ID #${c.id}</span>
          </div>
          <div class="data-card-body">
            <div class="data-field">
              <span class="data-field-label">Location</span>
              <span class="data-field-value">📍 ${escapeHtml(c.location)}</span>
            </div>
          </div>
        </div>
      `).join('')}
    </div>`;
}

async function searchCourses() {
  const query = document.getElementById('course-search').value.trim();
  const container = document.getElementById('courses-content');

  if (!query) {
    await loadCourses();
    return;
  }

  container.innerHTML = spinnerHTML();
  try {
    const courses = await fetchJSON(`${API}/courses/search?name=${encodeURIComponent(query)}`);
    renderCourses(courses, container);
  } catch (err) {
    container.innerHTML = emptyHTML('⚠️', 'Search failed');
  }
}

// Debounced search
let searchTimeout;
function onCourseSearchInput() {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(searchCourses, 350);
}

// ── Professors ─────────────────────────────────
async function loadProfessors() {
  const container = document.getElementById('professors-content');
  container.innerHTML = spinnerHTML();
  try {
    const professors = await fetchJSON(`${API}/professors`);
    professorsCache = professors;

    if (!professors.length) {
      container.innerHTML = emptyHTML('👨‍🏫', 'No professors found');
      return;
    }

    container.innerHTML = `
      <div class="cards-grid">
        ${professors.map(p => `
          <div class="data-card">
            <div class="data-card-header">
              <div class="data-card-title">🎓 ${escapeHtml(p.firstName)} ${escapeHtml(p.lastName)}</div>
              <span class="data-card-badge ${p.employmentType === 'Full-Time' ? 'badge-success' : 'badge-warning'}">${escapeHtml(p.employmentType)}</span>
            </div>
            <div class="data-card-body">
              <div class="data-field">
                <span class="data-field-label">ID</span>
                <span class="data-field-value">#${p.id}</span>
              </div>
              <div class="data-field">
                <span class="data-field-label">Degree</span>
                <span class="data-field-value">${escapeHtml(p.degree)}</span>
              </div>
            </div>
          </div>
        `).join('')}
      </div>`;
  } catch (err) {
    container.innerHTML = emptyHTML('⚠️', 'Error loading professors');
    showToast('Failed to load professors', 'error');
  }
}

// ── Enrollments ────────────────────────────────
async function loadEnrollments() {
  // Populate dropdowns
  await populateEnrollmentForm();

  const container = document.getElementById('enrollments-content');
  container.innerHTML = spinnerHTML();
  try {
    const enrollments = await fetchJSON(`${API}/enrollments`);
    renderEnrollmentsTable(enrollments, container);
  } catch (err) {
    container.innerHTML = emptyHTML('⚠️', 'Error loading enrollments');
    showToast('Failed to load enrollments', 'error');
  }
}

function renderEnrollmentsTable(enrollments, container) {
  if (!enrollments.length) {
    container.innerHTML = emptyHTML('📋', 'No enrollments found');
    return;
  }

  container.innerHTML = `
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>Student</th>
            <th>Course</th>
            <th>Price</th>
            <th>Enrolled At</th>
          </tr>
        </thead>
        <tbody>
          ${enrollments.map(e => `
            <tr>
              <td><strong>${escapeHtml(e.studentName)}</strong></td>
              <td>${escapeHtml(e.courseName)}</td>
              <td class="text-accent">$${parseFloat(e.price).toFixed(2)}</td>
              <td>${formatDate(e.enrolledAt)}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    </div>`;
}

async function populateEnrollmentForm() {
  try {
    if (!studentsCache.length) {
      studentsCache = await fetchJSON(`${API}/students`);
    }
    if (!coursesCache.length) {
      coursesCache = await fetchJSON(`${API}/courses`);
    }

    const studentSelect = document.getElementById('enroll-student');
    const courseSelect = document.getElementById('enroll-course');

    studentSelect.innerHTML = `<option value="">— Select Student —</option>` +
      studentsCache.map(s => `<option value="${s.id}">${s.firstName} ${s.lastName} (ID: ${s.id})</option>`).join('');

    courseSelect.innerHTML = `<option value="">— Select Course —</option>` +
      coursesCache.map(c => `<option value="${c.id}">${c.name} — ${c.location} (ID: ${c.id})</option>`).join('');

  } catch (err) {
    showToast('Error loading form data', 'error');
  }
}

async function submitEnrollment(event) {
  event.preventDefault();

  const studentId = document.getElementById('enroll-student').value;
  const courseId = document.getElementById('enroll-course').value;

  if (!studentId || !courseId) {
    showToast('Please select both a student and a course', 'error');
    return;
  }

  const submitBtn = document.getElementById('enroll-submit');
  submitBtn.disabled = true;
  submitBtn.textContent = 'Enrolling...';

  try {
    const result = await fetchJSON(`${API}/enrollments`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        studentId: parseInt(studentId),
        courseId: parseInt(courseId)
      })
    });

    showToast(`✅ ${result.studentName} enrolled in ${result.courseName} — $${parseFloat(result.price).toFixed(2)}`, 'success');

    // Reset form & reload
    document.getElementById('enroll-student').value = '';
    document.getElementById('enroll-course').value = '';
    await loadEnrollments();

  } catch (err) {
    const msg = err.detail || err.message || 'Enrollment failed';
    showToast(msg, 'error');
  } finally {
    submitBtn.disabled = false;
    submitBtn.textContent = '✨ Enroll Student';
  }
}

// Filter enrollments by course name
async function filterEnrollments() {
  const query = document.getElementById('enrollment-filter').value.trim();
  const container = document.getElementById('enrollments-content');
  container.innerHTML = spinnerHTML();

  try {
    const url = query
      ? `${API}/enrollments?courseName=${encodeURIComponent(query)}`
      : `${API}/enrollments`;

    const enrollments = await fetchJSON(url);
    renderEnrollmentsTable(enrollments, container);
  } catch (err) {
    container.innerHTML = emptyHTML('⚠️', 'Error filtering');
  }
}

let filterTimeout;
function onEnrollmentFilterInput() {
  clearTimeout(filterTimeout);
  filterTimeout = setTimeout(filterEnrollments, 350);
}

// ── Teaching ───────────────────────────────────
async function loadTeaching() {
  const container = document.getElementById('teaching-content');
  container.innerHTML = spinnerHTML();
  try {
    const teachings = await fetchJSON(`${API}/teaching`);

    if (!teachings.length) {
      container.innerHTML = emptyHTML('👨‍🏫', 'No teaching assignments found');
      return;
    }

    container.innerHTML = `
      <div class="cards-grid">
        ${teachings.map(t => `
          <div class="data-card">
            <div class="data-card-header">
              <div class="data-card-title">📖 ${escapeHtml(t.courseName)}</div>
              <span class="data-card-badge badge-success">Active</span>
            </div>
            <div class="data-card-body">
              <div class="data-field">
                <span class="data-field-label">Professor</span>
                <span class="data-field-value">🎓 ${escapeHtml(t.professorName)}</span>
              </div>
              <div class="data-field">
                <span class="data-field-label">Professor ID</span>
                <span class="data-field-value">#${t.professorId}</span>
              </div>
              <div class="data-field">
                <span class="data-field-label">Course ID</span>
                <span class="data-field-value">#${t.courseId}</span>
              </div>
            </div>
          </div>
        `).join('')}
      </div>`;
  } catch (err) {
    container.innerHTML = emptyHTML('⚠️', 'Error loading teaching data');
    showToast('Failed to load teaching assignments', 'error');
  }
}

// ── Utility Functions ──────────────────────────
async function fetchJSON(url, options = {}) {
  const res = await fetch(url, options);
  const data = await res.json();
  if (!res.ok) {
    throw data;
  }
  return data;
}

function escapeHtml(str) {
  if (!str) return '';
  const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
  return String(str).replace(/[&<>"']/g, c => map[c]);
}

function formatDate(dateStr) {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  return d.toLocaleDateString('en-US', {
    year: 'numeric', month: 'short', day: 'numeric',
    hour: '2-digit', minute: '2-digit'
  });
}

function spinnerHTML() {
  return `<div class="spinner-wrapper"><div class="spinner"></div></div>`;
}

function emptyHTML(icon, msg) {
  return `<div class="empty-state"><div class="icon">${icon}</div><p>${msg}</p></div>`;
}

// ── Toast Notifications ────────────────────────
function showToast(message, type = 'info') {
  const container = document.getElementById('toast-container');
  const icons = { success: '✅', error: '❌', info: 'ℹ️' };

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <span class="toast-icon">${icons[type] || icons.info}</span>
    <span class="toast-message">${escapeHtml(message)}</span>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.classList.add('hiding');
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}
