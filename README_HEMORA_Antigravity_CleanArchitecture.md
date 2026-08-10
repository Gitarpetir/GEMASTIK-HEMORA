# HEMORA — Mobile App Development Brief (Merged with PRD)
> Dokumen acuan tunggal untuk AI coding agent & tim developer. Baca dokumen ini secara utuh sebelum mengerjakan fitur apa pun. Jangan membangun fitur di luar "FASE 1" tanpa instruksi eksplisit dari tim.

## 1. Project Overview
HEMORA adalah aplikasi mobile Android untuk membantu remaja putri meningkatkan kepatuhan konsumsi Tablet Tambah Darah (TTD).

**Masalah yang diselesaikan:** Rendahnya kepatuhan konsumsi Tablet Tambah Darah (TTD) pada remaja putri di Indonesia, akibat (a) distribusi TTD yang sangat bergantung pada kehadiran di sekolah, dan (b) rendahnya pemahaman remaja terhadap manfaat TTD.
**Solusi:** Aplikasi Android dua-peran — Remaja Putri (mengingat & mencatat konsumsi TTD) dan UKS/Guru (mengatur jadwal & memantau kepatuhan siswi di sekolahnya).
**Konteks:** Dikembangkan untuk Gemastik XIX 2026, Divisi Pengembangan Perangkat Lunak. Terkait SDG 2 (Zero Hunger) dan SDG 3 (Good Health and Well-Being).

**Roles:**
- Remaja Putri
- UKS (Guru/Unit Kesehatan Sekolah)

Satu aplikasi digunakan oleh kedua role dengan fitur dan hak akses berbeda.

**Scope lock:** Jangan menambahkan Puskesmas, orang tua, chat, AI, diagnosis anemia, gamifikasi, kelas, atau fitur lain di luar requirement.

## 2. Technology Stack
| Komponen | Teknologi |
|---|---|
| Platform | Android Native |
| Bahasa | Kotlin |
| UI Framework | Jetpack Compose |
| Arsitektur | Clean Architecture (Data → Domain → Presentation) |
| Dependency Injection | Hilt / Dagger |
| Backend & Auth | Firebase Authentication |
| Database | Cloud Firestore |
| Notifikasi | Firebase Cloud Messaging (FCM) |
| Connectivity | Online / internet required |

Aplikasi tidak ditargetkan untuk publik/Play Store pada tahap awal dan digunakan dalam lingkup sekolah tertentu.

## 3. Clean Architecture

HEMORA **WAJIB** menggunakan Clean Architecture dengan tiga layer. Alur dependency selalu satu arah: `presentation → domain ← data`.

### Presentation
Tanggung jawab: Jetpack Compose UI, Screen, UI State, ViewModel, User interaction, Navigation.
Presentation **dilarang keras memanggil Firebase secara langsung**. Semua akses data lewat UseCase di `domain/`.

### Domain
Tanggung jawab: Business logic, Entity/domain model, Use Case, Repository interface.
Domain **tidak boleh** punya dependency ke Android framework atau Firebase SDK sama sekali — murni Kotlin.

### Data
Tanggung jawab: Repository implementation, Firebase SDK, DTO/data model.
`data/` mengimplementasikan interface yang didefinisikan di `domain/`, bukan sebaliknya.

## 4. Struktur Folder & Status Saat Ini

Setiap fitur baru mengikuti pola folder yang sama persis seperti `auth/` — copy-paste struktur, ganti nama sesuai fitur.

```text
core/
├── di/                     🏗️ struktur dibuat — AppModule.kt, FirebaseModule.kt, NetworkModule.kt
└── utils/                  🏗️ struktur dibuat — Resource.kt, Constants.kt, Extensions.kt

data/
└── auth/                   🏗️ FASE 1 — fondasi, prioritas #1
    ├── dto/                UserDto.kt, RegisterRequestDto.kt
    └── repository/         AuthRepositoryImpl.kt

domain/
├── auth/                   🏗️ FASE 1 — fondasi, prioritas #1
│   ├── repository/         AuthRepository.kt (interface)
│   └── usecase/            LoginUseCase.kt, RegisterUserUseCase.kt
└── model/                  User.kt, Role.kt

presentation/
└── auth/                   🏗️ FASE 1 — fondasi, prioritas #1
    LoginScreen.kt, RegisterScreen.kt, AuthViewModel.kt, AuthState.kt

ui/theme/                   ✅ selesai (auto-generated), tinggal disesuaikan identitas visual HEMORA
```

**Fitur yang BELUM dibuat foldernya** (akan mengikuti pola identik di atas):
- `school/`        → School Code (FR-12)
- `schedule/`       → Jadwal Konsumsi TTD (FR-14)
- `consumption/`     → Reminder, Tracker, Riwayat, Statistik individu (FR-04, 05, 06, 07)
- `monitoring/`      → Dashboard UKS, Daftar Siswi, Monitoring, Statistik sekolah (FR-13, 15, 16, 17)
- `education/`       → Modul Edukasi (FR-08)
- `user/ (profile/)`  → Kelola Profil (FR-03)

**Catatan penting:** folder `auth/` yang sudah ada harus melayani **kedua role** (Remaja Putri & UKS), bukan dibuat modul auth terpisah untuk UKS. Bedanya cukup lewat field `role` di `User.kt`/`Role.kt` — satu `LoginUseCase` dan `RegisterUserUseCase` yang sama, parameternya yang menyesuaikan.

## 5. Logical Data Model (Firestore Collections)

Diambil dari ERD resmi PRD. Firestore itu NoSQL, jadi relasi "FK" di bawah ini diimplementasikan sebagai field berisi ID dokumen dari collection lain.

**Collection: `school`**
| Field | Tipe | Ket |
|---|---|---|
| school_id | String | Document ID (PK) |
| school_name | String | |
| school_code | String | Kode unik untuk registrasi siswi (FR-12) |

**Collection: `user`**
| Field | Tipe | Ket |
|---|---|---|
| user_id | String | Document ID (PK) |
| name | String | |
| email | String | |
| role | String | `"remaja_putri"` \| `"uks"` |
| school_id | String | FK → `school` |

**Collection: `ttd_schedule`**
| Field | Tipe | Ket |
|---|---|---|
| schedule_id | String | Document ID (PK) |
| school_id | String | FK → `school` |
| date | Date | |
| time | Time | |

**Collection: `ttd_consumption`**
| Field | Tipe | Ket |
|---|---|---|
| consumption_id | String | Document ID (PK) |
| user_id | String | FK → `user` |
| schedule_id | String | FK → `ttd_schedule` |
| status | String | mis. `"confirmed"` / `"missed"` / `"pending"` |
| confirmed_at | DateTime | diisi saat siswi konfirmasi konsumsi |

**Collection: `education`** *(dibangun di FASE 2)*
| Field | Tipe | Ket |
|---|---|---|
| education_id | String | Document ID (PK) |
| title | String | |
| content | Text | |
| category | String | |

## 6. Scope Lock — Build Priority

> **Ini bagian paling penting di dokumen ini.** Progress 50% untuk babak penyisihan Gemastik dikunci pada scope FASE 1 di bawah. **Jangan mengerjakan FASE 2 sebelum seluruh FASE 1 selesai dan teruji.**

### 🟢 FASE 1 — Wajib (target 50%, babak penyisihan)
Urutan pengerjaan **mengikuti urutan dependency**, bukan bebas pilih:
1. **`core/`** — DI modules & utils harus jalan duluan, semua bergantung ke sini.
2. **`auth/`** — registrasi & login kedua role. Tanpa ini tidak ada yang bisa ditest end-to-end.
3. **`school/`** (FR-12) — School Code. Prasyarat mutlak registrasi siswi.
4. **`schedule/`** (FR-14) — UKS input jadwal. Prasyarat mutlak untuk reminder.
5. **`consumption/`** — **Hanya**: FR-04 (Reminder via FCM), FR-05 (Tracker), FR-06 (Riwayat). **FR-07 (Statistik individu) TIDAK termasuk FASE 1.**
6. **`monitoring/`** — **Hanya**: FR-15 (Daftar Siswi), FR-16 (Monitoring kepatuhan per siswi list). **FR-13 (Dashboard) dan FR-17 (Statistik sekolah) TIDAK termasuk FASE 1.**
7. **`user/` (profile)** (FR-03) — CRUD ringan.

### 🔴 FASE 2 — Ditunda ke babak final
- `consumption/` → FR-07 Statistik Kepatuhan individu.
- `monitoring/` → FR-13 Dashboard Monitoring visual, FR-17 Statistik Kepatuhan Sekolah.
- `education/` (seluruh folder) → FR-08 Modul Edukasi.

## 7. Functional Requirements

| Folder | Kode | Fitur | Aktor | Fase |
|---|---|---|---|---|
| auth | FR-01 | Registrasi Akun (pakai School Code) | Remaja Putri | 🟢 1 |
| auth | FR-02 | Login | Remaja Putri | 🟢 1 |
| user | FR-03 | Pengelolaan Profil | Remaja Putri | 🟢 1 |
| consumption | FR-04 | Reminder Konsumsi TTD (FCM) | Remaja Putri | 🟢 1 |
| consumption | FR-05 | Tracker Kepatuhan (konfirmasi minum) | Remaja Putri | 🟢 1 |
| consumption | FR-06 | Riwayat Konsumsi TTD | Remaja Putri | 🟢 1 |
| consumption | FR-07 | Statistik Kepatuhan (individu) | Remaja Putri | 🔴 2 |
| education | FR-08 | Modul Edukasi | Remaja Putri | 🔴 2 |
| auth | FR-09 | Logout | Remaja Putri | 🟢 1 |
| auth | FR-10 | Registrasi Akun UKS | UKS | 🟢 1 |
| auth | FR-11 | Login UKS | UKS | 🟢 1 |
| school | FR-12 | School Code (generate & kelola) | UKS | 🟢 1 |
| monitoring | FR-13 | Dashboard Monitoring (agregat visual) | UKS | 🔴 2 |
| schedule | FR-14 | Pengelolaan Jadwal Konsumsi TTD | UKS | 🟢 1 |
| monitoring | FR-15 | Daftar Siswi | UKS | 🟢 1 |
| monitoring | FR-16 | Monitoring Kepatuhan (per siswi) | UKS | 🟢 1 |
| monitoring | FR-17 | Statistik Kepatuhan Sekolah | UKS | 🔴 2 |
| auth | FR-18 | Logout | UKS | 🟢 1 |

## 8. Core Application Flow (Alur Kerja Utama)

**Alur Registrasi Remaja Putri:**
Buka app → Registrasi → masukkan School Code dari UKS → sistem validasi & hubungkan `user.school_id` ke `school` yang sesuai → akun aktif → Login.

**Alur Konsumsi TTD (inti aplikasi):**
UKS set `ttd_schedule` (tanggal & jam) → sistem trigger FCM sesuai jadwal → siswi terima notifikasi Reminder → siswi buka app → tekan tombol "Sudah Konsumsi" → sistem buat dokumen baru di `ttd_consumption` (status + confirmed_at) → data ini langsung muncul di Riwayat siswi DAN di Monitoring Kepatuhan UKS.

**Alur Monitoring UKS:**
Login UKS → lihat Daftar Siswi (dari `user` where `school_id` = sekolah UKS) → lihat status kepatuhan tiap siswi (query `ttd_consumption` per `user_id`) → *(Fase 2: lihat statistik & dashboard agregat)*.

## 9. Definition of Done — Fase 1 (Checklist 50%)
- [ ] Remaja Putri bisa registrasi pakai School Code yang valid.
- [ ] Remaja Putri & UKS bisa login/logout sesuai rolenya.
- [ ] UKS bisa membuat jadwal konsumsi TTD.
- [ ] Siswi menerima notifikasi reminder sesuai jadwal yang dibuat UKS.
- [ ] Siswi bisa konfirmasi "Sudah Konsumsi" dan data tersimpan di Firestore.
- [ ] Riwayat konsumsi siswi menampilkan data konfirmasi.
- [ ] UKS bisa melihat Daftar Siswi di sekolahnya.
- [ ] UKS bisa melihat status kepatuhan per siswi.
- [ ] Siswi bisa update profil dasar.
- [ ] Tidak ada pemanggilan Firebase langsung dari layer `presentation/`.

*Begitu checklist di atas selesai, hentikan pengembangan fitur baru. Fokus pada perbaikan bug untuk persiapan demo penyisihan.*

## 10. Konvensi Coding untuk AI Agent & Tim
- Setiap fitur baru **wajib** punya: `dto` → `repository interface` → `repository impl` → `usecase` → `screen + viewmodel + state`.
- DTO tidak boleh langsung dipakai di `presentation/` — selalu dikonversi ke Domain Model.
- Satu UseCase = satu tanggung jawab spesifik.
- Nama collection Firestore mengikuti persis nama entity (huruf kecil, snake_case): `school`, `user`, `ttd_schedule`, `ttd_consumption`, `education`.
- State per screen menggunakan pola `{Fitur}State.kt` seperti `AuthState.kt`.
- FCM bukan scheduler mutlak. Jika UKS set tanggal, gunakan `WorkManager`/`AlarmManager` di client, jangan asumsikan FCM bisa jalan sendiri.

## 11. Pembagian Kerja Tim (3 Orang) & Strategi Branching

**Langkah 0 — "Contract-First Day":** Seluruh tim wajib membuat dan menyepakati `domain/model/` dan interface di `domain/{fitur}/repository/` bersama-sama, dan push ke `develop`.

**Pembagian:**
- **Person 1:** `core/`, `auth/`, `school/` (Fondasi)
- **Person 2:** `consumption/` (Fase 1), `user/` (Squad Siswi)
- **Person 3:** `schedule/`, `monitoring/` (Fase 1) (Squad UKS)

**Strategi Branching (Git):**
- `main` → kode stabil/demo.
- `develop` → integrasi utama.
- `feature/{folder}-{deskripsi}` → branch berumur pendek. Commit format: `feat({folder}): deskripsi`.

## 12. Design System — Color Palette

| Token | Hex | Kegunaan |
|---|---|---|
| `primary` | `#D6336C` (Berry Rose) | Warna utama brand, CTA |
| `primaryLight` | `#FDE7ED` | Background kartu, tag |
| `secondary` | `#0F9B8E` (Teal) | Aksen sisi UKS/monitoring |
| `accent` | `#FFB020` (Amber) | Highlight reminder, streak |
| `success` | `#2E7D32` | Status "patuh/terkonfirmasi" |
| `warning` | `#F59E0B` | Status "menunggu konfirmasi" |
| `error` | `#DC2626` | Status "terlewat/gagal" |
| `background` | `#FFFBFA` | Latar aplikasi (putih hangat) |
| `surface` | `#FFFFFF` | Warna kartu |
| `textPrimary` | `#2B2730` | Teks utama |
| `textSecondary` | `#6B6570` | Teks sekunder |
| `border` | `#EDE3E6` | Divider |

*Catatan:* `ui/theme/Color.kt` diupdate menggunakan token ini. Gunakan `secondary` lebih dominan di sisi UKS (`monitoring/`, `schedule/`) dan `primary` dominan di sisi siswi.

## 13. What NOT to Build
- Puskesmas & akun Puskesmas
- Chat UKS-siswi, AI Chatbot
- Fitur orang tua
- Leaderboard / Gamifikasi kompleks
- Diagnosis anemia

*Do not expand the scope unless explicitly instructed.*
