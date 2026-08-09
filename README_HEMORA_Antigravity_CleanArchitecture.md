# HEMORA — Mobile App Development Brief

## 1. Project Overview
HEMORA adalah aplikasi mobile Android untuk membantu remaja putri meningkatkan kepatuhan konsumsi Tablet Tambah Darah (TTD).

**Roles:**
- Remaja Putri
- UKS (Guru/Unit Kesehatan Sekolah)

Satu aplikasi digunakan oleh kedua role dengan fitur dan hak akses berbeda.

**Scope lock:** Jangan menambahkan Puskesmas, orang tua, chat, AI, diagnosis anemia, gamifikasi, kelas, atau fitur lain di luar requirement.

## 2. Technology Stack
- Platform: Android
- Language: Kotlin
- UI: Jetpack Compose
- Architecture: Clean Architecture
- Backend: Firebase
- Authentication: Firebase Authentication
- Database: Cloud Firestore
- Notification: Firebase Cloud Messaging (FCM)
- Connectivity: Online / internet required

Aplikasi tidak ditargetkan untuk publik/Play Store pada tahap awal dan digunakan dalam lingkup sekolah tertentu.

## 3. Clean Architecture

HEMORA **WAJIB** menggunakan Clean Architecture dengan tiga layer:

### Presentation
Tanggung jawab:
- Jetpack Compose UI
- Screen
- UI State
- ViewModel
- User interaction
- Navigation

Presentation **tidak boleh mengakses Firebase secara langsung**.

Flow:
```text
UI / Screen
    ↓
ViewModel
    ↓
Use Case
```

### Domain
Tanggung jawab:
- Business logic
- Entity/domain model
- Use Case
- Repository interface

Domain tidak boleh bergantung langsung pada Firebase, Android framework, atau implementasi repository.

Contoh Use Case:
- RegisterUserUseCase
- LoginUseCase
- JoinSchoolUseCase
- CreateTtdScheduleUseCase
- ConfirmTtdConsumptionUseCase
- GetConsumptionHistoryUseCase
- GetUserStatisticsUseCase
- GetSchoolMonitoringUseCase

### Data
Tanggung jawab:
- Repository implementation
- Firebase Authentication
- Cloud Firestore
- Firebase Cloud Messaging
- Remote data source
- DTO/data model jika diperlukan
- Mapping data ke domain model

Dependency direction:
```text
Presentation → Domain
Data         → Domain
```

Jangan membuat:
```text
Composable → FirebaseFirestore
Composable → FirebaseAuth
ViewModel → Firestore SDK secara langsung
```

Gunakan:
```text
Composable
   ↓
ViewModel
   ↓
UseCase
   ↓
Repository Interface
   ↓
Repository Implementation
   ↓
Firebase
```

## 4. Feature Organization

Jangan membuat satu modul untuk setiap FR. Kelompokkan FR yang berkaitan.

Struktur yang disarankan:
```text
com.hemora
├── core/
│   ├── common/
│   ├── navigation/
│   ├── ui/
│   └── util/
├── data/
│   ├── auth/
│   ├── school/
│   ├── user/
│   ├── schedule/
│   ├── consumption/
│   └── education/
├── domain/
│   ├── auth/
│   ├── school/
│   ├── user/
│   ├── schedule/
│   ├── consumption/
│   └── education/
└── presentation/
    ├── auth/
    ├── home/
    ├── schedule/
    ├── consumption/
    ├── education/
    ├── profile/
    └── monitoring/
```

Feature mapping:
- Auth → FR-01, FR-02, FR-10, FR-11, FR-09, FR-18
- School → FR-12 dan proses School Code
- User/Profile → FR-03
- TTD Schedule → FR-14
- TTD Consumption → FR-04, FR-05, FR-06, FR-07
- Education → FR-08
- Monitoring → FR-13, FR-15, FR-16, FR-17

## 5. Development Principles
1. Jangan menambah fitur di luar requirement.
2. Utamakan core flow yang benar-benar berjalan.
3. Gunakan Clean Architecture.
4. UI tidak boleh mengakses Firebase secara langsung.
5. Business logic tidak boleh ditempatkan di Composable.
6. Repository interface berada di Domain; implementation berada di Data.
7. Pisahkan akses berdasarkan role `UKS` dan `REMAJA_PUTRI`.
8. Jangan membuat Puskesmas sebagai role atau entitas.
9. Remaja Putri melakukan registrasi mandiri menggunakan School Code.
10. Jangan menyimpan password sendiri di Firestore.
11. FCM bukan scheduler.
12. Jangan mengimplementasikan diagnosis atau rekomendasi medis.
13. Core flow tidak boleh hanya menggunakan dummy/local data.
14. Setelah setiap tahap, project harus tetap dapat di-build dan dijalankan.
15. Hindari over-engineering dan abstraction yang tidak diperlukan.

## 6. User Roles

### Remaja Putri
Fitur:
- Registrasi akun
- Login
- Pengelolaan profil
- Reminder Konsumsi TTD
- Konfirmasi konsumsi TTD
- Riwayat konsumsi
- Statistik/progress kepatuhan
- Modul edukasi
- Logout

Registrasi:
```text
Buka Registrasi
      ↓
Isi data akun
      ↓
Masukkan School Code
      ↓
Validasi School Code
      ↓
Akun berhasil dibuat
      ↓
Akun terhubung dengan sekolah
```

### UKS
Fitur:
- Registrasi akun UKS
- Login
- School Code
- Pengelolaan jadwal konsumsi TTD
- Dashboard Monitoring
- Daftar siswi
- Monitoring kepatuhan
- Statistik kepatuhan sekolah
- Logout

Scope awal:
> Satu sekolah diperuntukkan bagi satu akun UKS dan dapat memiliki banyak akun Remaja Putri.

## 7. Functional Requirements

| Kode | Aktor | Fitur |
|---|---|---|
| FR-01 | Remaja Putri | Registrasi Akun menggunakan School Code |
| FR-02 | Remaja Putri | Login |
| FR-03 | Remaja Putri | Pengelolaan Profil |
| FR-04 | Remaja Putri | Reminder Konsumsi TTD berdasarkan jadwal UKS |
| FR-05 | Remaja Putri | Konfirmasi konsumsi TTD / Tracker Kepatuhan |
| FR-06 | Remaja Putri | Riwayat Konsumsi TTD |
| FR-07 | Remaja Putri | Statistik Kepatuhan |
| FR-08 | Remaja Putri | Modul Edukasi |
| FR-09 | Remaja Putri | Logout |
| FR-10 | UKS | Registrasi Akun UKS |
| FR-11 | UKS | Login |
| FR-12 | UKS | School Code |
| FR-13 | UKS | Dashboard Monitoring |
| FR-14 | UKS | Pengelolaan Jadwal Konsumsi TTD |
| FR-15 | UKS | Daftar Siswi |
| FR-16 | UKS | Monitoring Kepatuhan |
| FR-17 | UKS | Statistik Kepatuhan Sekolah |
| FR-18 | UKS | Logout |

## 8. Core Application Flow

### UKS
```text
Registrasi UKS
      ↓
Terhubung dengan School
      ↓
Mendapatkan School Code
      ↓
Login
      ↓
Dashboard UKS
      ↓
Mengatur Jadwal Konsumsi TTD
      ↓
Melihat Daftar Siswi
      ↓
Monitoring Kepatuhan
      ↓
Statistik Sekolah
```

### Remaja Putri
```text
Registrasi dengan School Code
      ↓
Login
      ↓
Beranda
      ↓
Menerima Reminder
      ↓
Mengonsumsi TTD
      ↓
Konfirmasi "Sudah Konsumsi"
      ↓
Data tersimpan
      ↓
Riwayat diperbarui
      ↓
Statistik diperbarui
```

### Integrasi
```text
UKS membuat jadwal
        ↓
TTD Schedule
        ↓
Reminder
        ↓
Remaja Putri konsumsi TTD
        ↓
Konfirmasi konsumsi
        ↓
TTD Consumption
        ↓
┌───────────────┬────────────────────┐
↓               ↓                    ↓
Riwayat       Statistik       Dashboard UKS
```

## 9. Logical Data Model

### SCHOOL
```text
school_id : String (PK)
school_name : String
school_code : String
```

### USER
```text
user_id : String (PK)
name : String
email : String
role : String
school_id : String (FK)
```
Role: `UKS` atau `REMAJA_PUTRI`.

### TTD_SCHEDULE
```text
schedule_id : String (PK)
school_id : String (FK)
date : Date
time : Time
```

### TTD_CONSUMPTION
```text
consumption_id : String (PK)
user_id : String (FK)
schedule_id : String (FK)
status : String
confirmed_at : DateTime
```

### EDUCATION
```text
education_id : String (PK)
title : String
content : Text
category : String
```

Relationships:
```text
SCHOOL 1 : N USER
SCHOOL 1 : N TTD_SCHEDULE
USER 1 : N TTD_CONSUMPTION
TTD_SCHEDULE 1 : N TTD_CONSUMPTION
```

Education berdiri sendiri karena materi edukasi bersifat umum.

## 10. Suggested Firestore Structure

```text
schools/{schoolId}
  schoolName
  schoolCode

users/{userId}
  name
  email
  role
  schoolId

ttdSchedules/{scheduleId}
  schoolId
  date
  time

ttdConsumptions/{consumptionId}
  userId
  scheduleId
  status
  confirmedAt

education/{educationId}
  title
  content
  category
```

Struktur aktual boleh disesuaikan selama requirement dan relasi tetap konsisten.

## 11. Access Control

### Remaja Putri
Boleh:
- profil sendiri;
- jadwal sekolah sendiri;
- konfirmasi konsumsi sendiri;
- riwayat/statistik sendiri;
- materi edukasi.

Tidak boleh:
- data siswi lain;
- Dashboard UKS;
- mengubah jadwal;
- statistik sekolah keseluruhan.

### UKS
Hanya dapat mengakses data sekolahnya:
- mengelola jadwal;
- melihat siswi;
- melihat kepatuhan;
- melihat statistik sekolah.

Tidak boleh melihat data sekolah lain.

## 12. Main Screens

### Remaja Putri
```text
Splash / Loading
      ↓
Login / Register
      ↓
Home / Beranda
├── Reminder
├── Tracker / Konfirmasi
├── Riwayat
├── Statistik
├── Edukasi
└── Profil
```

### UKS
```text
Login / Register
      ↓
Dashboard UKS
├── Daftar Siswi
├── Jadwal TTD
├── Monitoring
├── Statistik Sekolah
├── School Code
└── Profil
```

## 13. UI/UX Guidelines

UI harus:
- sederhana;
- mudah dipahami remaja putri;
- jelas untuk UKS;
- hierarchy informasi jelas;
- label mudah dipahami;
- feedback aksi berhasil/gagal;
- status konsumsi jelas;
- tidak terlalu padat;
- konsisten antarhalaman.

Fokus UX:
> Remaja Putri dapat mengetahui kapan harus minum TTD dan mengonfirmasi konsumsi dengan cepat.

> UKS dapat mengetahui kondisi kepatuhan sekolah tanpa membuka terlalu banyak halaman.

## 14. Implementation Priority

### PRIORITAS 1 — Core
- FR-01
- FR-02
- FR-10
- FR-11
- FR-12
- FR-14
- FR-04
- FR-05
- FR-06
- FR-13
- FR-15
- FR-16
- FR-07
- FR-17

### PRIORITAS 2 — Pendukung
- FR-03
- FR-08
- FR-09
- FR-18

Semua FR tetap merupakan target aplikasi. Prioritas hanya menentukan urutan pengerjaan.

## 15. Definition of Done

Fitur dianggap selesai jika:
1. UI tersedia.
2. User dapat melakukan aksi utama.
3. Data tersimpan benar.
4. Data dapat dibaca kembali.
5. Role dan permission benar.
6. Error/invalid input ditangani.
7. Flow dapat didemonstrasikan tanpa manipulasi database manual.
8. Implementasi mengikuti layer Clean Architecture.

Contoh:
```text
Klik "Sudah Konsumsi"
        ↓
ViewModel
        ↓
ConfirmTtdConsumptionUseCase
        ↓
ConsumptionRepository
        ↓
Firestore
        ↓
Riwayat berubah
        ↓
Statistik berubah
        ↓
Data terlihat oleh UKS
```

## 16. Technical Notes

### Firebase Authentication
Gunakan Firebase Authentication untuk email/password, login state, dan logout. Jangan menyimpan password di Firestore.

### Cloud Firestore
Gunakan Firestore sebagai sumber data core. Jangan mengganti core backend dengan dummy/local data.

### FCM
FCM digunakan untuk pengiriman notifikasi.

**FCM bukan scheduler.** Jika UKS menentukan tanggal/waktu reminder, gunakan mekanisme scheduling/backend yang benar. Jangan mengasumsikan aplikasi Android yang tertutup dapat menjadi scheduler FCM sendiri.

## 17. What NOT to Build

Jangan implementasikan:
- Puskesmas
- akun/integrasi Puskesmas
- rumah sakit
- diagnosis anemia
- rekomendasi medis personal
- AI chatbot
- chat UKS-siswi
- fitur orang tua
- leaderboard
- gamifikasi kompleks
- sistem kelas
- pembayaran
- marketplace
- fitur sosial
- publikasi Play Store

## 18. Expected Demo Scenario

### UKS
1. Login sebagai UKS.
2. Membuka Dashboard.
3. Melihat School Code.
4. Membuat jadwal konsumsi TTD.
5. Melihat daftar siswi.
6. Membuka monitoring.
7. Melihat statistik.

### Remaja Putri
1. Registrasi menggunakan School Code.
2. Login.
3. Melihat jadwal/reminder.
4. Menerima reminder.
5. Mengonsumsi TTD.
6. Menekan "Sudah Konsumsi".
7. Melihat riwayat.
8. Melihat statistik.

### Integrasi
```text
UKS membuat jadwal
        ↓
Remaja Putri menerima reminder
        ↓
Konfirmasi konsumsi
        ↓
Data tersimpan
        ↓
Riwayat/statistik berubah
        ↓
Dashboard UKS berubah
```

## 19. Instructions for Antigravity

1. Baca README ini sebelum coding.
2. Jangan langsung membuat semua halaman.
3. Siapkan project Android dan Firebase.
4. Siapkan struktur Clean Architecture terlebih dahulu.
5. Implementasikan authentication dan role.
6. Implementasikan School dan School Code.
7. Implementasikan TTD Schedule.
8. Implementasikan Consumption/Tracker.
9. Implementasikan Riwayat dan Statistik.
10. Implementasikan Dashboard UKS.
11. Implementasikan Edukasi dan Profil.
12. Implementasikan notification flow.
13. Test antar-role setelah core flow selesai.
14. Jangan mengubah requirement tanpa alasan jelas.
15. Jika requirement ambigu, tandai dan tanyakan sebelum membuat asumsi besar.
16. Jangan menghapus requirement.
17. Build dan run project setelah setiap tahap.
18. Jangan mengganti core Firebase dengan mock data.
19. Jangan menaruh Firebase SDK call langsung di Composable.
20. Jangan menaruh business logic langsung di Composable.
21. ViewModel mengelola UI state dan memanggil Use Case.
22. Use Case menangani business operation yang relevan.
23. Repository Interface berada di Domain.
24. Repository Implementation berada di Data.
25. Hindari over-engineering.

## 20. Final Product Definition

HEMORA memiliki core implementation ketika:

> **UKS dapat menentukan jadwal konsumsi TTD → Remaja Putri yang terhubung ke sekolah menerima reminder → Remaja Putri melakukan konfirmasi konsumsi → sistem menyimpan data → riwayat dan statistik Remaja Putri diperbarui → UKS dapat melihat data kepatuhan melalui Dashboard Monitoring.**

Core flow harus benar-benar berjalan dan mengikuti Clean Architecture.

## 21. Current Scope Lock

```text
Platform       : Android
Language       : Kotlin
UI             : Jetpack Compose
Architecture   : Clean Architecture
Backend        : Firebase
Auth           : Firebase Authentication
Database       : Cloud Firestore
Notification   : Firebase Cloud Messaging
Connectivity   : Online / Internet Required
Roles          : UKS, REMAJA_PUTRI
School model   : School + School Code
Puskesmas      : OUT OF SCOPE
Public release : OUT OF SCOPE
```

**Do not expand the scope unless explicitly instructed.**
