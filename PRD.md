# PRD.md — HEMORA
> Dokumen acuan tunggal untuk AI coding agent & tim developer. Baca dokumen ini secara utuh sebelum mengerjakan fitur apa pun. Jangan membangun fitur di luar "FASE 1" tanpa instruksi eksplisit dari tim.

## 1. Ringkasan Proyek

**Nama Aplikasi:** HEMORA
**Masalah yang diselesaikan:** Rendahnya kepatuhan konsumsi Tablet Tambah Darah (TTD) pada remaja putri di Indonesia, akibat (a) distribusi TTD yang sangat bergantung pada kehadiran di sekolah, dan (b) rendahnya pemahaman remaja terhadap manfaat TTD.
**Solusi:** Aplikasi Android dua-peran — Remaja Putri (mengingat & mencatat konsumsi TTD) dan UKS/Guru (mengatur jadwal & memantau kepatuhan siswi di sekolahnya).
**Konteks:** Dikembangkan untuk Gemastik XIX 2026, Divisi Pengembangan Perangkat Lunak. Terkait SDG 2 (Zero Hunger — akses gizi mikro) dan SDG 3 (Good Health and Well-Being).
**Platform:** Android native.

---

## 2. Tech Stack (Wajib Diikuti)

| Komponen | Teknologi |
|---|---|
| Bahasa | Kotlin |
| UI Framework | Jetpack Compose |
| Arsitektur | Clean Architecture (Data → Domain → Presentation) |
| Dependency Injection | Hilt / Dagger |
| Backend & Auth | Firebase Authentication |
| Database | Cloud Firestore |
| Notifikasi | Firebase Cloud Messaging (FCM) |

**Aturan arsitektur (non-negotiable):**
- `domain/` **tidak boleh** punya dependency ke Android framework atau Firebase SDK sama sekali — murni Kotlin.
- `presentation/` **dilarang keras** memanggil Firebase secara langsung. Semua akses data lewat UseCase di `domain/`.
- Alur dependency selalu satu arah: `presentation → domain ← data`. `data/` mengimplementasikan interface yang didefinisikan di `domain/`, bukan sebaliknya.
- Setiap fitur baru mengikuti pola folder yang sama persis seperti `auth/` (lihat Bagian 3) — copy-paste struktur, ganti nama sesuai fitur.

---

## 3. Struktur Folder & Status Saat Ini

```
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

**Fitur yang BELUM dibuat foldernya** (akan mengikuti pola identik di atas: `data/{fitur}/dto`, `data/{fitur}/repository`, `domain/{fitur}/repository`, `domain/{fitur}/usecase`, `presentation/{fitur}/`):

```
school/        → School Code (FR-12)
schedule/       → Jadwal Konsumsi TTD (FR-14)
consumption/     → Reminder, Tracker, Riwayat, Statistik individu (FR-04, 05, 06, 07)
monitoring/      → Dashboard UKS, Daftar Siswi, Monitoring, Statistik sekolah (FR-13, 15, 16, 17)
education/       → Modul Edukasi (FR-08)
user/ (profile/)  → Kelola Profil (FR-03)
```

**Catatan penting:** folder `auth/` yang sudah ada harus melayani **kedua role** (Remaja Putri & UKS), bukan dibuat modul auth terpisah untuk UKS. Bedanya cukup lewat field `role` di `User.kt`/`Role.kt` — satu `LoginUseCase` dan `RegisterUserUseCase` yang sama, parameternya yang menyesuaikan.

---

## 4. Data Model (Firestore Collections)

Diambil dari ERD resmi PRD. Firestore itu NoSQL, jadi relasi "FK" di bawah ini diimplementasikan sebagai field berisi ID dokumen dari collection lain (bukan foreign key relasional).

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

**Relasi:** `school` 1—N `user`, `school` 1—N `ttd_schedule`, `user` 1—N `ttd_consumption`, `ttd_schedule` 1—N `ttd_consumption`.

---

## 5. Scope Lock — Build Priority

> **Ini bagian paling penting di dokumen ini.** Progress 50% untuk babak penyisihan Gemastik dikunci pada scope FASE 1 di bawah. **Jangan mengerjakan FASE 2 sebelum seluruh FASE 1 selesai dan teruji.**

### 🟢 FASE 1 — Wajib (target 50%, babak penyisihan)

Urutan pengerjaan **mengikuti urutan dependency**, bukan bebas pilih:

1. **`core/`** — DI modules & utils harus jalan duluan, semua bergantung ke sini.
2. **`auth/`** (data/domain/presentation) — registrasi & login kedua role. Tanpa ini tidak ada yang bisa ditest end-to-end.
3. **`school/`** (FR-12) — School Code. Prasyarat mutlak agar FR-01 (registrasi siswi) benar-benar bisa menghubungkan siswi ke sekolahnya.
4. **`schedule/`** (FR-14) — UKS input jadwal. Prasyarat mutlak untuk FR-04 (reminder butuh jadwal untuk tahu kapan harus mengingatkan).
5. **`consumption/`** — tapi **hanya bagian ini**: FR-04 (Reminder via FCM), FR-05 (Tracker/konfirmasi konsumsi), FR-06 (Riwayat konsumsi). **FR-07 (Statistik individu) TIDAK termasuk FASE 1.**
6. **`monitoring/`** — tapi **hanya bagian ini**: FR-15 (Daftar Siswi), FR-16 (Monitoring kepatuhan per siswi, versi list/status sederhana). **FR-13 (Dashboard visual agregat) dan FR-17 (Statistik sekolah) TIDAK termasuk FASE 1.**
7. **`user/` (profile)** (FR-03) — CRUD ringan, bisa dikerjakan paralel kapan saja setelah auth selesai.

### 🔴 FASE 2 — Ditunda ke babak final

- `consumption/` → FR-07 Statistik Kepatuhan individu (agregasi/visualisasi dari data FR-05/06 yang sudah terkumpul)
- `monitoring/` → FR-13 Dashboard Monitoring versi visual penuh, FR-17 Statistik Kepatuhan Sekolah
- `education/` (seluruh folder) → FR-08 Modul Edukasi — berdiri sendiri dari alur inti, aman ditunda

**Alasan pemisahan:** fitur FASE 2 semuanya adalah lapisan analitik/konten yang secara teknis membaca data yang **sudah harus ada dulu** dari FASE 1 — urutan ini bukan cuma soal prioritas, tapi memang keharusan teknis (statistik tidak bisa dibangun sebelum ada data konsumsi yang tercatat).

---

## 6. Functional Requirements per Folder

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

---

## 7. Non-Functional Requirements (berlaku sebagai acceptance criteria)

| Kode | Requirement | Wajib di Fase 1? |
|---|---|---|
| NFR-01 | Platform Android | ✅ Ya |
| NFR-02 | Butuh koneksi internet (auth, sync, FCM) | ✅ Ya |
| NFR-03 | UI sederhana & mudah dipahami kedua role | ✅ Ya |
| NFR-04 | Waktu respons memadai (login, konfirmasi, riwayat) | ⚠️ Boleh belum optimal |
| NFR-05 | Autentikasi & pembatasan akses sesuai role | ✅ Ya — tidak bisa ditawar |
| NFR-06 | Privasi data sesuai hak akses masing-masing role | ✅ Ya — tidak bisa ditawar |
| NFR-07 | Konsistensi data antara sisi siswi & UKS | ✅ Ya — tidak bisa ditawar |
| NFR-08 | Ketersediaan sistem selama ada koneksi internet | ⚠️ Boleh belum teruji skala besar |

---

## 8. Alur Kerja Utama (Core User Flows)

**Alur Registrasi Remaja Putri:**
Buka app → Registrasi → masukkan School Code dari UKS → sistem validasi & hubungkan `user.school_id` ke `school` yang sesuai → akun aktif → Login.

**Alur Konsumsi TTD (inti aplikasi):**
UKS set `ttd_schedule` (tanggal & jam) → sistem trigger FCM sesuai jadwal → siswi terima notifikasi Reminder → siswi buka app → tekan tombol "Sudah Konsumsi" → sistem buat dokumen baru di `ttd_consumption` (status + confirmed_at) → data ini langsung muncul di Riwayat siswi DAN di Monitoring Kepatuhan UKS.

**Alur Monitoring UKS:**
Login UKS → lihat Daftar Siswi (dari `user` where `school_id` = sekolah UKS) → lihat status kepatuhan tiap siswi (query `ttd_consumption` per `user_id`) → *(Fase 2: lihat statistik & dashboard agregat)*.

---

## 9. Konvensi Coding untuk AI Agent

- Setiap fitur baru **wajib** punya: `dto` (data/) → `repository interface` (domain/) → `repository impl` (data/) → `usecase` (domain/) → `screen + viewmodel + state` (presentation/).
- DTO tidak boleh langsung dipakai di `presentation/` — selalu dikonversi ke Domain Model (`User.kt`, dst) dulu.
- Satu UseCase = satu tanggung jawab spesifik (contoh: `ConfirmConsumptionUseCase`, bukan `ConsumptionUseCase` yang menangani banyak hal sekaligus).
- Nama collection Firestore mengikuti persis nama entity ERD (huruf kecil, snake_case): `school`, `user`, `ttd_schedule`, `ttd_consumption`, `education`.
- State per screen menggunakan pola `{Fitur}State.kt` seperti `AuthState.kt` yang sudah ada — replikasi pola ini untuk state loading/success/error (`Resource.kt` di `core/utils/`).

---

## 10. Definition of Done — Fase 1 (Checklist 50%)

- [ ] Remaja Putri bisa registrasi pakai School Code yang valid dan otomatis terhubung ke sekolah yang benar
- [ ] Remaja Putri & UKS bisa login/logout dengan role masing-masing terdeteksi benar
- [ ] UKS bisa membuat jadwal konsumsi TTD
- [ ] Siswi menerima notifikasi reminder sesuai jadwal yang dibuat UKS (FCM benar-benar terkirim, bukan cuma UI dummy)
- [ ] Siswi bisa konfirmasi "Sudah Konsumsi" dan data tersimpan di Firestore
- [ ] Riwayat konsumsi siswi menampilkan data yang sesuai dengan konfirmasi yang sudah dilakukan
- [ ] UKS bisa melihat Daftar Siswi yang terhubung ke sekolahnya
- [ ] UKS bisa melihat status kepatuhan per siswi (data yang sama dengan yang dikonfirmasi siswi — konsistensi data terjaga)
- [ ] Siswi bisa update profil dasar
- [ ] Tidak ada pemanggilan Firebase langsung dari layer `presentation/`

---

## 11. Pembagian Kerja Tim (3 Orang) & Strategi Branching

**Kenapa Clean Architecture penting di sini:** karena `presentation/` hanya bergantung ke interface di `domain/` (bukan ke `data/` secara langsung), tiga orang bisa kerja paralel tanpa saling tunggu — asal kontrak (interface + model) disepakati di awal.

### Langkah 0 — "Contract-First Day" (dikerjakan bertiga, sebelum split)
Sebelum split kerja, seluruh tim **wajib** duduk bareng untuk membuat dan menyepakati bersama, khusus untuk seluruh fitur FASE 1:
- `domain/model/` (User.kt, Role.kt, dan model untuk School, Schedule, Consumption)
- `domain/{fitur}/repository/` — semua interface repository (belum implementasinya)

Commit ini di-push ke branch `develop` duluan sebagai fondasi bersama. Setelah ini selesai, baru split ke 3 orang di bawah — karena interface sudah disepakati, tidak akan ada "rebut nama field" di tengah jalan.

### Pembagian Folder per Orang

| Orang | Fokus | Folder yang Dipegang | Kode FR (Fase 1) |
|---|---|---|---|
| **Person 1 — Fondasi & Auth** | Membangun duluan, "membuka jalan" untuk 2 orang lain | `core/`, `auth/` (2 role), `school/` | FR-01, 02, 09, 10, 11, 12, 18 |
| **Person 2 — Squad Siswi** | Sisi Remaja Putri | `consumption/` (bagian Fase 1 saja), `user/` (profile) | FR-03, 04, 05, 06 |
| **Person 3 — Squad UKS** | Sisi UKS | `schedule/`, `monitoring/` (bagian Fase 1 saja) | FR-14, 15, 16 |

**Catatan dependency yang perlu diantisipasi:** `consumption/` (Person 2) butuh baca data dari `schedule/` (Person 3) untuk tahu kapan reminder harus muncul (FR-04). Karena interface `ScheduleRepository` sudah disepakati bareng di Langkah 0, Person 2 **tidak perlu menunggu** Person 3 selesai — cukup pakai fake/dummy implementation dulu (`FakeScheduleRepository` yang return data contoh), lalu tinggal ganti ke implementasi asli lewat Hilt begitu Person 3 selesai. Ini alasan utama kenapa Langkah 0 tidak boleh dilewati.

**Urutan merge yang disarankan:** Person 1 (`auth` + `school`) idealnya selesai & merge ke `develop` paling cepat, karena `user.school_id` dan `Role` dipakai oleh dua fitur lainnya untuk masuk akal secara konteks (misalnya `monitoring` butuh query siswi `where school_id = ...`).

### Strategi Branching (Git)

```
main        → hanya kode yang sudah stabil/siap demo
develop     → branch integrasi utama, semua fitur bermuara ke sini
feature/*   → satu branch per potongan kerja
```

Penamaan branch: `feature/{folder}-{deskripsi-singkat}`, contoh:
- `feature/auth-register-login`
- `feature/school-code`
- `feature/consumption-reminder`
- `feature/consumption-tracker`
- `feature/schedule-crud`
- `feature/monitoring-daftar-siswi`

**Aturan kerja:**
1. Branch dari `develop`, bukan dari `main`.
2. Buat branch **kecil dan berumur pendek** — 1 UseCase/1 Screen selesai, langsung PR, jangan menumpuk banyak fitur di satu branch besar (biar konflik merge kecil).
3. Sebelum PR di-merge, cek 2 hal wajib: (a) tidak ada Firebase call langsung di `presentation/`, (b) build berhasil tanpa error.
4. Commit message pakai format `feat({folder}): deskripsi` atau `fix({folder}): deskripsi`, contoh: `feat(consumption): tambah tombol konfirmasi konsumsi TTD`.

---

## 12. Konteks Progress 50% — Definisi "Selesai" untuk Fase Ini

Perlu dipahami AI Agent maupun tim: **50% ini adalah target resmi dari panitia Gemastik untuk babak penyisihan**, bukan angka perkiraan kasar. Artinya:

- **Target yang harus dicapai = seluruh isi Bagian 5 (🟢 FASE 1) dan checklist di Bagian 10 (Definition of Done)** — bukan "separuh dari setiap fitur", tapi fitur-fitur tertentu yang selesai **penuh** dan bisa didemokan end-to-end.
- Begitu semua checklist di Bagian 10 tercentang, **hentikan pengembangan fitur baru**. Fokus berikutnya adalah stabilisasi, perbaikan bug, dan kesiapan demo — **bukan** mulai mengerjakan folder `education/`, `FR-07`, `FR-13`, atau `FR-17` (itu semua FASE 2).
- Boleh menyiapkan interface/model kosong untuk FASE 2 kalau memang membantu struktur (misalnya interface `EducationRepository` didefinisikan tapi belum diimplementasikan), tapi **jangan** membangun UI atau logika bisnis penuh untuk fitur FASE 2 di titik ini — itu buang waktu tim yang seharusnya dipakai memastikan FASE 1 benar-benar solid dan stabil untuk dinilai juri.
- Progress 50% ini adalah milestone **penyisihan**, bukan akhir proyek — kalau tim lolos ke babak final, dokumen ini akan direvisi untuk membuka scope FASE 2. AI Agent tidak perlu mengantisipasi itu sekarang.

---

## 13. Design System — Color Palette

Tema warna dipilih berdasarkan konteks aplikasi: isu darah/zat besi (Fe) memberi alasan kuat memakai warna merah-keunguan (berry) sebagai warna utama — tapi dibuat lebih hangat/lembut supaya tidak terkesan "peringatan medis darurat", karena target pengguna utama adalah remaja SMP/SMA. Warna kedua (teal) dipakai untuk sisi UKS supaya terasa lebih "dashboard/terpercaya", tanpa keluar dari satu identitas visual yang sama.

| Token | Hex | Kegunaan |
|---|---|---|
| `primary` | `#D6336C` (Berry Rose) | Warna utama brand, tombol CTA utama (mis. "Sudah Konsumsi"), ikon aktif |
| `primaryLight` | `#FDE7ED` | Background kartu/section ringan bernuansa primary, tag/badge |
| `secondary` | `#0F9B8E` (Teal) | Aksen sisi UKS/monitoring, tombol sekunder |
| `accent` | `#FFB020` (Amber) | Highlight reminder, streak/gamifikasi, notifikasi belum dibaca |
| `success` | `#2E7D32` | Status "patuh/terkonfirmasi" |
| `warning` | `#F59E0B` | Status "menunggu konfirmasi" |
| `error` | `#DC2626` | Status "terlewat/gagal", validasi form gagal |
| `background` | `#FFFBFA` | Latar belakang utama aplikasi (putih hangat, bukan putih dingin) |
| `surface` | `#FFFFFF` | Warna kartu/komponen di atas background |
| `textPrimary` | `#2B2730` | Teks utama |
| `textSecondary` | `#6B6570` | Teks sekunder/caption |
| `border` | `#EDE3E6` | Divider, outline input |

**Catatan implementasi:** siap langsung dipetakan ke `ui/theme/Color.kt` yang sudah auto-generated — tinggal ganti value default Compose dengan token di atas, dan gunakan `secondary` (teal) secara lebih dominan di layar-layar folder `monitoring/` & `schedule/` (sisi UKS) sementara `primary` (berry) lebih dominan di layar-layar folder `consumption/` & `auth/` (sisi siswi) agar ada sedikit pembedaan nuansa antar role tanpa memutus identitas visual.
