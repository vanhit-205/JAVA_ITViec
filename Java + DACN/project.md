# File: `ANDROID_FRONTEND_REFACTOR_GUIDE.md`

````md
# HƯỚNG DẪN HOÀN THIỆN FRONTEND ANDROID JAVA THEO USECASE WORKHUB

## 1. Mục tiêu

Tài liệu này dùng để hoàn thiện và chỉnh sửa toàn bộ frontend Android Java của ứng dụng WorkHub theo đúng yêu cầu usecase trong file báo cáo đồ án.

Hiện tại:
- Backend API đã hoàn thành.
- Retrofit đã call API thành công.
- Một số màn hình và chức năng đã có.
- Tuy nhiên còn:
  - Sai logic usecase.
  - Thiếu màn hình.
  - Sai luồng điều hướng.
  - Thiếu validate.
  - Sai phân quyền.
  - UI chưa đúng yêu cầu.

Mục tiêu:
- Hoàn thiện toàn bộ giao diện Android.
- Sửa đúng logic usecase.
- Đồng bộ với API backend.
- Tối ưu trải nghiệm người dùng.
- Tách cấu trúc code sạch theo mô hình MVVM hoặc MVC.

---



# 3. Công nghệ sử dụng

## Bắt buộc

* Java Android
* Retrofit2
* Gson
* RecyclerView
* Glide/Picasso
* Material Design
* ViewBinding
* JWT Token
* SharedPreferences
* Navigation Component

## Khuyến khích

* MVVM
* LiveData
* Room Database
* Paging 3
* SwipeRefreshLayout

---

# 4. Danh sách chức năng cần hoàn thiện

| Chức năng                  | Trạng thái |
| -------------------------- | ---------- |
| Đăng nhập                  | Sửa logic  |
| Đăng ký                    | Hoàn thiện |
| Quên mật khẩu              | Thiếu      |
| Đăng xuất                  | Thiếu      |
| Cập nhật thông tin cá nhân | Sửa        |
| Danh sách công ty          | Sửa UI     |
| Chi tiết công ty           | Thiếu      |
| Danh sách việc làm         | Sửa        |
| Chi tiết việc làm          | Thiếu      |
| Tìm kiếm theo skill        | Thiếu      |
| Quản lý resume             | Thiếu      |
| Theo dõi trạng thái resume | Thiếu      |
| Quản lý user admin         | Thiếu      |
| Quản lý skill              | Thiếu      |
| Quản lý company            | Thiếu      |
| Quản lý job                | Thiếu      |
| Subscriber nhận mail       | Thiếu      |
| Phân quyền role            | Thiếu      |

---


---

# 6. Logic Authentication

## 6.1 Đăng nhập

### API

```http
POST /auth/login
```

### Yêu cầu

* Validate:

    * Email không rỗng
    * Password không rỗng
* Loading khi login
* Lưu:

    * accessToken
    * refreshToken
    * userInfo
* Điều hướng:

    * ADMIN → AdminActivity
    * HR → HrActivity
    * USER → MainActivity

### Nếu lỗi

* Sai tài khoản:

    * Snackbar lỗi
* Mất mạng:

    * Dialog retry

### Cần sửa

* Không hardcode role
* Không lưu token plain text
* Tự động login nếu còn token

---

## 6.2 Đăng ký

### API

```http
POST /auth/register
```

### Validate

* Email đúng format
* Password >= 6 ký tự
* Confirm password trùng
* Không bỏ trống field

### Sau đăng ký

* Hiện dialog thành công
* Quay về LoginActivity

---

## 6.3 Quên mật khẩu

### Màn hình cần tạo

```bash
ForgotPasswordActivity
OtpVerificationActivity
ResetPasswordActivity
```

### Flow

```text
Nhập Email
→ Gửi OTP
→ Xác thực OTP
→ Nhập mật khẩu mới
→ Thành công
```

### API

```http
POST /forgot-password
POST /verify-otp
POST /reset-password
```

---

## 6.4 Đăng xuất

### Yêu cầu

* Xóa:

    * token
    * cache
    * session
* Gọi API logout
* Quay về LoginActivity

---

# 7. Home Screen

## Chức năng

* Banner
* Danh sách job nổi bật
* Công ty nổi bật
* Search job
* Search skill
* Bottom navigation

## RecyclerView

```bash
FeaturedJobAdapter
CompanyAdapter
SkillAdapter
```

---

# 8. Chức năng Company

# 8.1 Danh sách công ty

## UI

### USER

* CardView dạng grid
* Logo
* Tên công ty
* Địa chỉ
* Số lượng job

### ADMIN

* Table/List
* CRUD

## API

```http
GET /companies
```

---

# 8.2 Chi tiết công ty

## Màn hình cần tạo

```bash
CompanyDetailActivity
```

## Hiển thị

* Logo
* Tên
* Địa chỉ
* Mô tả
* Danh sách job

## API

```http
GET /companies/{id}
```

---

# 9. Chức năng Job

# 9.1 Danh sách job

## UI USER

* RecyclerView card
* Tên job
* Salary
* Level
* Location
* Company

## UI ADMIN

* CRUD table

## API

```http
GET /jobs
```

---

# 9.2 Chi tiết job

## Màn hình cần tạo

```bash
JobDetailActivity
```

## Hiển thị

* Mô tả
* Skill yêu cầu
* Salary
* Level
* Quantity
* Deadline
* Company

## Button

```text
Apply Now
```

---

# 9.3 Tìm kiếm theo skill

## Màn hình cần tạo

```bash
SearchJobActivity
```

## Chức năng

* SearchView
* Filter skill
* Filter level
* Filter salary

## API

```http
GET /jobs/search
```

---

# 10. Resume Module

# 10.1 Apply Job

## Upload CV

* PDF/DOCX
* File picker
* Multipart Retrofit

## API

```http
POST /resumes
```

---

# 10.2 Quản lý resume cá nhân

## Màn hình

```bash
MyResumeActivity
```

## Hiển thị

* Tên job
* Trạng thái:

    * PENDING
    * REVIEWING
    * APPROVED
    * REJECTED
* Ngày apply

---

# 10.3 HR duyệt resume

## Màn hình

```bash
ResumeManagementActivity
```

## Chức năng

* Xem danh sách
* Approve
* Reject
* Reviewing

---

# 11. Skill Module

# 11.1 Danh sách skill

## UI

* ChipGroup
* RecyclerView

## API

```http
GET /skills
```

---

# 11.2 CRUD Skill (ADMIN)

## Màn hình

```bash
SkillManagementActivity
```

## Chức năng

* Add
* Edit
* Delete

---

# 12. User Management

# ADMIN ONLY

## Màn hình

```bash
UserManagementActivity
```

## Chức năng

* Xem danh sách user
* Search user
* Edit role
* Delete user
* Lock account

---

# 13. Subscriber Module

## Màn hình

```bash
SubscriptionActivity
```

## Chức năng

* Chọn skill
* Bật/tắt nhận mail
* Lưu subscriber

---

# 14. Role & Permission

## Yêu cầu

Ẩn/hiện chức năng theo role:

| Role  | Quyền              |
| ----- | ------------------ |
| ADMIN | Full               |
| HR    | Quản lý job/resume |
| USER  | Apply job          |

---

# 15. SharedPreferences

## Lưu

```java
TOKEN
REFRESH_TOKEN
USER_ID
ROLE
EMAIL
```

## Không lưu

* Password

---

# 16. Retrofit chuẩn

## Tạo interceptor JWT

```java
Authorization: Bearer TOKEN
```

## Auto refresh token

* Khi 401
* Gọi refresh token API

---

# 17. RecyclerView cần có

| Adapter           |
| ----------------- |
| JobAdapter        |
| CompanyAdapter    |
| SkillAdapter      |
| ResumeAdapter     |
| UserAdapter       |
| SubscriberAdapter |

---

# 18. Loading & Error

## Bắt buộc

* ProgressBar
* SwipeRefresh
* Retry button
* Empty state

---

# 19. Validate Form

## Email

```text
Đúng format email
```

## Password

```text
>= 6 ký tự
```

## Phone

```text
10 số
```

---

# 20. Animation

## Khuyến khích

* Lottie Loading
* RecyclerView animation
* BottomSheetDialog
* Fade transition

---

# 21. Các Activity cần có

```bash
SplashActivity
LoginActivity
RegisterActivity
ForgotPasswordActivity
OtpVerificationActivity
ResetPasswordActivity
MainActivity
HomeFragment
JobFragment
CompanyFragment
ProfileFragment
JobDetailActivity
CompanyDetailActivity
SearchJobActivity
MyResumeActivity
ResumeManagementActivity
UserManagementActivity
SkillManagementActivity
CompanyManagementActivity
JobManagementActivity
SubscriptionActivity
ProfileActivity
UpdateProfileActivity
ChangePasswordActivity
```

---

# 22. API cần đồng bộ

## Authentication

```http
/auth/login
/auth/register
/auth/logout
/auth/forgot-password
/auth/reset-password
```

## User

```http
/users
/users/{id}
```

## Company

```http
/companies
```

## Job

```http
/jobs
/jobs/search
```

## Skill

```http
/skills
```

## Resume

```http
/resumes
```

## Subscriber

```http
/subscribers
```

---

# 23. UI cần sửa theo usecase

## Login

* Thêm:

    * Quên mật khẩu
    * Đăng ký
* Validate realtime

## Register

* Confirm password
* Điều hướng login

## Home

* Search realtime
* Danh sách job nổi bật

## Profile

* Upload avatar
* Update info

## Job Detail

* Apply ngay
* Hiển thị skill dạng chip

---

# 24. Quy tắc coding

## Naming

### Class

```java
PascalCase
```

Ví dụ:

```java
LoginActivity
JobAdapter
```

### Variable

```java
camelCase
```

Ví dụ:

```java
userName
jobList
```

---

# 25. Mục tiêu cuối cùng

Sau khi hoàn thiện:

* App đúng toàn bộ usecase trong đồ án.
* Logic frontend đồng bộ backend.
* Có đầy đủ CRUD.
* Có phân quyền.
* Có upload CV.
* Có quản lý resume.
* Có subscriber mail.
* Có tìm kiếm job theo skill.
* UI chuyên nghiệp.
* Dễ demo bảo vệ đồ án.
* Dễ mở rộng về sau.

```
```
