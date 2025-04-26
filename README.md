# 📚 Davon Library System

This repository contains the **Davon Library System**, a simple web-based library management platform.

---

## 🚀 Project Structure

```
davon-library-system/
🔼️ frontend/
🔼️ backend/
🔼️ README.md
```

---

## 🔀 Git Workflow – GitFlow Strategy

We are using the **GitFlow** methodology for version control and feature development.  
Here's how it works 👇

### 🌱 Main Branches

| Branch | Purpose |
|--------|---------|
| `main` | Production-ready code (releases only) |
| `develop` | Ongoing development, integrated features |

---

### 🌿 Supporting Branches

| Branch Type | Naming Convention | Purpose |
|-------------|--------------------|---------|
| Feature     | `feature/<feature-name>` | New features |
| Bugfix      | `bugfix/<issue-name>` | Minor bug fixes |
| Release     | `release/<version>` | Pre-release final testing |
| Hotfix      | `hotfix/<fix-name>` | Emergency fixes on `main` |

---

## 🧱 Example GitFlow Usage

### 🔧 Creating a Feature

```bash
git checkout develop
git checkout -b feature/add-book-form
```

### 💬 Commit Messages (Conventional Commits)

```bash
feat: add book form component
fix: handle empty input error
docs: update README with new feature
```

### 📤 Creating a Pull Request

- Base: `develop`
- Title: `✨ Add Book Form Feature`
- Description:
  ```markdown
  ### ✨ Summary
  - Implemented book form UI
  - Connected to API
  - Added basic validation
  ```

---

## ✍️ Contributing Guide

1. Fork the repository 🕱️
2. Clone your fork:
   ```bash
   git clone git@github.com:your-username/davon-library-system.git
   ```
3. Set upstream (optional but helpful):
   ```bash
   git remote add upstream git@github.com:davon-org/davon-library-system.git
   ```
4. Create a feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
5. Commit & push:
   ```bash
   git add .
   git commit -m "feat: short description"
   git push origin feature/your-feature-name
   ```

6. Open a Pull Request ✅

---

## 🌿 Versioning

- We use **semantic versioning** (`MAJOR.MINOR.PATCH`)
- Latest release: `v1.0.0` (example)

---

## 📮 Contact

For feedback or collaboration, reach out via [GitHub Issues](https://github.com/tanaksudavon/davon-library-system/issues) or open a pull request.

---

> Made with 💙 by Tan Aksu


