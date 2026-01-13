# 📘 CodeStream

**CodeStream** adalah aplikasi pembelajaran Informatika berbasis **Android** yang terinspirasi dari platform seperti *Coursera*. Aplikasi ini memungkinkan pengguna untuk belajar melalui kursus berbentuk **video** dan **dokumen**, mengelola kelas, wishlist, hingga mendapatkan **sertifikat** setelah menyelesaikan pembelajaran.

---

## 🚀 Fitur Utama

### 🔐 Autentikasi
- Login menggunakan **Email & Password**
- Data pengguna disimpan menggunakan **SQLite Database**

### 🏠 Home Screen
- Dashboard utama aplikasi
- **Sidebar Navigation** untuk berpindah fitur

### 🔎 Find Class
- Pencarian kursus
- Daftar **Popular Course**

### ❤️ Wishlist
- Menyimpan kursus favorit
- Proses **payment**
- Akses **Course Dashboard** setelah kursus dibeli

### 📚 Course & Learning
- Kursus yang sudah dan belum diambil
- Dua tipe materi pembelajaran:
  - 🎥 Video
  - 📄 Dokumen
- Progress pembelajaran

### 🏆 Sertifikat
- Sertifikat otomatis didapatkan setelah menyelesaikan kelas
- Sertifikat dapat dilihat di menu **Profile → Certificate**

### 👤 Profile
- Informasi pengguna
- Pengaturan akun
- Sertifikat

---

## 🛠️ Teknologi yang Digunakan

- Android Studio
- Kotlin & Java
- SQLite Database
- Material Design
- Jetpack Components

---

## 🗂️ Alur Aplikasi

Login
└── Home
├── Find Class
│ ├── Search Course
│ └── Popular Course
├── Wishlist
│ └── Payment
│ └── Course Dashboard
├── Learn
│ └── Video / Document
└── Profile
├── Settings
└── Certificate


---

## 📦 Instalasi

1. Clone repository:
   ```bash
   git clone https://github.com/budipermana16/CodeStream.git
